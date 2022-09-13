package com.pdgc.general.util;

import com.pdgc.exceptions.RetryAttemptsFailedException;

/**
 * Utility class for retries on error with exponential backoff
 * @author Jessica Shin
 */
public final class RetryUtil {
    private static final int MAX_ATTEMPTS = 4;
    private static final int BACKOFF_MILLIS = 200;

    private RetryUtil() { }

    public static <T> T execute(RetryFunction<T> fn) {
        return execute(MAX_ATTEMPTS, BACKOFF_MILLIS, fn);
    }

    /**
     * Retry lambda function on error
     * @param maxAttempts - max number of attempts before throwing exception
     * @param backoffMillis - milliseconds to multiply by for exponential backoff
     * @param fn - lambda function to retry on error
     * @return - return value of retry function
     */
    public static <T> T execute(int maxAttempts, int backoffMillis, RetryFunction<T> fn) {
        int attempts = 0;
        while (attempts < maxAttempts - 1) {
            try {
                return fn.execute();
            } catch (Exception e) {
                doWait(++attempts, backoffMillis);
            }
        }
        try {
            return fn.execute();
        } catch (Exception e) {
            String message = "Reached maximum retry count";
            if (e.getMessage() != null) {
                message += ": " + e.getMessage();
            }
            throw new RetryAttemptsFailedException(message, e.getCause());
        }
    }

    /**
     * Delay before retry at an exponentially increasing rate
     * @param attempts - current count of attempts
     * @param backoffMillis - milliseconds to multiply by for backoff
     */
    private static void doWait(int attempts, int backoffMillis) {
        try {
            Thread.sleep(Math.round(Math.pow(2, attempts)) * backoffMillis); // 2^attempts * backoffMillis
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface RetryFunction<T> {
        T execute() throws Exception;
    }
}
