package com.pdgc.avails.structures.criteria;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Date;

import com.pdgc.general.lookup.Constants;

public class TimeSpanHelper {

    private static TimeUnit[] calcPriority = {
            TimeUnit.YEARS, TimeUnit.MONTHS, TimeUnit.WEEKS, TimeUnit.DAYS};
    
    /**
     * Lazy creator to create a TimeSpan that contains only 1 length + unit combo
     * ie...what window length currently is
     * @param length
     * @param timeUnit
     * @return
     */
    public static TimeSpan createBasicTimeSpan(int length, TimeUnit timeUnit) {
        return new TimeSpan(Collections.singleton(new TimeSpanComponent(length, timeUnit)));
    }
    
    /**
     * Returns true if the timespan is null or empty
     * @param timespan
     * @return
     */
    public static boolean isNullOrEmpty(TimeSpan timespan) {
        return timespan == null || timespan.isEmpty();
    }
    
    /**
     * Returns true if the difference between startDate and endDate is at least as long as windowLength.
     * End dates are implicitly considered too be 23:59:59, so even if startDate and endDate are equal,
     * the length of time is considered to be 1 day, not 0
     * @param startDate
     * @param endDate
     * @param windowLength
     * @return
     */
    public static boolean meetsWindowLength(LocalDate startDate, LocalDate endDate, TimeSpan windowLength) {
        if (isNullOrEmpty(windowLength)) {
            return true;
        }
        
        LocalDate minEndDate = addTime(startDate, windowLength);
        
        // End dates are implicitly 23:59:59 or so, so that a 1-day term has the
        // same start/end date as far as year/month/day is concerned, so cheat
        // and ensure the date is 1 less
        minEndDate = minEndDate.plusDays(-1);
        
        return endDate.compareTo(minEndDate) >= 0;
    }
    
    /**
     * Adds timeSpan to date
     * @param date
     * @param timeSpan
     * @return
     */
    public static LocalDate addTime(LocalDate date, TimeSpan timeSpan) {
        if (isNullOrEmpty(timeSpan)) {
            return date;
        }
        
        LocalDate resultDate = date;
        for (TimeUnit unit : calcPriority) {
            resultDate = addTimeUnit(resultDate, unit, timeSpan.getLengthForUnit(unit));
        }
        return resultDate;
    }
    
    /**
     * Subtracts timeSpan from date
     * @param date
     * @param timeSpan
     * @return
     */
    public static LocalDate subtractTime(LocalDate date, TimeSpan timeSpan) {
        if (isNullOrEmpty(timeSpan)) {
            return date;
        }
        
        LocalDate resultDate = date;
        for (TimeUnit unit : calcPriority) {
            resultDate = addTimeUnit(resultDate, unit, -timeSpan.getLengthForUnit(unit));
        }
        return resultDate;
    }
    
    /**
     * Adds the passed length of time unit to the {@link Date} specified and
     * returns the new {@link Date}
     * 
     * @param date the date to add time unit to
     * @param timeUnit the {@link TimeUnit} to use to add
     * @param length the length of time unit to add, can be negative which will
     * subtract the time unit
     * 
     * @return the {@code Date} object with length of time unit added
     */
    public static LocalDate addTimeUnit(LocalDate date, TimeUnit timeUnit, int length) {
        //unit is irrelevant if length is 0
        if (length == 0) {
            return date;
        }
        
        LocalDate newDate = date;
        boolean isEndOfMonth = date.equals(date.with(TemporalAdjusters.lastDayOfMonth()));

        try {
            switch (timeUnit) {
                case DAYS:
                    newDate = date.plusDays(length);
                    break;
                case WEEKS:
                    newDate = date.plusWeeks(length);
                    break;
                case MONTHS:
                    newDate = date.plusMonths(length);
                    newDate = isEndOfMonth ? newDate.with(TemporalAdjusters.lastDayOfMonth()) : newDate;
                    break;
                case YEARS:
                    newDate = date.plusYears(length);
                    newDate = isEndOfMonth ? newDate.with(TemporalAdjusters.lastDayOfMonth()) : newDate;
                    break;
            }
        } catch (DateTimeException e) {
            //If we got an invalid date, then we also ended up earlier than EPOCH or later than PERPETUITY
            //So snap to the the appropriate constant depending on whether we were adding (PERPETUITY) or subtracting (EPOCH)
            return length > 0 ? Constants.PERPETUITY : Constants.EPOCH;
        }
        
        //Snap to EPOCH or PERPETUITY if we went out of bounds
        if (newDate.isBefore(Constants.EPOCH)) {
            return Constants.EPOCH;
        }
        if (newDate.isAfter(Constants.PERPETUITY)) {
            return Constants.PERPETUITY;
        }

        return newDate;
    }
}
