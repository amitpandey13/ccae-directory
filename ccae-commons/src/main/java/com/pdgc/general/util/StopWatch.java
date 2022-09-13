package com.pdgc.general.util;

/**
 * Custom implementation of stop watch to also provide split times without having to track the intermediate split start/end times
 * externally.
 * 
 * @author Vishal Raut
 */
public class StopWatch {

	private long startTime;
	private long splitStartTime;
	private long splitEndTime;
	private long stopTime;
	private long heapSize;
	private long heapMaxSize;
	private long heapFreeSize;
	private State state;

	private enum State {
		IDLE, STARTED, SPLIT, STOPPED
	};

	public StopWatch() {
		state = State.IDLE;
	}

	/**
	 * Creates a new {@link StopWatch} which is already started
	 * 
	 * @return new {@link StopWatch}
	 */
	public static StopWatch newStarted() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		return stopWatch;
	}

	/**
	 * Start the watch
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		splitStartTime = 0;
		splitEndTime = 0;
		stopTime = 0;
		state = State.STARTED;
		captureMemoryStats();		
	}

	/**
	 * <p>
	 * Split the time. The {@link #getSplitTime()} method will then return the time taken by last split
	 * </p>
	 * <p>
	 * Once stopped, {@link #split()} will not have any effect unless the watch is started again using {@link #start()}
	 * </p>
	 * 
	 * @throws IllegalStateException
	 *             if stop watch was never started
	 */
	public String split() {
		if (state == State.IDLE) {
			throw new IllegalStateException("StopWatch must be started first");
		}
		if (state == State.STARTED || state == State.SPLIT) {
			splitStartTime = (splitEndTime == 0 ? startTime : splitEndTime);
			splitEndTime = System.currentTimeMillis();
		}
		//Capture the memory stats when the clock is stopped;
		captureMemoryStats();
		return logTimeAndMemory();
	}

	/**
	 * <p>
	 * Stop the watch.
	 * </p>
	 * <p>
	 * Once stopped, {@link #stop()} will not have any effect unless the watch is started again using {@link #start()}
	 * </p>
	 * 
	 * @throws IllegalStateException
	 *             if stop watch was never started
	 */
	public void stop() {
		if (state == State.IDLE) {
			throw new IllegalStateException("StopWatch must be started first");
		}
		if (state == State.STARTED || state == State.SPLIT) {
			stopTime = System.currentTimeMillis();
		}
		//Capture the memory stats when the clock is stopped;
		captureMemoryStats();		
		
	}

	/**
	 * Returns the time when watch was last started
	 * 
	 * @return the time in milliseconds
	 * @throws IllegalStateException
	 *             if stop watch was never started
	 */
	public long getStartTime() {
		if (state == State.IDLE) {
			throw new IllegalStateException("StopWatch must be started first");
		}
		return startTime;
	}

	/**
	 * Returns the time taken by last split. It is either the time between {@link #start()} and {@link #split()} if the split was called
	 * only once, otherwise it is the time between last two {@link #split()}'s.
	 * 
	 * @return the time in milliseconds
	 * @throws IllegalStateException
	 *             if stop watch was never split
	 */
	public long getSplitTime() {
		if (splitEndTime == 0) {
			throw new IllegalStateException("StopWatch must be split first");
		}
		return splitEndTime - splitStartTime;
	}

	/**
	 * If the watch was stopped, then it returns the time between stop and start, otherwise it returns the time elapsed since start.
	 * 
	 * @return the time in milliseconds
	 */
	public long getTime() {
		if (state == State.IDLE) {
			throw new IllegalStateException("StopWatch must be started first");
		}
		return (stopTime == 0 ? System.currentTimeMillis() : stopTime) - startTime;
	}

	/**
	 * Indicates if the stop watch is started and currently running.
	 * 
	 * @return <code>true</code> if StopWatch is started, <code>false</code> otherwise.
	 */
	public boolean isStarted() {
		return state == State.STARTED || state == State.SPLIT;
	}

	/**
	 * Indicates if the stop watch is stopped.
	 * 
	 * @return <code>true</code> if StopWatch is stopped, <code>false</code> otherwise.
	 */
	public boolean isStopped() {
		return state == State.STOPPED;
	}
	
	/**
	 * Capture the heap size in mega bytes
	 */
	private void captureMemoryStats() {
		long mega = 1000000l;
		heapSize = Runtime.getRuntime().totalMemory() / mega;
		heapFreeSize = Runtime.getRuntime().freeMemory() / mega;
		heapMaxSize = Runtime.getRuntime().maxMemory() / mega;
	}

	/**
	 * 
	 * @return heap size in Mb
	 */
	public long getHeapSize() {
		return heapSize;
	}

	/**
	 * 
	 * @return Max heap size in Mb
	 */
	public long getHeapMaxSize() {
		return heapMaxSize;
	}

	/**
	 * 
	 * @return Free memory for heap to use in Mb
	 */
	public long getHeapFreeSize() {
		return heapFreeSize;
	}
	
	public String logTimeAndMemory() {
		return "stopwatch split: " + getSplitTime() + ", heap used/free/max " + getHeapSize() + "/" + getHeapFreeSize() + "/" + getHeapMaxSize();
	}

}
