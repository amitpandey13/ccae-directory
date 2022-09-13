package com.pdgc.general.structures.tsvLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public class RowMap {

	int numRows;
	Map<String, ArrayList<String>> rowMap = new HashMap<>();
	List<String> columnNames;
	
	public RowMap(String[] columnNames) {
		this.columnNames = new ArrayList<>(columnNames.length);
		for (int i=0; i<columnNames.length; i++) {
		    addColumn(columnNames[i], "");
		}
	}
	
	public int createNewRow(String[] row) {
		for (int i=0; i<row.length; i++) {
			rowMap.get(columnNames.get(i)).add(row[i]);
		}
		return numRows++;
	}
	
	public void addColumn(String columnName, String defaultVal) {
	    String column = columnName;
        column = column.replace("\"", "");
        column = column.toLowerCase();
        
        this.columnNames.add(column);
        ArrayList<String> columnVals = new ArrayList<>(numRows);
        for (int i=0; i<numRows; i++) {
            columnVals.add(defaultVal);
        }
        rowMap.put(column, columnVals);
	}
	
	public int getNumRows() {
		return numRows;
	}
	
	public boolean containsColumnName(String columnName) {
		columnName = columnName.toLowerCase();
		return rowMap.containsKey(columnName);
	}
	
	/**
	 * Selects among the columnNames and returns the one that exists.
	 * It returns the first column that exists, so the order of the names matters
	 * Returns null if there are no matches
	 * 
	 * @param columnNames
	 * @return
	 */
	public String getExistingColumnName(Iterable<String> columnNames) {
		for (String columnName : columnNames) {
			columnName = columnName.toLowerCase();
			if (rowMap.containsKey(columnName)) {
				return columnName;
			}
		}
		return null;
	}
	
	public <E> E getValue(String columnName, int lineNum, Function<String, E> mapper) {
		columnName = columnName.toLowerCase();
		
		if (!rowMap.containsKey(columnName)) {
			return null;
		}
		
		String value = rowMap.get(columnName).get(lineNum);
		
		if (StringUtils.isBlank(value)) {
			return null;
		}
		
		return mapper.apply(value);
	}
}
