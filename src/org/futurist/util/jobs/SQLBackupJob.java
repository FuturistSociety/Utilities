package org.futurist.util.jobs;

import java.io.IOException;
import java.sql.Date;

import org.futurist.util.SQLHelper;
import org.futurist.util.jobs.Job;

public class SQLBackupJob extends Job {
	
	protected final String dataDir = "data/";
	
	private String user;
	private String pass;
	private String db;
	private String output;
	private String backup;
	
	/**
	 * Default constructor to create a SQLBackupJob using <code>mysqldump</code>.
	 * @param n the name of this Job.
	 * @param i the interval in ms between executions of this Job.
	 * @param f the Date of this job's first execution.
	 * @param u the username.
	 * @param p the password.
	 * @param dbName the name of the database.
	 * @param o the full path to the output file's location.
	 */
	public SQLBackupJob(String n, Long i, Date f, String u, String p, String dbName, String o) {
		super(n, i, f);
		user = u;
		pass = p;
		db = dbName;
		output = o;
		backup = "mysqldump -u" + user + " -p" + pass + " --database " + db + " -r " + output;
	}
	
	/**
	 * Constructor to create a SQLBackupJob using <code>mysqldump</code> from an existing SQLHelper.
	 */
	public SQLBackupJob(String n, Long i, Date f, SQLHelper sql, String o) {
		this(n, i, f, sql.getUser(), sql.getPass(), sql.getDatabase(), o);
	}
	
	/**
	 * Run this Job if the next schedule run time has come (or passed).
	 */
	@SuppressWarnings("unused")
	public void run() {
		Date now = new Date(System.currentTimeMillis());
		if(now.after(nextRun)) {
			Runtime runtime = Runtime.getRuntime();
			try {
				Process proc = runtime.exec(backup);
			} catch (IOException e) {
				e.printStackTrace();
			}
			prevRun = new Date(System.currentTimeMillis());
			nextRun = new Date(System.currentTimeMillis() + interval);
		}
	}

}