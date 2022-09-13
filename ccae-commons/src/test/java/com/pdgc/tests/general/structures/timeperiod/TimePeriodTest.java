package com.pdgc.tests.general.structures.timeperiod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.Test;

import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.structures.timeperiod.TimePeriodType;
import com.pdgc.general.util.DateTimeUtil;

public class TimePeriodTest {
	// Test for a time period's ability to populate a single day's bit array
	// given a start/end time

	@Test
	public void emptyPeriodTest() {
		assertTrue(new TimePeriod().isEmpty());
	}

	// Test for a time period's ability to populate a single day's bit array
	// given a start/end time
	@Test
	public void fullPeriodTest() {
		assertTrue(TimePeriod.FULL_WEEK.isFull());
	}

	// Test what happens when 2 or more time periods are combined together
	@Test
	public void combinePeriodTest() {
		TimePeriod fullPeriod = TimePeriod.FULL_WEEK;
		// Define a time period of 5-8pm M-F (D)
		List<TimePeriodPart> timePeriodParts = new ArrayList<TimePeriodPart>();
		timePeriodParts.add(new TimePeriodPart(Duration.ofHours(17), Duration.ofHours(20), TimePeriodType.DAILY, true,
				true, true, true, true, false, false));
		TimePeriod partialPeriod = new TimePeriod(timePeriodParts);
		TimePeriod emptyPeriod = new TimePeriod();

		assertEquals(partialPeriod, TimePeriod.intersectPeriods(fullPeriod, partialPeriod));
		assertEquals(emptyPeriod, TimePeriod.intersectPeriods(partialPeriod, emptyPeriod));
		assertEquals(emptyPeriod, TimePeriod.intersectPeriods(fullPeriod, emptyPeriod));
		assertEquals(emptyPeriod, TimePeriod.intersectPeriods(fullPeriod, partialPeriod, emptyPeriod));

		assertEquals(fullPeriod, TimePeriod.unionPeriods(fullPeriod, partialPeriod));
		assertEquals(partialPeriod, TimePeriod.unionPeriods(partialPeriod, emptyPeriod));
		assertEquals(fullPeriod, TimePeriod.unionPeriods(fullPeriod, emptyPeriod));
		assertEquals(fullPeriod, TimePeriod.unionPeriods(fullPeriod, partialPeriod, emptyPeriod));
	}

	// Test the inverse period function
	@Test

	public void inversePeriodTest() {
		TimePeriod fullPeriod = TimePeriod.FULL_WEEK;
		// Define a time period of 5-8pm M-F (D)
		List<TimePeriodPart> timePeriodParts = new ArrayList<TimePeriodPart>();
		timePeriodParts.add(new TimePeriodPart(Duration.ofHours(17), Duration.ofHours(20), TimePeriodType.DAILY, true,
				true, true, true, true, false, false));
		TimePeriod partialPeriod = new TimePeriod(timePeriodParts);
		TimePeriod emptyPeriod = new TimePeriod();

		BitSet[] expectedInversedPartialDays = new BitSet[7];
		expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)] = new BitSet(96);
		expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)] = new BitSet(96);
		expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)] = new BitSet(96);
		expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)] = new BitSet(96);
		expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)] = new BitSet(96);
		expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)] = TimePeriod.createFullDay();
		expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY)] = TimePeriod.createFullDay();

		for (int i = 0; i < 68; i++) {
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)].set(i, true);
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)].set(i, true);
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)].set(i, true);
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)].set(i, true);
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)].set(i, true);
		}

		for (int i = 80; i < 96; i++) {
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)].set(i, true);
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)].set(i, true);
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)].set(i, true);
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)].set(i, true);
			expectedInversedPartialDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)].set(i, true);
		}

		// Inverse of the partial period
		TimePeriod inversedPartialPeriod = new TimePeriod(expectedInversedPartialDays);

		assertEquals(emptyPeriod, TimePeriod.inversePeriod(fullPeriod));
		assertEquals(inversedPartialPeriod, TimePeriod.inversePeriod(partialPeriod));
		assertEquals(fullPeriod, TimePeriod.inversePeriod(emptyPeriod));
	}

	// Test the subtraction function
	@Test
	public void subtractPeriodsTest() {
		TimePeriod fullPeriod = TimePeriod.FULL_WEEK;
		// Define a time period of 5-8pm M-F (D)
		List<TimePeriodPart> timePeriodParts = new ArrayList<TimePeriodPart>();
		timePeriodParts.add(new TimePeriodPart(Duration.ofHours(17), Duration.ofHours(20), TimePeriodType.DAILY, true,
				true, true, true, true, false, false));
		TimePeriod partialPeriod = new TimePeriod(timePeriodParts);
		TimePeriod emptyPeriod = new TimePeriod();

		assertEquals(emptyPeriod, TimePeriod.subtractPeriods(fullPeriod, fullPeriod));
		assertEquals(TimePeriod.inversePeriod(partialPeriod), TimePeriod.subtractPeriods(fullPeriod, partialPeriod));
		assertEquals(fullPeriod, TimePeriod.subtractPeriods(fullPeriod, emptyPeriod));

		assertEquals(emptyPeriod, TimePeriod.subtractPeriods(partialPeriod, fullPeriod));
		assertEquals(emptyPeriod, TimePeriod.subtractPeriods(partialPeriod, partialPeriod));
		assertEquals(partialPeriod, TimePeriod.subtractPeriods(partialPeriod, emptyPeriod));

		assertEquals(emptyPeriod, TimePeriod.subtractPeriods(emptyPeriod, fullPeriod));
		assertEquals(emptyPeriod, TimePeriod.subtractPeriods(emptyPeriod, partialPeriod));
		assertEquals(emptyPeriod, TimePeriod.subtractPeriods(emptyPeriod, emptyPeriod));
	}
}
