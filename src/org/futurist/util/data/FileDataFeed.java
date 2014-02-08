package org.futurist.util.data;

import java.io.File;
import java.sql.Date;

import org.futurist.util.math.DataLoader;

public abstract class FileDataFeed extends DataFeed {
	
	protected String dataDir = "data/";
	protected DataLoader loader;
	protected File output;
	
	public FileDataFeed(Integer i, Date f, DataLoader l, String outName) {
		super(i, f);
		loader = l;
		output = new File(dataDir + outName);
	}
	
	public abstract void readFile();
	public abstract void writeFile();

}
