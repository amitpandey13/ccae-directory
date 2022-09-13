package com.pdgc.db;

import static com.pdgc.general.lookup.Constants.ENV_PROPERTY_FILE_DIRECTORY_PATH;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.NamedParameterStatement;
import com.pdgc.db.structures.QueryParameters;
import com.pdgc.db.structures.QueryType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * TODO: this class is basically a merge of DaoManager and BaseDAO.
 * They should be consolidated, though that's currently not 
 * possible b/c of the way project dependencies are set up
 * @author Linda Xu
 *
 */
public class DBUtil {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(DBUtil.class);

	private static final String HIKARI_PROPERTY_FILE_NAME = "hikari.properties";
    private static final String HIKARI_READ_ONLY_PROPERTY_FILE_NAME = "hikari_read_only.properties";
	
    private final String propertyFile;
	private DataSource dataSource;
	
	public static final DBUtil READ_ONLY_CONNECTION = new DBUtil(true);
	
	private DBUtil(boolean isReadOnly) {
	    if (isReadOnly) {
	        propertyFile = HIKARI_READ_ONLY_PROPERTY_FILE_NAME;
	    } else {
	        propertyFile = HIKARI_PROPERTY_FILE_NAME;
	    }
	}

	public void executeUpdate(String sql) {
		try (Connection c = getConnection();) {
			executeUpdate(sql, c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing update: {} ", sql, e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public void executeUpdate(String sql, Connection connection) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		executeUpdateWithParameters(sql, parameters, connection);
	}
	
	/**
	 * Allows the ability to grab a connection when updating
	 * @param sql
	 * @param parameters
	 */
	public void executeUpdateWithParameters(String sql, Map<String, Object> parameters) {
		try (Connection c = getConnection(); ) {
			executeUpdateWithParameters(sql, parameters, c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing update: {} ", sql, e);
			throw new RuntimeException(e);
		} 
	}

	/**
	 * Batch updates.
	 * @param sql
	 * @param parameters
	 */
	public DataTable executeUpdateWithParameters(String sql, Collection<Map<String, Object>> parameters) {
		try (Connection c = getConnection(); ) {
			return executeUpdateWithParameters(sql, parameters, c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing update: {} ", sql, e);
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * Batch updates.
	 * @param sql
	 * @param parameters
	 * @param conn
	 */
	public DataTable executeUpdateWithParameters(String sql, Collection<Map<String, Object>> parameters, Connection conn) {
		try {
			NamedParameterStatement stmt = new NamedParameterStatement(conn, sql, Statement.RETURN_GENERATED_KEYS);
			for (Map<String, Object> parameterMap : parameters) {
				for (Entry<String, Object> paramEntry : parameterMap.entrySet()) {
					if (paramEntry.getValue() == null) {
						stmt.setObject(paramEntry.getKey(), null);
					}
					else if (paramEntry.getValue().getClass().isArray()) {
						// by default create array of text, the query should however
						// use explicit casting to required type
						stmt.setObject(paramEntry.getKey(), conn.createArrayOf("text", (Object[]) paramEntry.getValue()));
					} 
					else {
						stmt.setObject(paramEntry.getKey(), paramEntry.getValue());
						LOGGER.debug(stmt.getStatement().toString());
					}
				}
				stmt.addBatch();
			}
			LOGGER.debug(stmt.getStatement().unwrap(PreparedStatement.class).toString());
			stmt.executeBatch();
			ResultSet rs = stmt.getStatement().getGeneratedKeys();
			DataTable dataTable = new DataTable();
			dataTable.load(rs);
			conn.commit();
			stmt.close();
			LOGGER.debug("Number of rows affected: " + dataTable.getRowCount());
			return dataTable;
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing query: " + sql, e);
			throw new RuntimeException(e);
		}
	}
	
	
	
	/**
	 * Allows the ability to grab a connection when updating
	 * @param sql
	 * @param parameters
	 */
	public void executeUpdateWithParameters(QueryParameters queryParameters) {
		try (Connection c = getConnection(); ) {
			executeUpdateWithParameters(queryParameters.getQuery(), queryParameters.getParameters(), c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing update: {} ", queryParameters.getQuery(), e);
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * Execute an insert or update statement.
	 * @param sql
	 * @param parameters the set of values to replace variables in the query with.
	 * @param connection
	 */
	public void executeUpdateWithParameters(String sql, Map<String, Object> parameters, Connection conn) {
		try {
			NamedParameterStatement stmt = new NamedParameterStatement(conn, sql);
			for (Entry<String, Object> paramEntry : parameters.entrySet()) {
				if (paramEntry.getValue() == null) {
					stmt.setObject(paramEntry.getKey(), null);
				}
				else if (paramEntry.getValue().getClass().isArray()) {
					// by default create array of text, the query should however
					// use explicit casting to required type
					stmt.setObject(paramEntry.getKey(), conn.createArrayOf("text", (Object[]) paramEntry.getValue()));
				} 
				else {
					stmt.setObject(paramEntry.getKey(), paramEntry.getValue());
				// Too verbose	LOGGER.debug(stmt.getStatement().toString());
				}
			}    
			LOGGER.debug(stmt.getStatement().unwrap(PreparedStatement.class).toString());
			stmt.executeUpdate();
			stmt.close();
			conn.commit();    // The caller needs to be in control of the commt here! we need to remove these for sure..
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing query: " + sql, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Execute an insert or update statement.  This statement takes a paremeter list and replaces ? variables with the values in the list.
	 * @param sql
	 * @param parameters the set of values to replace variables in the query with.
	 * @param connection
	 */
	public int executeUpdateWithParameters(String sql, List<Object> parameters) {
		try (Connection c = getConnection(); PreparedStatement stmt = c.prepareStatement(sql);) {
			for (int i = 0; i < parameters.size(); i++) {
				stmt.setObject(i + 1, parameters.get(i));
			}
			LOGGER.debug(stmt.toString());
			int recordsUpdated = stmt.executeUpdate();
			stmt.close();
			//TODO remove this commit!!!!!!!!
			c.commit();    // The caller needs to be in control of the commt here! we need to remove these for sure..
			return recordsUpdated;
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing query: " + sql, e);
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Execute an insert or update statement.
	 * @param sql
	 * @param parameters the set of values to replace variables in the query with.
	 * @param connection
	 */
	public DataTable executeUpdatesWithParameters(List<Triplet<String, QueryType, Map<String, Object>>> sqlWithParameters) {
		try (Connection c = getConnection()) {
			ResultSet rs = null;
			DataTable dt = new DataTable();
			StringBuffer sb = new StringBuffer();
			for (Triplet<String, QueryType, Map<String, Object>> sqlParameter : sqlWithParameters){
				try (NamedParameterStatement stmt = new NamedParameterStatement(c, sqlParameter.getValue0());) {
					for (Entry<String, Object> paramEntry : sqlParameter.getValue2().entrySet()) {
						if (paramEntry.getValue() == null) {
							stmt.setObject(paramEntry.getKey(), null);
						}
						else if (paramEntry.getValue().getClass().isArray()) {
							// by default create array of text, the query should however
							// use explicit casting to required type
							stmt.setObject(paramEntry.getKey(), c.createArrayOf("text", (Object[]) paramEntry.getValue()));
						} 
						else {
							stmt.setObject(paramEntry.getKey(), paramEntry.getValue());
						}
					}
					sb.append(stmt.getStatement().unwrap(PreparedStatement.class).toString() + "; \n");
					//Dont print the statement if this is a debug query as we print the statement along with the result when the query is executed.
					if (sqlParameter.getValue1() != QueryType.DEBUG)
						LOGGER.debug(stmt.getStatement().unwrap(PreparedStatement.class).toString());

					if (sqlParameter.getValue1() == QueryType.DDL)
						stmt.getStatement().execute();
					//If the caller included a commit directive, then execute a commit after the main query is run.
					else if (sqlParameter.getValue1() == QueryType.COMMIT) {
						c.commit();
						LOGGER.debug("Commit executed");
					}
					else if (sqlParameter.getValue1() == QueryType.UPDATE)
						stmt.getStatement().executeUpdate();
					//Print out the results of a sql statement - must return a single value as a string.
					else if (sqlParameter.getValue1() == QueryType.DEBUG) { 
						ResultSet debugRs = stmt.executeQuery();
						if (debugRs.next()) LOGGER.debug(stmt.getStatement().unwrap(PreparedStatement.class).toString() + ": " + (String) debugRs.getObject(1));
						debugRs.close();
					}
					else {
						rs = stmt.executeQuery();
						dt.load(rs);
						LOGGER.debug("Number of rows affected: " + dt.getRowCount());
					}
					stmt.close();
				}
				catch (Exception e) {
					LOGGER.error("Error occurred while executing query: " + e.getMessage(), e);
					throw new RuntimeException(e);
				}
			}
			dt.setQuery(sb.toString());
			return dt;
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while executing query: " + e.getMessage(), e);
		}
		return null;
	}
	
	public DataTable executeQuery(String sql) {
		try (Connection c = getConnection();) {
			return executeQuery(sql, c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing query: {} ", sql, e);
			throw new RuntimeException(e);
		}
	}
	/**
	 * Executes a query and places the data into a DataTable for further processing by the caller. This minimizes the time of an open
	 * connection, allowing the connection pool to be more free.
	 * 
	 * @param sql
	 *            Query
	 * @return Data table of results
	 */
	public DataTable executeQuery(String sql, Connection c) {
		// TODO Vishal Raut, Mar 22, 2017: Check logging
		LOGGER.debug(sql);
		try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			DataTable dataTable = new DataTable();
			dataTable.setQuery(sql);
			dataTable.load(rs);
			LOGGER.debug("Number of rows affected: " + dataTable.getRowCount());
			return dataTable;
		} catch (Exception e) {
			System.err.println("Error occurred while executing query: " + e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Executes a query asynchronously and places the data into a DataTable for further processing by the caller. This minimizes the time of
	 * an open connection, allowing the connection pool to be more free.
	 * 
	 * @param sql
	 *            Query
	 * @return CompletableFuture which can be queried for the result
	 */
	public CompletableFuture<DataTable> executeQueryAsync(String sql) {
		return CompletableFuture.supplyAsync(new Supplier<DataTable>() {

			@Override
			public DataTable get() {
				return executeQuery(sql);
			}

		});

	}

	public DataTable executeQueryWithParameters(String sql, Map<String, Object> parameters) {
		try (Connection c = getConnection(); ) {
			return executeQueryWithParameters(sql, parameters, c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing query: {} ", sql, e);
			throw new RuntimeException(e);
		}
	}
	
	public DataTable executeQueryWithParameters(QueryParameters queryParameters) {
		try (Connection c = getConnection(); ) {
			return executeQueryWithParameters(queryParameters.getQuery(), queryParameters.getParameters(), c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing query: {} ", queryParameters.getQuery(), e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Executes a query with parameters and places the data into a DataTable for further processing by the caller. This minimizes the time
	 * of an open connection, allowing the connection pool to be more free.
	 * 
	 * @param sql
	 *            Query
	 * @param parameters
	 *            Collection of parameters
	 * @return Data table of results
	 */
	public DataTable executeQueryWithParameters(String sql, Map<String, Object> parameters, Connection c) {
		ResultSet rs = null;
		try (NamedParameterStatement stmt = new NamedParameterStatement(c, sql)) {
			for (Entry<String, Object> paramEntry : parameters.entrySet()) {
				if (paramEntry.getValue().getClass().isArray()) {
					// by default create array of text, the query should however
					// use explicit casting to required type
					stmt.setObject(paramEntry.getKey(), c.createArrayOf("text", (Object[]) paramEntry.getValue()));
				} else {
					stmt.setObject(paramEntry.getKey(), paramEntry.getValue());
				}
			}
			// TODO Vishal Raut, Mar 22, 2017: Check logging
			LOGGER.debug(stmt.getStatement().unwrap(PreparedStatement.class).toString());
			rs = stmt.executeQuery();
			DataTable dataTable = new DataTable();
			dataTable.setQuery(stmt.getStatement().unwrap(PreparedStatement.class).toString());
			dataTable.load(rs);
			c.commit(); // we have to do this right now because we are getting rollbacks on dirty reads.
			LOGGER.debug("Number of rows affected: " + dataTable.getRowCount());
			return dataTable;
		} catch (Exception e) {
			System.err.println("Error occurred while executing query: " + e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Executes a query with parameters and places the data into a DataTable for further processing by the caller. This minimizes the time
	 * of an open connection, allowing the connection pool to be more free.
	 * 
	 * @param sql
	 *            Query
	 * @param parameters
	 *            Varargs represent param key followed by param value
	 * @return Data table of results
	 */
	public DataTable executeQueryWithParameters(String sql, Object... parameters) {
		return executeQueryWithParameters(sql, buildQueryParameters(parameters));
	}

	/**
	 * Executes a query asynchronously with parameters and places the data into a DataTable for further processing by the caller. This
	 * minimizes the time of an open connection, allowing the connection pool to be more free.
	 * 
	 * @param sql
	 *            Query
	 * @param parameters
	 *            Collection of parameters
	 * @return CompletableFuture which can be queried for the result
	 */
	public CompletableFuture<DataTable> executeQueryWithParametersAsync(final String sql, final Map<String, Object> parameters) {

		return CompletableFuture.supplyAsync(new Supplier<DataTable>() {

			@Override
			public DataTable get() {
				return executeQueryWithParameters(sql, parameters);
			}

		});

	}

	/**
	 * Executes a query asynchronously with parameters and places the data into a DataTable for further processing by the caller. This
	 * minimizes the time of an open connection, allowing the connection pool to be more free.
	 * 
	 * @param sql
	 *            Query
	 * @param parameters
	 *            Varargs represent param key followed by param value
	 * @return CompletableFuture which can be queried for the result
	 */
	public CompletableFuture<DataTable> executeQueryWithParametersAsync(final String sql, final Object... parameters) {
		return executeQueryWithParametersAsync(sql, buildQueryParameters(parameters));
	}

	private static Map<String, Object> buildQueryParameters(Object... params) {
		Map<String, Object> parameters = new HashMap<>(params.length);
		for (int i = 0; i < params.length; i += 2) {
			parameters.put(params[i].toString(), params[i + 1]);
		}
		return parameters;
	}

	public Connection getConnection() throws SQLException {
		if (dataSource == null) {
		    dataSource = new HikariDataSource(new HikariConfig(getHikariConfigFilePath()));
		}
		return dataSource.getConnection();
	}

	private String getHikariConfigFilePath() {
		String fileDirPath = System.getenv(ENV_PROPERTY_FILE_DIRECTORY_PATH);
		String filePath;
		if (StringUtils.isBlank(fileDirPath)) {
			filePath = "." + File.separator + propertyFile;
		} else {
			filePath = fileDirPath + File.separator + propertyFile;
		}
		LOGGER.debug("Load DB property file from {}", filePath);
		return filePath;
	}

	/**
	 * Closes the underlying {@link DataSource}.
	 */
	public void closeDataSource() {
		if (dataSource != null) {
			if (dataSource instanceof HikariDataSource) {
				((HikariDataSource) dataSource).close();
			} else {
				throw new UnsupportedOperationException("Unsupported DataSource");
			}
			dataSource = null;
		}
	}
	
	/**
	 * Updates the database and returns the updated rows
	 * @param sql
	 * @return
	 */
	public DataTable executeUpdateReturn(String sql) {
		try (Connection c = getConnection();) {
			return executeUpdateReturn(sql, c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing query: {} ", sql, e);
			throw new RuntimeException(e);
		}
	}
	/**
	 * Executes a update and places the data into a DataTable for further processing by the caller
	 * 
	 * @param sql
	 *            Query
	 * @return Data table of results
	 */
	public DataTable executeUpdateReturn(String sql, Connection c) {
		// TODO Vishal Raut, Mar 22, 2017: Check logging
		LOGGER.debug(sql);
		try (Statement stmt = c.createStatement();) {
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			DataTable dataTable = new DataTable();
			dataTable.load(rs);
			c.commit();
			stmt.close();
			LOGGER.debug("Number of rows affected: " + dataTable.getRowCount());
			return dataTable;
		} catch (Exception e) {
			System.err.println("Error occurred while executing query: " + e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Executes an update with parameters and returns all affected rows.
	 * 
	 * @param queryParameters
	 * @return
	 */
	public DataTable executeUpdateReturnWithParameters(QueryParameters queryParameters) {
		try (Connection c = getConnection();) {
			return executeUpdateReturnWithParameters(queryParameters.getQuery(), queryParameters.getParameters(), c);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing query: {} ", queryParameters.getQuery(), e);
			throw new RuntimeException(e);
		}
	}
	
	private static DataTable executeUpdateReturnWithParameters(String sql, Map<String, Object> parameters, Connection c) {
		try (NamedParameterStatement stmt = new NamedParameterStatement(c, sql, Statement.RETURN_GENERATED_KEYS);) {
			for (Entry<String, Object> paramEntry : parameters.entrySet()) {
				if (paramEntry.getValue() == null) {
					stmt.setObject(paramEntry.getKey(), null);
				}
				else if (paramEntry.getValue().getClass().isArray()) {
					// by default create array of text, the query should however
					// use explicit casting to required type
					stmt.setObject(paramEntry.getKey(), c.createArrayOf("text", (Object[]) paramEntry.getValue()));
				} 
				else {
					stmt.setObject(paramEntry.getKey(), paramEntry.getValue());
				//	LOGGER.debug(stmt.getStatement().toString());
				}
			}
			LOGGER.debug(stmt.getStatement().unwrap(PreparedStatement.class).toString());
			stmt.executeUpdate();
			ResultSet rs = stmt.getStatement().getGeneratedKeys();
			DataTable dataTable = new DataTable();
			dataTable.load(rs);
			c.commit();
			stmt.close();
			LOGGER.debug("Number of rows affected: " + dataTable.getRowCount());
			return dataTable;
		} catch (Exception e) {
			System.err.println("Error occurred while executing query: " + e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Will put in the format taken by the SQL:  
	 *  
	 *  WHERE pmtlId =  any (VALUES (1), (2), (3) ) 
	 *  
	 * @param vals
	 * @return
	 */
	public static String valuesAnySQLFormat(Collection<? extends Number> vals) {
		if(vals.isEmpty())
			return null;
		
		StringBuilder str = new StringBuilder();
		for(Number val : vals) {
			str.append("(").append(val).append(")")
			.append(",");
		}
		str.deleteCharAt(str.length() - 1);
		return str.toString();
	}
} 
