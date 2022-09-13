package com.pdgc.general;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.tsvLoader.RowMap;
import com.pdgc.general.structures.tsvLoader.TSVReader;

public class TermLoader {

	public static List<Term> readPMTLs(String filePath) throws Exception {
		RowMap rowMap = TSVReader.readTSVFile(filePath);
		
		return processRowMap(rowMap);
	}
	
	public static List<Term> processRowMap(RowMap rowMap) throws Exception {
		String startDateColumn = rowMap.getExistingColumnName(Arrays.asList("startDate"));
		String endDateColumn = rowMap.getExistingColumnName(Arrays.asList("endDate"));
		
		if (startDateColumn == null || endDateColumn == null) {
			throw new Exception("startDate or endDate column is missing");
		}
		
		List<Term> terms = new ArrayList<>();
		
		LocalDate startDate;
		LocalDate endDate;
		for (int i=0; i<rowMap.getNumRows(); i++) {
			startDate = loadDate(rowMap, startDateColumn, i);
			endDate = loadDate(rowMap, endDateColumn, i);
			
			terms.add(new Term(startDate, endDate));
		}
		
		return terms;
	}
	
	private static LocalDate loadDate(RowMap rowMap, String columnName, int lineNum) throws Exception {
		String dateString = rowMap.getValue(columnName, lineNum, Function.identity());
		DateTimeFormatter formatter;
		LocalDate date = null;
		
		formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		date = parseDate(dateString, formatter);
		
		if (date == null) {
			formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
			date = parseDate(dateString, formatter);
		}
		
		if (date == null) {
			formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			date = parseDate(dateString, formatter);
		}
		
		if (date == null) {
			formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
			date = parseDate(dateString, formatter);
		}
		
		if (date == null) {
			throw new Exception("Failed to parse date: " + dateString);
		}
		
		return date;
	}
	
	private static LocalDate parseDate(String dateString, DateTimeFormatter formatter) {
		try {
			return LocalDate.parse(dateString, formatter);
		}
		catch (DateTimeParseException e) {
			return null;
		}
	}
}
