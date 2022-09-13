package com.pdgc.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;
import com.pdgc.general.structures.tsvLoader.RowMap;
import com.pdgc.general.structures.tsvLoader.TSVReader;

public class PMTLTSVLoader {

	/**
	 * Returns a non-unique list of pmtlidSets. 
	 * The idSets will be in the order in which they were stored in the file, 
	 * so the pmtlIdSet stored at index i will be found line i+1 of the file
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static List<PMTLIdSet> readPMTLs(String filePath) throws Exception {
		RowMap rowMap = TSVReader.readTSVFile(filePath);
		
		return processRowMap(rowMap);
	}
	
	public static List<PMTLIdSet> processRowMap(RowMap rowMap) throws Exception {
		String productColumn = rowMap.getExistingColumnName(getAcceptableColumnNames("product"));
		String mediaColumn = rowMap.getExistingColumnName(getAcceptableColumnNames("media"));
		String territoryColumn = rowMap.getExistingColumnName(getAcceptableColumnNames("territory"));
		String languageColumn = rowMap.getExistingColumnName(getAcceptableColumnNames("language"));
		
		if (productColumn == null || mediaColumn == null || territoryColumn == null || languageColumn == null) {
			throw new Exception("One or more columns are missing. Acceptable formats are : {dimensionName}, {dimensionName}Id, {dimensionName}Ids, {dimensionName}Array");
		}
		
		List<PMTLIdSet> pmtls = new ArrayList<>();
		for (int i=0; i<rowMap.getNumRows(); i++) {
			Set<Integer> productIds = loadDimension(rowMap, productColumn, i);
			Set<Integer> mediaIds = loadDimension(rowMap, mediaColumn, i);
			Set<Integer> territoryIds = loadDimension(rowMap, territoryColumn, i);
			Set<Integer> languageIds = loadDimension(rowMap, languageColumn, i);
			
			pmtls.add(new PMTLIdSet(
				productIds,
				mediaIds,
				territoryIds,
				languageIds
			));
		}
		
		return pmtls;
	}
	
	private static Collection<String> getAcceptableColumnNames(String dimensionName) {
		return Arrays.asList(
			dimensionName,
			dimensionName + "Id",
			dimensionName + "Ids",
			dimensionName + "Array"
		);
	}
	
	private static Set<Integer> loadDimension(RowMap rowMap, String columnName, int lineNum) {
		String valueString = rowMap.getValue(columnName, lineNum, Function.identity());
		valueString = valueString.replaceAll("\\[*\\]*\\{*\\}*\\(*\\)*", ""); //Strip any brackets/parenthesis/whatever that may exist
		valueString = valueString.replaceAll("\\D", "|"); //Standardize the delimiter character for consistency
		
		String[] idStrings = valueString.split("\\|");
		
		Set<Integer> ids = new HashSet<>();
		for (String idString : idStrings) {
			if (idString.isEmpty()) {
				continue;
			}
			
			ids.add(Integer.parseInt(idString));
		}
		
		return ids;
	}
}
