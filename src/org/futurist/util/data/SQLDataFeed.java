package org.futurist.util.data;

import java.sql.Date;
import java.util.ArrayList;

public abstract class SQLDataFeed extends DataFeed {
	
	protected String user;
	protected String pass;
	protected String server;
	protected String dbName;
	protected String srcTable;
	protected String dstTable;
	protected ArrayList<Integer> srcRowIDs;
	protected ArrayList<Integer> srcColIDs;
	protected Boolean connected;
	
	public SQLDataFeed(Integer i, Date f, String u, String p, String db, String src, String dst) {
		super(i, f);
	
		user= u;
		pass = p;
		dbName = db;
		srcTable = src;
		dstTable = dst;
		connected = false;
	}

}
