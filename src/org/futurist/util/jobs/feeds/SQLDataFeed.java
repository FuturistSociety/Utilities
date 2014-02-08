package org.futurist.util.jobs.feeds;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.futurist.util.SQLHelper;
import org.futurist.util.jobs.Job;

public class SQLDataFeed extends Job {

	// source DB variables
	protected SQLHelper srcSQL;
	protected String[] srcTables;
	protected String[] srcCols;
	protected String[] srcWhereFields;
	protected String[] srcWhereOperators;
	protected String[] srcWhereValues;

	// destination DB variables
	protected SQLHelper dstSQL;
	protected String[] dstTables;
	protected String[] dstCols;

	/**
	 * Default constructor to create a SQLDataFeed from the source database to the destination database.
	 * @param n the name of this Job.
	 * @param i the interval in ms between executions of this Job.
	 * @param f the Date of this job's first execution.
	 * @param srcDB the source database's connection helper.
	 * @param srcTab the source tables of interest in <code>srcDB</code>.
	 * @param srcCols the names of the source columns of interest in <code>srcTab</code>.
	 * @param dstDB the destination database's connection helper.
	 * @param dstTab the destination tables of interest in <code>dstDB</code>.
	 * @param dstCols the names of the destination columns of interest in <code>dstTab</code>.
	 */
	public SQLDataFeed(String n, Long i, Date f, SQLHelper srcDB, String[] srcTab, String[] srcCols, SQLHelper dstDB, String[] dstTab, String[] dstCols) {
		super(n, i, f);
		srcSQL = srcDB;
		srcTables = srcTab;
		this.srcCols = srcCols;
		dstSQL = dstDB;
		dstTables = dstTab;
		this.dstCols = dstCols;
	}

	/**
	 * Constructor to create a SQLDataFeed from the source database to the destination database.
	 * @param n the name of this Job.
	 * @param i the interval in ms between executions of this Job.
	 * @param f the Date of this job's first execution.
	 * @param srcDBServer the source database server name or IP address.
	 * @param srcDBName the source database name.
	 * @param srcUser the source username.
	 * @param srcPass the source password.
	 * @param srcTab the source tables of interest in <code>srcDB</code>.
	 * @param srcCols the names of the source columns of interest in <code>srcTab</code>.
	 * @param dstDBServer the destination database server name or IP address.
	 * @param dstDBName the destination database name.
	 * @param dstUser the destination username.
	 * @param dstPass the destination password.
	 * @param dstDB the destination database's connection helper.
	 * @param dstTab the destination tables of interest in <code>dstDB</code>.
	 * @param dstCols the names of the destination columns of interest in <code>dstTab</code>.
	 */
	public SQLDataFeed(String n, Long i, Date f, String srcDBServer, String srcDBName, String srcUser, String srcPass, String[] srcTab, String[] srcCols, String dstDBServer, String dstDBName, String dstUser, String dstPass, String[] dstTab, String[] dstCols) {
		this(n, i, f, new SQLHelper(srcDBServer, srcDBName, srcUser, srcPass), srcTab, srcCols, new SQLHelper(dstDBServer, dstDBName, dstUser, dstPass), dstTab, dstCols);
	}

	/**
	 * Run this Job if the next schedule run time has come (or passed).
	 */
	public void run() {
		Date now = new Date(System.currentTimeMillis());
		if(now.after(nextRun)) {
			for(int t = 0; t < srcTables.length; t++) {
				ResultSet rs = srcSQL.selectWhere(srcTables[t], srcCols, srcWhereFields, srcWhereOperators, srcWhereValues);
				try {
					while(rs.next()) {
						for(Integer c = 0; c < dstCols.length; c++) {
							dstSQL.insert(dstTables[t], new String[]{c.toString()}, new String[]{rs.getString(c)});
						}
					}
				} catch(SQLException e) {
					System.out.println("SQLException: " + e.getMessage());
					System.out.println("SQLState: " + e.getSQLState());
				}
			}
			prevRun = new Date(System.currentTimeMillis());
			nextRun = new Date(System.currentTimeMillis() + interval);
		}
	}
}