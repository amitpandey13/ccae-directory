package com.pdgc.general.util;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;

/**
 * Util class for commonly used date comparison and/or manipulation 
 * 
 * @author Clara Hong
 *
 */
public class DatesUtil {
	
	private static final DateTimeFormatter EXCEL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

	private DatesUtil() { } 
	
	/**
	 * Determine if date is equal to TBA 
	 * @param date
	 * @return
	 */
	public static boolean isTbaDate(LocalDate date) {
		if (date == null) 
			return false; 

	return date.equals(Constants.TBA_DATE) 
		|| date.equals(Constants.TBA_DATE.plusDays(1)) 
		|| date.equals(Constants.TBA_DATE.minusDays(1));
	}
	
	/**
	 * Determine if date is equal to Perpetuity 
	 * @param date
	 * @return
	 */
	public static boolean isPerpetuityDate(LocalDate date) {
		if (date == null) 
			return false;
		
		return date.equals(Constants.PERPETUITY);
	}

    /**
     * Determine if date is equal to Perpetuity
     * @param date
     * @return
     */
    public static boolean isEpochDate(LocalDate date) {
        if (date == null)
            return false;

        return date.equals(Constants.EPOCH);
    }
	
	/**
	 * Get Excel-format date string
	 * @param date
	 * @return
	 */
	public static String getExcelDate(LocalDate date) {
		String dateString; 
		if (date == null) {
			dateString = "";
		} else if (isTbaDate(date)) {
			dateString = Constants.TBA_STR;
		} else if (isPerpetuityDate(date)) {
			dateString = Constants.PERPETUITY_STR;
		} else {
			dateString = DateTimeUtil.toOADate(date);
		}
		return dateString;
	}
	
	/**
	 * Determine if a term is equal to TBA 
	 * @param term
	 * @return
	 */
	public static boolean isTbaTerm(Term term) {
		if (term == null) return false; 
		return isTbaTerm(term.getStartDate(), term.getEndDate());
	}
	
	/**
	 * Determine if a start and end date of term is equal to TBA 
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isTbaTerm(LocalDate startDate, LocalDate endDate) {
		if (startDate == null) return false; 
		if (endDate == null) return false; 
		return isTbaDate(startDate) && isTbaDate(endDate);
	}
	
	/**
	 * Returns {@code true} if the start date and end date are equal to TBA term, and the term falls after the latest allowed end date.  
	 * @param startDate
	 * @param endDate
	 * @param latestEndDate
	 * @return
	 */
	public static boolean isTermTbaOutOfRange(LocalDate startDate, LocalDate endDate, LocalDate latestEndDate) {
		return isTbaTerm(startDate, endDate) && Constants.TBA_DATE.isAfter(latestEndDate);  
	}

	public static String getStringDate(LocalDate date) {
		if (date == null) {
			return "";
		} else if (date.isEqual(Constants.PERPETUITY)) {
			return Constants.PERPETUITY_STR;
		} else if (date.isEqual(Constants.TBA_DATE)) {
			return Constants.TBA_STR;
		} else {
			return date.format(EXCEL_DATE_FORMATTER);
		}
	}

	public static String oaDateToStringDateConverter(String oaDate) {
		try {
			Double date = Double.parseDouble(oaDate);
			return DatesUtil.getStringDate(LocalDate.of(1899, Month.DECEMBER, 30).plusDays(date.longValue()));
		} catch (NumberFormatException ne) {
			return oaDate;
		}
	}
	
	 /**
     * Returns {@code true} if the the date is supported by excel date
     * 
     * @param date
     * @return
     */
    public static boolean isSupportedExcelDate(LocalDate date) {

        return (date == null || !LocalDate.of(1900, 01, 01).isAfter(date));
    }
}