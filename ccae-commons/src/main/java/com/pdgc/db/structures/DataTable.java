package com.pdgc.db.structures;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.postgresql.jdbc.PgResultSetMetaData;

import com.pdgc.general.lookup.Constants;

/**
 * Container to hold the results returned by {@link ResultSet} on executing an
 * SQL Query
 * 
 * @author Vishal Raut
 */
public class DataTable {

	private String query;
	private List<ColumnInfo> columns;
	private List<DataRow> rows;
	private int columnCount;
	private boolean isLoaded;

	private Map<String, Integer> columnIndexByName = new HashMap<>();

	//
	// A helper method to feed it the sql its about to turn into rows
	//
	public  DataTable(String _sql) {
		query = _sql;
	}
	
	public  DataTable() {
		// Need this if we are going to have the above Constructor
	}
	
	public void load(ResultSet rs) throws SQLException {
		columnCount = rs.getMetaData().getColumnCount();
		columns = new ArrayList<>(columnCount);
		rows = new ArrayList<>();

		addColumns(rs.getMetaData());
		addRows(rs);

		isLoaded = true;		
		rs.close(); // Lets close this out when we are done because we are 
	}

	public int getColumnCount() {
		return columnCount;
	}

	public int getRowCount() {
		return columnCount == -1 ? -1 : rows.size();
	}

	public Iterable<DataRow> getRows() {
		// return an unmodifiable copy of rows
		return Collections.unmodifiableList(rows);
	}

	/**
	 * Returns the underlying column name of a query result, or "" if it is
	 * unable to be determined.
	 * 
	 * @param columnIndex
	 *            column position (0-based)
	 * @return underlying column name of a query result
	 * @throws IllegalStateException
	 *             if data table has not been loaded with {@link ResultSet}
	 * @throws IllegalArgumentException
	 *             if column index is out of range
	 */
	public String getColumnName(int columnIndex) {
		validateColumnIndex(columnIndex);
		return columns.get(columnIndex).getName();
	}

	/**
	 * Gets the designated column's suggested title for use in printouts and
	 * displays. The suggested title is usually specified by the SQL AS clause.
	 * If a SQL AS is not specified, the value returned from getColumnLabel will
	 * be the same as the value returned by the getColumnName method.
	 * 
	 * @param columnIndex
	 *            column position (0-based)
	 * @return the suggested column title
	 * @throws IllegalStateException
	 *             if data table has not been loaded with {@link ResultSet}
	 * @throws IllegalArgumentException
	 *             if column index is out of range
	 */
	public String getColumnLabel(int columnIndex) {
		validateColumnIndex(columnIndex);
		return columns.get(columnIndex).getLabel();
	}

	private int getColumnIndex(String columnName) {
		String lcColumnName = columnName.toLowerCase(Locale.ROOT);
		Integer cachedIndex = columnIndexByName.get(lcColumnName);
		if (cachedIndex != null) {
			return cachedIndex;
		}

		for (int i = 0; i < columns.size(); i++) {
			ColumnInfo columnInfo = columns.get(i);
			if (columnInfo.getName().equalsIgnoreCase(lcColumnName) || columnInfo.getLabel().equalsIgnoreCase(lcColumnName)) {
				columnIndexByName.put(lcColumnName, i);
				return i;
			}
		}

		throw new IllegalArgumentException(String.format("Column with name: %s could not be found", columnName));
	}

	/**
	 * Returns the row at the specified row index
	 * 
	 * @param rowIndex
	 *            row position (0-based)
	 * @return the row at the index
	 * @throws IllegalStateException
	 *             if data table has not been loaded with {@link ResultSet}
	 * @throws IllegalArgumentException
	 *             if row index is out of range
	 */
	public DataRow getRow(int rowIndex) {
		validateRowIndex(rowIndex);
		return rows.get(rowIndex);
	}

	private void validateColumnIndex(int columnIndex) {
		validateDataTableLoaded();
		validateIndex(columnIndex, columnCount, "Column");
	}

	private void validateRowIndex(int rowIndex) {
		validateDataTableLoaded();
		validateIndex(rowIndex, rows.size(), "Row");
	}

	private void validateDataTableLoaded() {
		if (!isLoaded) {
			throw new IllegalStateException("DataTable has not been loaded");
		}
	}

	private void addColumns(ResultSetMetaData resultSetMetaData) throws SQLException {
		for (int i = 1; i <= columnCount; i++) {
			ColumnInfo column = new ColumnInfo();
			column.setName(((PgResultSetMetaData) resultSetMetaData).getBaseColumnName(i));
			column.setLabel(WordUtils.capitalize(resultSetMetaData.getColumnLabel(i)));
			columns.add(column);
		}
	}

	private void addRows(ResultSet rs) throws SQLException {
		while (rs.next()) {
			DataRow row = new DataRow(this);
			for (int i = 1; i <= columnCount; i++) {
				row.addColumn(rs.getObject(i));
			}
			rows.add(row);
		}
	}

	private static void validateIndex(int index, int maxIndex, String label) {
		if (index < 0 || index >= maxIndex) {
			throw new IllegalArgumentException(
					String.format("Invalid %s Index: %d, should be between 0 and %d (exclusive)", label, index, maxIndex));
		}
	}

	/**
	 * Contains the data for a row read from the database
	 */
	public static class DataRow {

		private DataTable dataTable;
		private List<Object> columns;

		public DataRow(DataTable dataTable) {
			this.dataTable = dataTable;
			this.columns = new ArrayList<>(dataTable.getColumnCount());
		}

		private void addColumn(Object data) {
			validateIndex(columns.size(), dataTable.getColumnCount(), "Add Column");
			columns.add(data);
		}

		/**
		 * Returns the underlying column data of a query result for this row
		 * 
		 * @param columnIndex
		 *            column position (0-based)
		 * @return underlying column data of a query result for this row
		 * @throws IllegalStateException
		 *             if data table has not been loaded with {@link ResultSet}
		 * @throws IllegalArgumentException
		 *             if column index is out of range
		 */
		public Object getColumn(int columnIndex) {
			validateIndex(columnIndex, dataTable.getColumnCount(), "Column");
			return columns.get(columnIndex);
		}

		/**
		 * Returns the underlying column data of a query result for this row
		 * 
		 * @param columnName
		 *            name of the column
		 * @return underlying column data of a query result for this row
		 * @throws IllegalStateException
		 *             if data table has not been loaded with {@link ResultSet}
		 * @throws IllegalArgumentException
		 *             if column with specified name could not be found
		 */
		public Object getColumn(String columnName) {
			return columns.get(dataTable.getColumnIndex(columnName));
		}

		/**
		 * Does the column exists on the result set. True if yes. False otherwise.
		 * @param columnName the name of the column to validate.
		 * @return
		 */
		public boolean columnExists(String columnName) {
			try {
				dataTable.getColumnIndex(columnName);
			}
			catch (IllegalArgumentException e) {
				return false;
			}
			return true;
		}
		
		public String getString(String columnName) {
			return getString(columnName, null);
		}
		
		public String getString(String columnName, String valueIfNull) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? valueIfNull : value.toString();
		}
		
		public Long getLong(String columnName) {
			return getLong(columnName, null);
		}

		public Long getLong(String columnName, Long valueIfNull) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? valueIfNull : Long.valueOf(value.toString());
		}

		public Integer getInteger(String columnName) {
			return getInteger(columnName, null);
		}
		
		public Integer getInteger(String columnName, Integer valueIfNull) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? valueIfNull : Integer.valueOf(value.toString());
		}
		
		public Double getDouble(String columnName) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? null : Double.valueOf(value.toString());
		}

		public Boolean getBoolean(String columnName) {
			return getBoolean(columnName, null);
		}
		
		public Boolean getBoolean(String columnName, Boolean valueIfNull) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? false : Boolean.TRUE.equals(value) ? true : Constants.TRUE_INT.equals(value.toString()) ? true : Constants.TRUE_WORD.equals(value.toString()) ? true : false;
		}

		public LocalDate getDate(String columnName) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? null : ((java.sql.Date) value).toLocalDate();
		}
		
		public LocalTime getTime(String columnName) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? null : ((java.sql.Time) value).toLocalTime();
		}
				
		public LocalDateTime getDateTime(String columnName) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? null : ((java.sql.Timestamp) value).toLocalDateTime();
		}

		public Duration getDuration(String columnName) {
			Object value = columns.get(dataTable.getColumnIndex(columnName));
			return isDBNull(value) ? null : Duration.parse(value.toString());
		}
		
		public Array getArray(String columnName) {
            Object value = columns.get(dataTable.getColumnIndex(columnName));
            return isDBNull(value) ? null : (Array) value;
        }
        
        @SuppressWarnings("unchecked")
        public <E> E[] getArrayOfType(String columnName, Class<E> clazz) throws SQLException {
            return  (E[]) getArray(columnName).getArray();
        }

		private static boolean isDBNull(Object value) {
			return value == null || StringUtils.isBlank(value.toString());
		}

	}

	/**
	 * Contains only the needed metadata about the query result column
	 */
	private static class ColumnInfo {

		private String name;
		private String label;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
