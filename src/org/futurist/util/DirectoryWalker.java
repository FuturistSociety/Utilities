/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util;

import java.io.File;
import java.util.ArrayList;

public class DirectoryWalker {

	private ArrayList<File> files;
	private File rootFile;

	/**
	 * Default constructor that takes the directory to be recursively traversed.
	 * @param f the directory to traverse.
	 */
	public DirectoryWalker(File f) {
		files = new ArrayList<File>();
		rootFile = f;
	}

	/**
	 * Returns the list of all files contained within all sub-directories of the root given in the constructor.
	 * @return The list of all files found in the traversal.
	 */
	public ArrayList<File> traverse() {
		return recursiveTraversal(rootFile);
	}

	private ArrayList<File> recursiveTraversal(File currentFile) {
		if (currentFile.isDirectory()) {
			File allFiles[] = currentFile.listFiles();
			for (File aFile : allFiles) {
				recursiveTraversal(aFile);
			}
		} else if (currentFile.isFile()) {
			files.add(currentFile);
		}
		return files;
	}
	
}