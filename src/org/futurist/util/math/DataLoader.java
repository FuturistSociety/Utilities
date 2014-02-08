/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util.math;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DataLoader {

	private File input;
	private String delimiter;
	private ArrayList<String> headers;
	private ArrayList<String[]> data;
	
	/**
	 * Default constructor to load data from the given file by treating each line as a single observation of however many parameters split around the given delimiter.
	 * @param in the input <code>File</code>.
	 * @param d the delimiter used to demarcate where one parameter ends and another parameter begins in a single observation.
	 */
	public DataLoader(File in, String d) {
		input = in;
		delimiter = d;
		FileReader fileReader;
		BufferedReader buffReader;
		try {
			fileReader = new FileReader(input);
			buffReader = new BufferedReader(fileReader);
			headers = new ArrayList<String>();
			data = new ArrayList<String[]>();
			
			// read headers
			if(buffReader.ready()) {
				String line[] = buffReader.readLine().split(delimiter);
				for(String h : line) { headers.add(h); }
			}
			
			// read data
			while(buffReader.ready()) {
				data.add(buffReader.readLine().split(delimiter));
			}
			
			buffReader.close();
			fileReader.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns the data headers for each column.
	 * @return the headers read from the first line of the input file.
	 */
	public ArrayList<String> getHeaders() {
		return headers;
	}
	
	/**
	 * Returns the data read from the input file.  The <code>ArrayList</code> contains observations.  Each <code>String[]</code> is a single observation of all parameters.
	 * @return the data.
	 */
	public ArrayList<String[]> getData() {
		return data;
	}
	
	/**
	 * Returns the contents of the file as a <code>ConcurrentHashMap</code> of data header -> data observation pairs.
	 * @return the contents of the file as a <code>ConcurrentHashMap</code> of data header -> data observation pairs.
	 */
	public ConcurrentHashMap<String, String[]> getDatasetAsHashMap() {
		ConcurrentHashMap<String, String[]> dataset = new ConcurrentHashMap<String, String[]>();
		for(int j = 0; j < headers.size(); j++) {
			String[] paramObs = new String[data.size()];
			for(int i = 0; i < data.size(); i++) {
				paramObs[i] = data.get(i)[j];
			}
			dataset.put(headers.get(j), paramObs);
		}
		return dataset;
	}
	
}