package org.futurist.util.jobs;

import java.sql.Date;

public abstract class Job implements Runnable {
	
	protected Integer id;
	protected String name;
	protected Long interval;
	protected Date firstRun;
	protected Date nextRun;
	protected Date prevRun;
	
	/**
	 * Default Constructor to create an abstract job that can be run on a processor.
	 * @param n the name of this Job.
	 * @param i the interval in ms between executions of this Job.
	 * @param f the Date of this job's first execution.
	 */
	public Job(String n, Long i, Date f) {
		super();
		name = n;
		interval = i;
		firstRun = f;
		nextRun = new Date(System.currentTimeMillis() + interval);
		prevRun = null;
	}
	
	/**
	 * @return the ID
	 */
	public Integer getID() {
		return id;
	}
	
	/**
	 * @return the name
	 */
	public String getJobName() {
		return name;
	}
	/**
	 * @param n the name to set
	 */
	public void setJobName(String n) {
		name = n;
	}

	/**
	 * @return the first run date/timestamp
	 */
	public Date getFirstRun() {
		return firstRun;
	}
	
	/**
	 * @return the next scheduled run date/timestamp
	 */
	public Date getNextRun() {
		return nextRun;
	}
	
	/**
	 * @return the previous completed run date/timestamp
	 */
	public Date getPrevRun() {
		return prevRun;
	}
}