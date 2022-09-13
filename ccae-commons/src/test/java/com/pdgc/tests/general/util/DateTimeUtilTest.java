package com.pdgc.tests.general.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.util.DateTimeUtil;

public class DateTimeUtilTest {

	@Before
	public void setUp() throws IOException {
		Constants.instantiateConstants();
	}
	
	@Test
	public void testAbbreviateDayOfWeek() {
		assertEquals("M", DateTimeUtil.abbreviateDayOfWeek(Calendar.MONDAY));
		assertEquals("T", DateTimeUtil.abbreviateDayOfWeek(Calendar.TUESDAY));
		assertEquals("W", DateTimeUtil.abbreviateDayOfWeek(Calendar.WEDNESDAY));
		assertEquals("Th", DateTimeUtil.abbreviateDayOfWeek(Calendar.THURSDAY));
		assertEquals("F", DateTimeUtil.abbreviateDayOfWeek(Calendar.FRIDAY));
		assertEquals("Sa", DateTimeUtil.abbreviateDayOfWeek(Calendar.SATURDAY));
		assertEquals("Su", DateTimeUtil.abbreviateDayOfWeek(Calendar.SUNDAY));
	}

	@Test
	public void normalizeDayTest() {
		assertEquals(0, DateTimeUtil.normalizeDay(Calendar.SUNDAY));
		assertEquals(1, DateTimeUtil.normalizeDay(Calendar.MONDAY));
		assertEquals(2, DateTimeUtil.normalizeDay(Calendar.TUESDAY));
		assertEquals(3, DateTimeUtil.normalizeDay(Calendar.WEDNESDAY));
		assertEquals(4, DateTimeUtil.normalizeDay(Calendar.THURSDAY));
		assertEquals(5, DateTimeUtil.normalizeDay(Calendar.FRIDAY));
		assertEquals(6, DateTimeUtil.normalizeDay(Calendar.SATURDAY));
	}

	@Test
	public void normalizeDayOfWeekTest() {
		assertEquals(0, DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY));
		assertEquals(1, DateTimeUtil.normalizeDay(2));
		assertEquals(2, DateTimeUtil.normalizeDay(3));
		assertEquals(3, DateTimeUtil.normalizeDay(4));
		assertEquals(4, DateTimeUtil.normalizeDay(5));
		assertEquals(5, DateTimeUtil.normalizeDay(6));
		assertEquals(6, DateTimeUtil.normalizeDay(7));
	}

	@Test
	public void getAbsoluteMinutesTest() {
		assertEquals(0, DateTimeUtil.getAbsoluteMinutes(Duration.ofDays(1)));
		assertEquals(0, DateTimeUtil.getAbsoluteMinutes(Duration.ofHours(1)));
		assertEquals(15, DateTimeUtil.getAbsoluteMinutes(Duration.ofMinutes(15)));
		assertEquals(1, DateTimeUtil.getAbsoluteMinutes(Duration.ofSeconds(60)));
	}

	@Test
	public void getAbsoluteSecondsTest() {
		assertEquals(0, DateTimeUtil.getAbsoluteSeconds(Duration.ofDays(1)));
		assertEquals(0, DateTimeUtil.getAbsoluteSeconds(Duration.ofHours(1)));
		assertEquals(0, DateTimeUtil.getAbsoluteSeconds(Duration.ofMinutes(1)));
		assertEquals(45, DateTimeUtil.getAbsoluteSeconds(Duration.ofSeconds(45)));
	}

	@Test
	public void formatDateTest() {
		assertEquals(DateTimeUtil.formatDate(new Date()), DateTimeUtil.formatDate(new Date()));
	}

	@Test
	public void createDate() {
		assertEquals(LocalDate.of(2017, 2, 7), DateTimeUtil.createDate(2017, 2, 7));
	}
	
//	TODO: fix this toOADateTest. It has been commented out.	
	@Test
	public void toOADateTest() {
		int counter = 0;
		for(int i = 1900; i <= 2017; i++) {
			if((i % 4) == 0) {
				if(i % 100 == 0) {
					if(i % 400 == 0) {
						counter++;
						continue;
					}
				}
				else {
					counter++;
					continue;
				}
			}
		}
		System.out.println("Counter value for a leap year is: " + counter);
		assertEquals(String.format("%.10f", 36526.0), DateTimeUtil.toOADate(LocalDate.of(2000, 1, 1)));
		assertEquals(String.format("%.10f", 1.0), DateTimeUtil.toOADate(LocalDate.of(1900, 1, 1)));
		assertEquals(String.format("%.10f", 1.0), DateTimeUtil.toOADate(LocalDate.of(1900, 1, 1)));
		assertEquals(String.format("%.10f", 61.0), DateTimeUtil.toOADate(LocalDate.of(1900, 3, 1)));
		assertEquals(String.format("%.10f", 1.25), DateTimeUtil.toOADate(LocalDateTime.of(LocalDate.of(1900, 1, 1), LocalTime.of(6, 0))));
		assertEquals(String.format("%.10f", 2.0), DateTimeUtil.toOADate(LocalDate.of(1900, 1, 2)));
		assertEquals(String.format("%.10f", 367.0), DateTimeUtil.toOADate(LocalDate.of(1901, 1, 1)));
		assertEquals(String.format("%.10f", 732.0), DateTimeUtil.toOADate(LocalDate.of(1902, 1, 1)));
		assertEquals(String.format("%.10f", 1828.0), DateTimeUtil.toOADate(LocalDate.of(1905, 1, 1)));
		assertEquals(String.format("%.10f", 42735.0), DateTimeUtil.toOADate(LocalDate.of(2016, 12, 31)));
		assertEquals(String.format("%.10f", 42736.0), DateTimeUtil.toOADate(LocalDate.of(2017, 1, 1)));
		assertEquals(String.format("%.10f", 43100.0), DateTimeUtil.toOADate(LocalDate.of(2017, 12, 31)));
		assertEquals(String.format("%.10f", 43101.0), DateTimeUtil.toOADate(LocalDate.of(2018, 1, 1)));
	}

}
