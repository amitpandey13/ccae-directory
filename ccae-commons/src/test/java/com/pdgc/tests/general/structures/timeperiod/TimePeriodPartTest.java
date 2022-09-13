package com.pdgc.tests.general.structures.timeperiod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.BitSet;

import org.junit.Test;

import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.structures.timeperiod.TimePeriodType;
import com.pdgc.general.util.DateTimeUtil;

public class TimePeriodPartTest {
	// Test for a time period's ability to populate a single day's bit array
	// given a start/end time
	@Test
	public void createTimePeriodDayTest() {

		BitSet day = TimePeriod.createDay(Duration.ofHours(1), Duration.ofMinutes(150));
		boolean equal = true;
		for (int i = 0; i < 4; i++) {
			if (day.get(i) != false) {
				equal = false;
			}
		}
		for (int i = 4; i < 10; i++) {
			if (day.get(i) != true) {
				equal = false;
			}
		}
		for (int i = 10; i < 96; i++) {
			if (day.get(i) != false) {
				equal = false;
			}
		}
		assertTrue(equal);
	}

	// Test for daily time period that does not wrap around Sunday/Monday
	@Test
	public void dailyPeriodNoWrapTest() {
		TimePeriodPart timePart = new TimePeriodPart(Duration.ofHours(17), Duration.ofMinutes(1350),
				TimePeriodType.DAILY, true, true, true, true, true, false, false);

		TimePeriod timePeriod = new TimePeriod(timePart);
		String prettyString = timePart.getPrettyString();
		BitSet[] periodDays = new BitSet[7];
		BitSet periodDay = new BitSet(96);
		for (int i = 68; i < 90; i++) {
			periodDay.set(i, true);
		}

		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)] = periodDay;
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)] = periodDay;
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)] = periodDay;
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)] = periodDay;
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)] = periodDay;
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)] = TimePeriod.createEmptyDay();
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY)] = TimePeriod.createEmptyDay();

		assertEquals("M-F 5:00P-10:30P (D)", prettyString);
		assertTrue(timePeriod.equals(new TimePeriod(periodDays)));
	}

	// Test for daily time period that wraps around Sunday/Monday
	@Test
	public void dailyPeriodWrapTest() {
		TimePeriodPart timePart = new TimePeriodPart(Duration.ofHours(17), Duration.ofMinutes(1350),
				TimePeriodType.DAILY, true, true, false, false, false, true, true);

		TimePeriod timePeriod = new TimePeriod(timePart);
		String prettyString = timePart.getPrettyString();
		BitSet[] periodDays = new BitSet[7];
		BitSet periodDay = new BitSet(96);
		for (int i = 68; i < 90; i++) {
			periodDay.set(i, true);
		}

		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)] = periodDay;
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)] = periodDay;
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)] = TimePeriod.createEmptyDay();
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)] = TimePeriod.createEmptyDay();
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)] = TimePeriod.createEmptyDay();
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)] = periodDay;
		periodDays[DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY)] = periodDay;

		assertEquals("Sa-T 5:00P-10:30P (D)", prettyString);
		assertTrue(timePeriod.equals(new TimePeriod(periodDays)));
	}

	// Test for daily period that exists only on one day
	@Test
	public void dailyPeriodOneDayTest() {
		TimePeriodPart timePart = new TimePeriodPart(Duration.ofHours(17), Duration.ofMinutes(1350),
				TimePeriodType.DAILY, false, true, false, false, false, false, false);

		TimePeriod timePeriod = new TimePeriod(timePart);
		String prettyString = timePart.getPrettyString();

		BitSet[] expectedDays = new BitSet[7];
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)] = new BitSet(96);
		for (int i = 68; i < 90; i++) {
			expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)].set(i, true);
		}
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY)] = TimePeriod.createEmptyDay();

		assertEquals("T 5:00P-10:30P (D)", prettyString);
		assertTrue(timePeriod.equals(new TimePeriod(expectedDays)));
	}

	// Test for continuous period that does not wrap around Sunday/Monday
	@Test
	public void continuousPeriodNoWrapTest() {
		TimePeriodPart timePart = new TimePeriodPart(Duration.ofHours(17), Duration.ofMinutes(1350),
				TimePeriodType.CONTINUOUS, true, true, true, true, true, false, false);

		TimePeriod timePeriod = new TimePeriod(timePart);
		String prettyString = timePart.getPrettyString();

		BitSet[] expectedDays = new BitSet[7];
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)] = new BitSet(96);
		for (int i = 68; i < 96; i++) {
			expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)].set(i, true);
		}
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)] = TimePeriod.createFullDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)] = TimePeriod.createFullDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)] = TimePeriod.createFullDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)] = new BitSet(96);
		for (int i = 0; i < 90; i++) {
			expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)].set(i, true);
		}
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY)] = TimePeriod.createEmptyDay();

		assertEquals("M-F 5:00P-10:30P (C)", prettyString);
		assertTrue(timePeriod.equals(new TimePeriod(expectedDays)));
	}

	// Test for continuous period that wraps around Sunday/Monday
	@Test
	public void continuousPeriodWrapTest() {
		TimePeriodPart timePart = new TimePeriodPart(Duration.ofHours(17), Duration.ofMinutes(1350),
				TimePeriodType.CONTINUOUS, true, true, false, false, false, true, true);

		TimePeriod timePeriod = new TimePeriod(timePart);
		String prettyString = timePart.getPrettyString();

		BitSet[] expectedDays = new BitSet[7];
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)] = new BitSet(96);
		for (int i = 68; i < 96; i++) {
			expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)].set(i, true);
		}
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY)] = TimePeriod.createFullDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)] = TimePeriod.createFullDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)] = new BitSet(96);
		for (int i = 0; i < 90; i++) {
			expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)].set(i, true);
		}
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)] = TimePeriod.createEmptyDay();

		assertEquals("Sa-T 5:00P-10:30P (C)", prettyString);
		assertTrue(timePeriod.equals(new TimePeriod(expectedDays)));
	}

	// Test for continuous period that exists only on one day
	@Test
	public void continuousPeriodOneDayTest() {
		TimePeriodPart timePart = new TimePeriodPart(Duration.ofHours(17), Duration.ofMinutes(1350),
				TimePeriodType.CONTINUOUS, false, true, false, false, false, false, false);

		TimePeriod timePeriod = new TimePeriod(timePart);
		String prettyString = timePart.getPrettyString();

		BitSet[] expectedDays = new BitSet[7];
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)] = new BitSet(96);
		for (int i = 68; i < 90; i++) {
			expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)].set(i, true);
		}
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)] = TimePeriod.createEmptyDay();
		expectedDays[DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY)] = TimePeriod.createEmptyDay();

		assertEquals("T 5:00P-10:30P (C)", prettyString);
		assertTrue(timePeriod.equals(new TimePeriod(expectedDays)));
	}
}
