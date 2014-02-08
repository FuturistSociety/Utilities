package org.futurist.util.jobs.feeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;

import org.futurist.util.jobs.Job;

public class HTTPDataFeed extends Job {
	
	protected final String dataDir = "data/";
	protected URL input;
	protected String output;
	protected String wget;
	
	/**
	 * Default constructor to create an SQLBackupJob using <code>wget</code>.
	 * @param i the interval in ms between executions of this Job.
	 * @param f the Date of this job's first execution.
	 * @param u the URL source to download.
	 * @param o the full path to the output file's location.
	 */
	public HTTPDataFeed(String n, Long i, Date f, URL u, String o) {
		super(n, i, f);
		input = u;
		output = o;
		wget = "wget -nc -p --convert-links -o " + output;
	}
	
	/**
	 * Constructor to create an SQLBackupJob using <code>wget</code>.
	 * @param n the name of this Job.
	 * @param i the interval in ms between executions of this Job.
	 * @param f the Date of this job's first execution.
	 * @param protocol the protocol to use when downloading.
	 * @param protocol the host from which to download.
	 * @param protocol the port to use when downloading.
	 * @param protocol the file to download.
	 * @param o the full path to the output file's location.
	 */
	public HTTPDataFeed(String n, Long i, Date f, String protocol, String host, int port, String file, String outName) throws MalformedURLException {
		this(n, i, f, new URL(protocol, host, port, file), outName);
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
				Process proc = runtime.exec(wget);
			} catch (IOException e) {
				e.printStackTrace();
			}
			prevRun = new Date(System.currentTimeMillis());
			nextRun = new Date(System.currentTimeMillis() + interval);
		}
	}

}