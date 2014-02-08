/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DelimitedFileConverter {

	private File input;
	private String inDelimit;
	private File output;
	private String outDelimit;

	/**
	 * Default constructor that takes an existing file originally delimited with the given string and writes its contents to a new file with another given string delimiter. 
	 * @param inFile the existing file (including its extension) originally delimited with the given string
	 * @param inDelimiter the string delimiter for the given existing file
	 * @param outFile the name of the new file to create (including its extension)
	 * @param outDelimiter the string delimiter for the new file to be created
	 */
	public DelimitedFileConverter(String inFile, String inDelimiter, String outFile, String outDelimiter) {
		input = new File(inFile);
		inDelimit = inDelimiter;
		output = new File(outFile);
		outDelimit = outDelimiter;
	}

	/**
	 * Convert the file by replacing all occurrences of the input delimiter with the output delimiter.  Writes the contents to the file name given in the constructor.
	 */
	public void convert() {	

		try {
			BufferedReader reader = new BufferedReader(new FileReader(input));
			FileWriter writer = new FileWriter(output);
			while(reader.ready()) {
				String line = reader.readLine();
				line = line.replaceAll("\"", "");
				line = line.replaceAll(inDelimit, outDelimit);
				/*
				String[] fields = line.split(inDelimit);
				for(int i = 0; i < fields.length; i++) {
					writer.write(fields[i] + outDelimit);
				}
				*/
				writer.write("\r\n\r\n");
			}
			reader.close();
			writer.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
