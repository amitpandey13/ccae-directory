package com.pdgc.general.structures.timeperiod;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.pdgc.csharp.BitConverter;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;

/**
 * Class used to describe and analyze time periods. This class stores them in a
 * way that allows for calculations to be done on them.
 * 
 * @author Vishal Raut
 */
public class TimePeriod implements Serializable, Comparable<TimePeriod>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Don't think hardware architecture would truly affect these strings, but
	// don't hard-code these values in case they do
	private static String emptyPeriodString;
	private static String fullPeriodString;
	
	public final static TimePeriod EMPTY_WEEK;
	public final static TimePeriod FULL_WEEK;
	
	
	// Indexes are same as the built-in DayOfWeek enum: 0 = Sunday, 1 = Monday,
	// 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday
	private BitSet[] days = new BitSet[7];

	// stringified version of the days BitArray array so that we're not looping
	// through all 96*7 bits every single time we want an equality comparison
	private String comparatorString;

	static {
		byte[] byteArray = new byte[84];
		emptyPeriodString = BitConverter.toString(byteArray); // Probably equal to 00-00-00....?
		
		Arrays.fill(byteArray, (byte) 0xFF);
		fullPeriodString = BitConverter.toString(byteArray); // Probably equal to FF-FF-FF....?
		
		EMPTY_WEEK = new TimePeriod();
		
		BitSet[] fullWeekDays = new BitSet[7];
		fullWeekDays[0] = createFullDay();
		fullWeekDays[1] = createFullDay();
		fullWeekDays[2] = createFullDay();
		fullWeekDays[3] = createFullDay();
		fullWeekDays[4] = createFullDay();
		fullWeekDays[5] = createFullDay();
		fullWeekDays[6] = createFullDay();
		FULL_WEEK = new TimePeriod(fullWeekDays);
	}

	public TimePeriod() {
		for (int i = 0; i < days.length; i++) {
			days[i] = new BitSet(96);
		}
		comparatorString = emptyPeriodString;
	}

	public TimePeriod(BitSet[] days) {
		validateDays(days);
		this.days = days;
		populateComparatorString();
	}

	public TimePeriod(TimePeriod timePeriod) {
		for (int i = 0; i < days.length; i++) {
			this.days[i] = (BitSet) timePeriod.days[i].clone();
		}
		populateComparatorString();
	}

	public TimePeriod(TimePeriodPart timePeriodPart) {
		days = timePeriodPart.convertToTimePeriod().days;
		populateComparatorString();
	}

	public TimePeriod(Iterable<TimePeriodPart> timePeriodParts) {
		if (CollectionsUtil.isNullOrEmpty(timePeriodParts)) {
			days = new TimePeriod().days;
		} else {
			List<TimePeriod> prelimTimePeriods = new ArrayList<TimePeriod>();
			for (TimePeriodPart timePeriodPart : timePeriodParts) {
				prelimTimePeriods.add(timePeriodPart.convertToTimePeriod());
			}
			days = unionPeriods(prelimTimePeriods).days;
		}
		populateComparatorString();
	}

	private void validateDays(BitSet[] days) {
		if (ArrayUtils.isEmpty(days)) {
			throw new NullPointerException("Days cannot be null");
		} else if (days.length != 7) {
			throw new IllegalArgumentException("Number of days must equal 7");
		} else {
			for (BitSet day : days) {
				// Java's BitSet works in terms of words of long, so the size
				// for storing 96 bits evaluates to 2 long words, each long
				// being 64 bits so the size returned is 128
				if (day.size() != 128) {
					throw new IllegalArgumentException("Day must be divided in 96 parts");
				}
			}
		}
	}

	/*
	 * helper function that converts the days array into a string. This should
	 * be called by every constructor after the days array has been populated
	 */
	private void populateComparatorString() {
		byte[] byteArray = new byte[84];
		for (int i = 0; i < days.length; i++) {
			byte[] srcByteArray = toByteArray(days[i]);
			System.arraycopy(srcByteArray, 0, byteArray, 12 * i, srcByteArray.length);
		}
		comparatorString = BitConverter.toString(byteArray);
	}
	
	private byte[] toByteArray(BitSet bits) {
		byte[] bytes = new byte[12];
		byte[] srcByteArray = bits.toByteArray();
		System.arraycopy(srcByteArray, 0, bytes, 0, srcByteArray.length > 12 ? 12 : srcByteArray.length);
		return bytes;
	}

	/**
	 * Creates a {@link BitSet} that describes a single day that has dayParts
	 * spanning from {@code startTime} to {@code endTime}
	 * 
	 * @param startTime
	 *            A start time, defined by the hours:minutes after midnight
	 * @param endTime
	 *            An end time, define by the hours:minutes after minute
	 * @return
	 */
	public static BitSet createDay(Duration startTime, Duration endTime) {
		// If the end time is past 'midnight', set it to 11:59:00pm so we don't
		// exceed the bit array size
		if (endTime.toHours() >= 24) {
			endTime = Duration.ofMinutes(23 * 60 + 59);
		}

		if (DateTimeUtil.getAbsoluteMinutes(endTime) % 15 == 0) {
			endTime = endTime.minusMinutes(1);
		}

		if (startTime.toMinutes() == 0 && endTime.toMinutes() >= 1439) {
			return createFullDay();
		}

		if (startTime.toMinutes() == 0 && endTime.toMinutes() == 0) {
			return createFullDay();
		}

		BitSet day = new BitSet(96);
		int startBit = (int) ((startTime.toHours() * 4) + (DateTimeUtil.getAbsoluteMinutes(startTime) / 15));
		int endBit = (int) ((endTime.toHours() * 4) + (DateTimeUtil.getAbsoluteMinutes(endTime) / 15));
		for (int i = startBit; i <= endBit; i++) {
			day.set(i, true);
		}
		return day;
	}

	/**
	 * Creates an empty {@link BitSet} of the size required by TimePeriod to
	 * describe a single day
	 * 
	 * @return
	 */
	public static BitSet createEmptyDay() {
		return new BitSet(96);
	}

	/**
	 * Creates a {@link BitSet} of the size required by TimePeriod to describe a
	 * single day, with all bits flipped to true
	 * 
	 * @return
	 */
	public static BitSet createFullDay() {
		BitSet fullDay = new BitSet(96);
		fullDay.set(0, fullDay.size());
		return fullDay;
	}

	/**
	 * merges the bit arrays of multiple time periods together...using either
	 * AND or OR rules
	 * 
	 * @param combineAND
	 *            true if the time periods are to be ANDed together. False if
	 *            using OR
	 * @param newTimePeriods
	 *            list of time periods to be combined. A null time period is
	 *            interpreted as a full day
	 * @return A single composite time period. This is never null
	 */
	private static TimePeriod combinePeriods(boolean combineAND, Iterable<TimePeriod> newTimePeriods) {
		TimePeriod combinedPeriod;
		if (combineAND) {
			combinedPeriod = new TimePeriod(TimePeriod.FULL_WEEK);
			for (TimePeriod newTimePeriod : newTimePeriods) {
				if (combinedPeriod.isEmpty()) {
					return TimePeriod.EMPTY_WEEK;
				}
				else if (newTimePeriod != null) {
					if (newTimePeriod.isEmpty()) {
						return TimePeriod.EMPTY_WEEK;
					} else if (!newTimePeriod.isFull()) {
						for (int i = 0; i < combinedPeriod.days.length; i++) {
							combinedPeriod.days[i].and(newTimePeriod.days[i]);
						}
					}
				}
			}
		} else {
			combinedPeriod = new TimePeriod();
			for (TimePeriod newTimePeriod : newTimePeriods) {
				if (combinedPeriod.isFull()) {
					return TimePeriod.FULL_WEEK;
				}

				if (newTimePeriod.isFull()) {
					return TimePeriod.FULL_WEEK;
				}
				else {
					for (int i = 0; i < combinedPeriod.days.length; i++) {
						combinedPeriod.days[i].or(newTimePeriod.days[i]);
					}
				}
			}
		}

		combinedPeriod.populateComparatorString();
		return getStaticReference(combinedPeriod);
	}

	/**
	 * Combines the time periods using AND logic
	 * @param newTimePeriods
	 * @return
	 */
	public static TimePeriod intersectPeriods(Iterable<TimePeriod> newTimePeriods) {
		return combinePeriods(true, newTimePeriods);
	}
	
	/**
	 * @see TimePeriod#intersectPeriods(Iterable)
	 * @param newTimePeriods
	 * @return
	 */
	public static TimePeriod intersectPeriods(TimePeriod... newTimePeriods) {
		return combinePeriods(true, Arrays.asList(newTimePeriods));
	}
	
	/**
	 * Combines the time periods using OR logic
	 * @param newTimePeriods
	 * @return
	 */
	public static TimePeriod unionPeriods(Iterable<TimePeriod> newTimePeriods) {
		return combinePeriods(false, newTimePeriods);
	}
	
	/**
	 * @see TimePeriod#unionPeriods(Iterable)
	 * @param newTimePeriods
	 * @return
	 */
	public static TimePeriod unionPeriods(TimePeriod... newTimePeriods) {
		return combinePeriods(false, Arrays.asList(newTimePeriods));
	}
	
	/**
	 * Calculates the inverse of the time period.
	 * 
	 * A time period that encompasses 5pm - 7pm every day M-F would have an
	 * inverse of all-day Sa-Su, and 12am - 5pm and 7pm - 12am every day M-F
	 * 
	 * @param timePeriod
	 *            The time period to be inversed
	 * @return The inverse time period. This will never be null
	 */
	public static TimePeriod inversePeriod(TimePeriod timePeriod) {
		TimePeriod inverseTimePeriod = new TimePeriod(timePeriod);
		for (int i = 0; i < inverseTimePeriod.days.length; i++) {
			inverseTimePeriod.days[i].flip(0, inverseTimePeriod.days[i].size());
		}
		
		inverseTimePeriod.populateComparatorString();
		return getStaticReference(inverseTimePeriod);
	}

	/**
	 * Subtract {@code subtrahend} from {@code minuend}
	 * 
	 * If a daypart is not included in {@code subtrahend} or in {@code minuend}
	 * , the resultant TimePeriod will not include this daypart. If a daypart is
	 * included in {@code subtrahend} and no in {@code minuend} , the resultant
	 * TimePeriod will include this daypart. If a daypart is not included in
	 * {@code subtrahend} but is in {@code minuend} , the resultant TimePeriod
	 * will not include this daypart. If a daypart is included in
	 * {@code subtrahend} and in {@code minuend} , the resultant TimePeriod will
	 * not include this daypart.
	 * 
	 * @param minuend
	 *            The period being subtracted from
	 * @param subtrahend
	 *            The subtracting period
	 * @return The "difference" between the two periods
	 */
	public static TimePeriod subtractPeriods(TimePeriod minuend, TimePeriod subtrahend) {
		if (minuend == null) {
			minuend = TimePeriod.FULL_WEEK;
		}

		if (subtrahend == null) {
			subtrahend = TimePeriod.FULL_WEEK;
		}
		
		if (minuend.isEmpty() || subtrahend.isFull() || minuend.equals(subtrahend)) {
			return TimePeriod.EMPTY_WEEK;
		}

		TimePeriod difference = new TimePeriod(minuend);

		for (int day = 0; day < 7; day++) {
			difference.days[day].and(subtrahend.days[day]);
			difference.days[day].flip(0, difference.days[day].size());
			difference.days[day].and(minuend.days[day]);
		}

		difference.populateComparatorString();
		return getStaticReference(difference);
	}

	/**
	 * reduces the number of java objects by pointing references to full and empty time periods to the static constants
	 * @param timePeriod
	 * @return
	 */
	private static TimePeriod getStaticReference(TimePeriod timePeriod) {
		if (TimePeriod.EMPTY_WEEK.equals(timePeriod)) {
			return TimePeriod.EMPTY_WEEK;
		}
		
		if (TimePeriod.FULL_WEEK.equals(timePeriod)) {
			return TimePeriod.FULL_WEEK;
		}
		
		return timePeriod;
	}
	
	public static boolean hasIntersection(TimePeriod tp1, TimePeriod tp2) {
		if (tp1 == null || tp2 == null || tp1.isEmpty() || tp2.isEmpty()) {
		    return false;
		}
		
		if (tp1.isFull() || tp2.isFull()) {
			return true;
		}
		
		TimePeriod intersectionPeriod = intersectPeriods(tp1, tp2);
		return !intersectionPeriod.isEmpty();
	}	
	
	public boolean isEmpty() {
		return comparatorString.equals(emptyPeriodString);
	}

	public boolean isFull() {
		return comparatorString.equals(fullPeriodString);
	}
	
    /**
	 * Exposes the {@link BitSet} that describes {@code dayOfWeek}
	 * 
	 * @param dayOfWeek
	 *            The day of the week being requested
	 * @return The internal {@link BitSet} object
	 */
	public BitSet getDay(int dayOfWeek) {
		return days[dayOfWeek - 1];
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		TimePeriod obj2 = (TimePeriod)obj;
		return comparatorString.equals(obj2.comparatorString);
	}
	
	public BitSet[] getBitWeek() {
		return days;
	}
	
	@Override
	public int hashCode() {
		return comparatorString.hashCode();
	}

	@Override
	public String toString() {
		if (isFull()) {
			return "Full week";
		}
		if (isEmpty()) {
			return "Empty week";
		}
		
		StringBuilder dayString = new StringBuilder();
		for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
			int startIndex = DateTimeUtil.normalizeDay(dayOfWeek) * 36;
			dayString.append(DateTimeUtil.abbreviateDayOfWeek(dayOfWeek)).append(": ")
					.append(comparatorString.substring(startIndex, startIndex + 35))
					.append(System.getProperty("line.separator"));
		}
		return dayString.toString();
	}
	
	/**
	 * Orders using the opposite of the comparatorString, so that the earlier a timePeriod starts, the earliest it's consideredd
	 */
	@Override
	public int compareTo(TimePeriod o) {
		return -comparatorString.compareTo(o.comparatorString);
	}
}