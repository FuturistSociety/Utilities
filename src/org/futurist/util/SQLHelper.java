package org.futurist.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLHelper {

	private String server;
	private String user;
	private String pass;
	private String db;

	public Connection mysql;
	public PreparedStatement ps;
	public ResultSet rs;

	/**
	 * Default constructor to create a SQLHelper.
	 * @param dbServer the database server name or IP address.
	 * @param dbName the database name.
	 * @param user the username.
	 * @param pass the password.
	 */
	public SQLHelper(String dbServer, String dbName, String u, String p) {
		server = dbServer;
		user = u;
		pass = p;
		db = dbName;

		try {
			mysql = DriverManager.getConnection("jdbc:mysql://" + server + "/" + db + "?user=" + user + "&password=" + pass);
		} catch(SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}

	/**
	 * Get the server's hostname or IP address.
	 * @return the server hostname or IP address
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Get the username used for this connection.
	 * @return the username
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Get the password used for this connection.
	 * @return the password
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * Get the database name used for this connection
	 * @return the database name
	 */
	public String getDatabase() {
		return db;
	}
	
	/**
	 * Get usage and load statistics from this connection.
	 * @return the statistics.
	 */
	public String getStats() throws SQLException {
		String results = "";		
		ps = mysql.prepareStatement("SHOW GLOBAL STATUS");
		ResultSet r = ps.executeQuery();
		
		while(r.next()) {			
			if(r.getString(1).equals("Bytes_received")) {
				results += "Bytes Received:\t" + r.getString(2) + "\n";
			}
			if(r.getString(1).equals("Bytes_sent")) {
				results += "Bytes Sent:\t" + r.getString(2) + "\n";
			}
			if(r.getString(1).equals("Open_files")) {
				results += "Open Files:\t" + r.getString(2) + "\n";
			}
			if(r.getString(1).equals("Open_tables")) {
				results += "Open Tables:\t" + r.getString(2) + "\n";
			}
			if(r.getString(1).equals("Questions")) {
				results += "Statements Executed:\t" + r.getString(2) + "\n";
			}
			if(r.getString(1).equals("Slow_queries")) {
				results += "Slow Queries:\t" + r.getString(2) + "\n";
			}
			if(r.getString(1).equals("Threads_connected")) {
				results += "Open Connections:\t" + r.getString(2) + "\n";
			}
			if(r.getString(1).equals("Threads_running")) {
				results += "Threads Running:\t" + r.getString(2) + "\n";
			}
			if(r.getString(1).equals("Uptime")) {
				results += "Uptime:\t" + r.getString(2) + "\n";
			}
		
		}
		
		return results;
	}
	
	/**
	 * Check whether the database is connected. 
	 * @return true if the database connection has not been closed; true otherwise.
	 */
	public Boolean connected() {
		Boolean result = true;
	
			try {
				if(mysql.isClosed()) {
					result = false;
				}
			} catch (SQLException e) {
				result = false;
			}
			
		return result;
	}

	/**
	 * Select the given columns from the given table.
	 * @param colNames the names of the columns you wish to select.
	 * @param table the table containing the columns you wish to select.
	 * @return the <code>ResultSet</code> created by executing the SELECT statement.
	 * @throws SQLException
	 */
	public ResultSet select(String[] colNames, String table) throws SQLException {
		String query = "SELECT ";
		for(String c : colNames) {
			query += c + ", ";
		}
		query = query.substring(0, query.lastIndexOf(","));
		query += " FROM " + table;

		ps = mysql.prepareStatement(query);	
		return ps.executeQuery();
	}

	/**
	 * Select the given columns from the given table that match the conditions set by the where clause.
	 * @param table the table containing the columns you wish to select.
	 * @param colNames the names of the columns you wish to select.
	 * @param whereFields the column names used to filter selections.
	 * @param whereOperators the operators used for comparison like >, >=, =, <>, <, <=, LIKE, BETWEEN, and IN.
	 * @param whereValues the values used to filter selections in the given whereFields.
	 * @return the <code>ResultSet</code> created by executing the SELECT WHERE statement.
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public ResultSet selectWhere(String table, String[] colNames, String[] whereFields, String[] whereOperators, String[] whereValues) {
		ResultSet r = null;

		String query = "SELECT ";
		for(String c : colNames) {
			query += c + ", ";
		}
		query = query.substring(0, query.lastIndexOf(","));
		query += " FROM " + table;
		query += " WHERE ";
		for(int i = 0; i < whereFields.length; i++ ){ 
			query += whereFields[i] + " " + whereOperators[i] + "'" + whereValues[i] + "'";
		}

		try {
			ps = mysql.prepareStatement(query);
			r = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return r;
		}
	}
	
	@SuppressWarnings("finally")
	/**
	 * Select all rows from the srcTable in the current database and copy them into the destination table in the destination database. 
	 * @param srcTable the source table in the current DB.
	 * @param dstDB the destination DB.
	 * @param dstTable the destination table in the destinationDB.
	 * @return the <code>ResultSet</code> created by executing the SELECT INTO statement.
	 */
	public ResultSet selectInto(String srcTable, String dstDB, String dstTable) {
		ResultSet r = null;
		
		String query = "SELECT * INTO" + dstTable + " IN " + dstDB + " FROM " + srcTable;
		try {
			ps = mysql.prepareStatement(query);
			r = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return r;
		}
	}
	
	@SuppressWarnings("finally")
	/**
	 * Select matching rows from the srcTable in the current database and copy them into the destination table in the destination database. 
	 * @param srcTable the source table in the current DB.
	 * @param colNames the names of the columns you wish to select.
	 * @param dstDB the destination DB.
	 * @param dstTable the destination table in the destinationDB.
	 * @param whereFields the column names used to filter selections.
	 * @param whereOperators the operators used for comparison like >, >=, =, <>, <, <=, LIKE, BETWEEN, and IN.
	 * @param whereValues the values used to filter selections in the given whereFields.
	 * @return the <code>ResultSet</code> created by executing the SELECT INTO WHERE statement.
	 */
	public ResultSet selectIntoWhere(String srcTable, String[] colNames, String dstDB, String dstTable, String[] whereFields, String[] whereOperators, String[] whereValues) {
		ResultSet r = null;
		
		String query = "SELECT ";
		for(String c : colNames) {
			query += c + ", ";
		}
		query = query.substring(0, query.lastIndexOf(","));
		query += " INTO " + dstTable + " IN " + dstDB + " FROM " + srcTable;
		query += " WHERE ";
		for(int i = 0; i < whereFields.length; i++ ){ 
			query += whereFields[i] + " " + whereOperators[i] + "'" + whereValues[i] + "'";
		}
		
		try {
			ps = mysql.prepareStatement(query);
			r = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return r;
		}
	}

	/**
	 * Insert the given values into the given columns in the given table.
	 * @param table the table into which you wish to insert the given values.
	 * @param colNames the names of the columns into which you wish to insert values.
	 * @param values the values to insert into the given table.
	 * @return the <code>ResultSet</code> created by executing the INSERT statement.
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public ResultSet insert(String table, String[] colNames, String[] values) {
		ResultSet r = null;

		String query = "INSERT INTO" + table + " (";
		for(String c : colNames) {
			query += c + ", ";
		}
		query = query.substring(0, query.lastIndexOf(","));
		query += ") VALUES (";
		for(int i = 0; i < values.length; i++ ){ 
			Double numVal = Double.valueOf(values[i]);
			if(numVal != null && numVal != Double.NaN) {
				query += values[i];
			} else {
				query += "'" + values[i] + "'";
			}
		}
		query += ")";

		try {
			ps = mysql.prepareStatement(query);
			r = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return r;
		}	
	}

	/**
	 * Delete the given columns from the given table that match the conditions set by the where clause.
	 * @param table the table containing the columns you wish to delete.
	 * @param colNames the names of the columns you wish to delete.
	 * @param whereFields the column names used to filter selections.
	 * @param whereOperators the operators used for comparison like >, >=, =, <>, <, <=, LIKE, BETWEEN, and IN.
	 * @param whereValues the values used to filter selections in the given whereFields.
	 * @return the <code>ResultSet</code> created by executing the DELETE WHERE statement.
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public ResultSet deleteWhere(String table, String[] colNames, String[] whereFields, String[] whereOperators, String[] whereValues) {
		ResultSet r = null;

		String query = "DELETE FROM " + table + " WHERE ";
		for(int i = 0; i < whereFields.length; i++ ){ 
			query += whereFields[i] + " " + whereOperators[i];
			Double numVal = Double.valueOf(whereValues[i]);
			if(numVal != null && numVal != Double.NaN) {
				query += whereValues[i];
			} else {
				query += "'" + whereValues[i] + "'";
			}
		}

		try {
			ps = mysql.prepareStatement(query);
			r = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return r;
		}	
	}

	/**
	 * Update the given columns from the given table that match the conditions set by the where clause.
	 * @param table the table containing the columns you wish to update.
	 * @param colNames the names of the columns you wish to update.
	 * @param whereFields the column names used to filter selections.
	 * @param whereOperators the operators used for comparison like >, >=, =, <>, <, <=, LIKE, BETWEEN, and IN.
	 * @param whereValues the values used to filter selections in the given whereFields.
	 * @return the <code>ResultSet</code> created by executing the UPDATE WHERE statement.
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public ResultSet update(String table, String[] colNames, String[] newValues, String[] whereFields, String[] whereOperators, String[] whereValues) {
		ResultSet r = null;

		String query = "UPDATE " + table + " SET ";
		for(int i = 0; i < newValues.length; i++ ){ 
			query += colNames[i] + "=";
			Double numVal = Double.valueOf(newValues[i]);
			if(numVal != null && numVal != Double.NaN) {
				query += newValues[i];
			} else {
				query += "'" + newValues[i] + "'";
			}
			query += ", ";
		}
		query = query.substring(0, query.lastIndexOf(","));
		query += " WHERE ";
		for(int j = 0; j < whereFields.length; j++ ){ 
			query += whereFields[j] + " " + whereOperators[j];
			Double numVal = Double.valueOf(whereValues[j]);
			if(numVal != null && numVal != Double.NaN) {
				query += whereValues[j];
			} else {
				query += "'" + whereValues[j] + "'";
			}
		}

		try {
			ps = mysql.prepareStatement(query);
			r = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return r;
		}
	}
	
	/**
	 * Run a custom SQL query.
	 * @param query the SQL query to execute.
	 * @return the <code>ResultSet</code> created by executing the statement.
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public ResultSet query(String query) {
		ResultSet r = null;
		try {
			ps = mysql.prepareStatement(query);
			r = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return r;
		}		
	}

}