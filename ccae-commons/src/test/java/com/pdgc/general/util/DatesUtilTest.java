package com.pdgc.general.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pdgc.general.lookup.Constants;

class DatesUtilTest {

	@BeforeEach
	void setUp() throws Exception {
		Constants.instantiateConstants();
	}

	@Test
	public void givenTbaDate_whenCalculateIsTbaDate_thenTrue() {
		LocalDate date = Constants.TBA_DATE;
		assertTrue(DatesUtil.isTbaDate(date));
	}
	
	@Test
	public void givenNonTbaDate_whenCalculateIsTbaDate_thenFalse() {
		LocalDate date = LocalDate.MAX;
		assertFalse(DatesUtil.isTbaDate(date));
	}
	
	@Test
	public void givenNullDate_whenCalculateIsTbaDate_thenFalse() {
		LocalDate date = null;
		assertFalse(DatesUtil.isTbaDate(date));
	}
	
	@Test
	public void givenPerpetuityDate_whenCalculateIsPerpetuityDate_thenTrue() {
		LocalDate date = Constants.PERPETUITY;
		assertTrue(DatesUtil.isPerpetuityDate(date));
	}
	
	@Test
	public void givenNonPerpetuityDate_whenCalculateIsPerpetuityDate_thenFalse() {
		LocalDate date = LocalDate.MAX;
		assertFalse(DatesUtil.isPerpetuityDate(date));
	}
	
	@Test
	public void givenNullDate_whenCalculateIsPerpetuityDate_thenFalse() {
		LocalDate date = null;
		assertFalse(DatesUtil.isPerpetuityDate(date));
	}
	
	@Test
	public void givenTbaDate_whenGetExcelDate_thenTbaString() {
		LocalDate date = Constants.TBA_DATE; 
		assertEquals(Constants.TBA_STR, DatesUtil.getExcelDate(date)); 
	}
	
	@Test
	public void givenPerpetuityDate_whenGetExcelDate_thenPerpetuityString() {
		LocalDate date = Constants.PERPETUITY; 
		assertEquals(Constants.PERPETUITY_STR, DatesUtil.getExcelDate(date)); 
	}
	
	@Test
	public void givenRegularDate_whenGetExcelDate_thenToOADateString() {
		LocalDate date = LocalDate.of(2019, 1, 1); 
		assertEquals(DateTimeUtil.toOADate(LocalDate.of(2019, 1, 1)), DatesUtil.getExcelDate(date)); 
	}
	
	@Test
	public void givenNullDate_whenGetExcelDate_thenBlankString() {
		LocalDate date = null; 
		assertEquals("", DatesUtil.getExcelDate(date)); 
	}
	
	@Test
	public void givenTbaStartDateAndTbaEndDate_whenCalculateIsTbaTerm_thenTrue () {
		LocalDate startDate = Constants.TBA_DATE;
		LocalDate endDate = Constants.TBA_DATE; 
		assertTrue(DatesUtil.isTbaTerm(startDate, endDate));
	}

	@Test
	public void givenTbaStartDateAndNonTbaEndDate_whenCalculateIsTbaTerm_thenFalse () {
		LocalDate startDate = Constants.TBA_DATE;
		LocalDate endDate = LocalDate.MAX; 
		assertFalse(DatesUtil.isTbaTerm(startDate, endDate));
	}
	
	@Test
	public void givenNonTbaStartDateAndTbaEndDate_whenCalculateIsTbaTerm_thenFalse () {
		LocalDate startDate = LocalDate.MAX; 
		LocalDate endDate = Constants.TBA_DATE;
		assertFalse(DatesUtil.isTbaTerm(startDate, endDate));
	}
	
	@Test
	public void givenNullDates_whenCalculateIsTbaTerm_thenFalse () {
		LocalDate startDate = null; 
		LocalDate endDate = null;
		assertFalse(DatesUtil.isTbaTerm(startDate, endDate));
	}
	
	@Test
	public void givenTbaDatesOutOfRange_whenCalculateIsTermTbaOutOfRange_thenTrue() {
		LocalDate startDate = Constants.TBA_DATE; 
		LocalDate endDate = Constants.TBA_DATE;
		LocalDate criteriaEndDate = LocalDate.MIN;
		assertTrue(DatesUtil.isTermTbaOutOfRange(startDate, endDate, criteriaEndDate));
	}
	
	@Test
	public void givenTbaDatesInRange_whenCalculateIsTermTbaOutOfRange_thenFalse() {
		LocalDate startDate = Constants.TBA_DATE; 
		LocalDate endDate = Constants.TBA_DATE;
		LocalDate criteriaEndDate = LocalDate.MAX;
		assertFalse(DatesUtil.isTermTbaOutOfRange(startDate, endDate, criteriaEndDate));
	}
}
