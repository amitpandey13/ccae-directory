package com.pdgc.general.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;

/**
 * JobManager
 */
public final class JobManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);
    private static int maxRunningJobs = 50;
    private static int maxRunningMbAllowed = 32000;

    private static final Object jobLock = new Object();
    private static final ConcurrentMap<UUID, JobInfo> runningJobs = new ConcurrentHashMap<>();

    private JobManager() {

    }

    public static void instantiateProperties() throws IOException {
        File file = Constants.retrieveExternalPropertyFile("Constants.properties");
        Properties prop = new Properties();
        if (file.exists()) {
            // The property file at the same location as jar takes higher precedence
            try (final FileInputStream fileInput = new FileInputStream(file)) {
                prop.load(fileInput);
            } catch (IOException ex) {
                LOGGER.error("Fail to load constant property file! {}", ex.getMessage());
            }
        } else {
            try (final InputStream resourceAsStream = Constants.class.getClassLoader().getResourceAsStream("Constants.properties")) {
                prop.load(resourceAsStream);
            } catch (IOException ex) {
                LOGGER.error("Fail to load constant property file! {}", ex.getMessage());
            }
        }

        //the following does not use the properties file
        maxRunningJobs = Integer.valueOf(prop.getProperty("MAX_RUNNING_JOBS", "10"));
        maxRunningMbAllowed = Integer.valueOf(prop.getProperty("MAX_RUNNING_MB_ALLOWED", "12000"));

        JobType.instantiateAllotments(prop);
    }

    /**
     * Attempts to reserve memory or the job type.
     * If this fails, then it throws a MemoryReservationException without running the job
     * If the memory reservation is successful, then the callable job is run and its results returned.
     * <p>
     * The expectation is that the caller will catch and swallow the MemoryReservationException
     * if the intent is to keep trying until the job is executed
     *
     * @param jobType
     * @param job
     * @param timeout - number of milliseconds to wait while trying to reserve memory.
     *                Values less than or equal to 0 are interpreted as no wait
     * @return
     * @throws MemoryReservationException - attempt to reserve memory for the specific job type failed
     * @throws Exception                  - whatever exception the callable job ends up throwing
     */
    public static <T> T runJob(
        JobType jobType,
        Callable<T> job,
        long timeout,
        Long runId,
        Long remoteJobId
    ) throws MemoryReservationException, Exception {    //NOPMD Callable requires Exception to be thrown
        UUID guid = reserveMemory(jobType, timeout, runId, remoteJobId);

        try {
            return job.call();
        } finally {
            LOGGER.info("RunId {} RemoteJobId {} Unreserving memory for jobType {} that takes up {} mb",
                runId, remoteJobId, jobType.name(), jobType.getMemoryAllotment());
            unreserveMemory(guid);
        }
    }

    /**
     * Attempts to reserve memory for the specific job type. Returns null if the reservation failed for whatever reason
     * The expectation is that anything that calls reserveMemory will eventually call a unreserveMemory() after it's finished whatever it's doing
     *
     * @param jobType
     * @param timeout
     * @return
     */
    public static UUID reserveMemory(
        JobType jobType,
        long timeout,
        Long runId,
        Long remoteJobId
    ) throws MemoryReservationException {
        UUID guid = UUID.randomUUID();
        try {
            while (!isMemoryAvailable(jobType, guid, runId, remoteJobId)) {
                if (timeout <= 0) {
                    throw new MemoryReservationException();
                } else {
                    LOGGER.info("RunId {} RemoteJobId {} of jobType {} that takes up {} mb now must wait for his turn....",
                        runId, remoteJobId, jobType.name(), jobType.getMemoryAllotment());
                    jobLock.wait(timeout);
                    LOGGER.info("RunId {} RemoteJobId {} of jobType {} that takes up {} mb woke up!",
                        runId, remoteJobId, jobType.name(), jobType.getMemoryAllotment());
                }
            }
        } catch (InterruptedException e) {
            throw new MemoryReservationException();     //NOPMD
        }

        return guid;
    }

    /**
     * Calculates whether or not there is enough memory available by looking at the runningJobs,
     * and, if the memory is available, reserves the memory
     *
     * @param jobType
     * @param guid
     * @return
     */
    private static boolean isMemoryAvailable(JobType jobType, UUID guid, Long runId, Long remoteJobId) {
        synchronized (jobLock) {
            int usedMemory = 0;
            int numJobs = 0;
            StringBuilder runningjobInfos = new StringBuilder("RunId " + runId + " RemoteJob");
            for (JobInfo job : runningJobs.values()) {
                usedMemory += job.jobType.getMemoryAllotment();
                ++numJobs;
                runningjobInfos.append(" " + job.remoteJobId + " |");
            }

            if (numJobs < maxRunningJobs && usedMemory < maxRunningMbAllowed) {
                runningJobs.put(guid, new JobInfo(jobType, runId, remoteJobId));
                return true;
            } else {
                LOGGER.info("Not enough memory because " + runningjobInfos.substring(0, runningjobInfos.length() - 2) + " are running");
                LOGGER.info("RunId {} RemoteJobId {} of jobType {} requires {} mb but there are currently: \r\n "
                        + "{} total jobs taking up {} total mb memory.. but {} max jobs allowed OR {} max running mb allowed",
                    runId, remoteJobId, jobType.name(), jobType.getMemoryAllotment(), numJobs, usedMemory, maxRunningJobs, maxRunningMbAllowed);
            }
            LOGGER.debug("Current runningJobs {} taking up a total of {} mb", runningJobs.values(), usedMemory);
        }

        //Only attempt a cleanup of dead jobs if the original attempt to reserve memory failed...and keep going until there was no memory release
        if (releaseDeadJobs()) {
            return isMemoryAvailable(jobType, guid, runId, remoteJobId);
        }
        return false;
    }

    private static void unreserveMemory(UUID guid) {
        synchronized (jobLock) {
            LOGGER.info("Successfully removed a running Job? {}", runningJobs.remove(guid));
            jobLock.notifyAll();
        }
    }

    private static boolean releaseDeadJobs() {
        boolean releasedDeadMemory = false;
        synchronized (jobLock) {
            Map<JobType, LocalDateTime> oldestAllowableJobMap = new HashMap<>();
            for (JobType jobType : JobType.values()) {
                oldestAllowableJobMap.put(jobType, DateTimeUtil.getUTCNow().minusMinutes(jobType.getTimeAllotment()));
            }

            //Unreserve memory used by too-old jobs
            Collection<Entry<UUID, JobInfo>> expiredLargeAvails = CollectionsUtil.where(
                runningJobs.entrySet(),
                kv -> kv.getValue().startTime.isBefore(oldestAllowableJobMap.get(kv.getValue().jobType))
            );

            for (Entry<UUID, JobInfo> entry : expiredLargeAvails) {
                LOGGER.warn("Releasing dead job because it surpassed time limit: {}", runningJobs.get(entry.getKey()));
                releasedDeadMemory = true;
                unreserveMemory(entry.getKey());
            }
        }

        return releasedDeadMemory;
    }

    /**
     * JobInfo is an intermediary object to hold job information
     */
    private static class JobInfo {
        public JobType jobType;
        public LocalDateTime startTime;
        public Long runId;
        public Long remoteJobId;

        /**
         * JobInfo constructor
         *
         * @param jobType
         * @param runId
         * @param remoteJobId
         */
        public JobInfo(JobType jobType, Long runId, Long remoteJobId) {
            this.jobType = jobType;
            this.runId = runId;
            this.remoteJobId = remoteJobId;
            this.startTime = DateTimeUtil.getUTCNow();
        }

        @Override
        public String toString() {
            return "[" + jobType + ": " + startTime + "]";
        }
    }
}
