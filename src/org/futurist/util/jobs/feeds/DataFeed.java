package org.futurist.util.jobs.feeds;

import java.sql.Date;

public abstract class DataFeed implements Runnable {
	
	protected Integer interval;
	protected Date firstRun;
	protected Date nextRun;
	
	public DataFeed(Integer i, Date f) {
		interval = i;
		firstRun = f;
		nextRun = new Date(System.currentTimeMillis() + interval);
		run();
	}
	
	public void run() {
		Date now = new Date(System.currentTimeMillis());
		if(now.after(nextRun)) {
			nextRun = new Date(System.currentTimeMillis() + interval);
		}
	}

}
