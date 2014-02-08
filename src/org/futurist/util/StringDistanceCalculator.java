/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util;

import java.util.Arrays;

public class StringDistanceCalculator {

	private String s1;
	private String s2;

	/**
	 * Default constructor to compare two Strings.
	 * @param s1 the first String.
	 * @param s2 the second String.
	 */
	public StringDistanceCalculator(String s1, String s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	/**
	 * The Hamming Distance between two strings of equal length is the number of positions at which the corresponding symbols are different.  Put another way, it measures the minimum number of substitutions required to change one string into the other, or the number of errors that transformed one string into the other.	
	 * @return the distance.
	 */
	public int getHammingDistance() {

		String p1 = s1;
		String p2 = s2;

		// pad strings with spaces to ensure equal lengths
		if(p1.length() > p2.length()) {
			for(int i = p1.length(); i > p2.length(); i--) {
				p2 += " ";
			}
		} else if(p2.length() > p1.length()) {
			for(int i = p2.length(); i > p1.length(); i--) {
				p1 += " ";
			}
		}

		int counter = 0;
		for (int i = 0; i < p1.length(); i++) {
			if(p1.charAt(i) == p2.charAt(i)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * The Hamming Percent is defined here as the percent of the longest String that must be changed to make the Strings equal. 
	 * @return the percent.
	 */
	public double getHammingPercent() {
		int distance = getHammingDistance();
		if(s1.length() > s2.length()) {
			return 1 - (distance / s1.length());
		} else {
			return 1 - (distance / s2.length());
		}
	}

	/**
	 * The Jaro–Winkler Distance is a variant of the Jaro distance metric used in the area of record linkage (duplicate detection).  The score is normalized such that 0 equates to no similarity and 1 is an exact match.
	 * @return the distance.
	 */
	public double getJaroWinklerDistance() {

        if (s1.length() == 0) {
            return s2.length() == 0 ? 1.0 : 0.0;
        }

        int  searchRange = Math.max(0, Math.max(s1.length(), s2.length())/2 - 1);

        boolean[] matched1 = new boolean[s1.length()];
        Arrays.fill(matched1,false);
        boolean[] matched2 = new boolean[s2.length()];
        Arrays.fill(matched2,false);

        int numCommon = 0;
        for (int i = 0; i < s1.length(); ++i) {
            int start = Math.max(0, i-searchRange);
            int end = Math.min(i + searchRange+1, s2.length());
            for (int j = start; j < end; ++j) {
                if (matched2[j]) continue;
                if (s1.charAt(i) != s2.charAt(j))
                    continue;
                matched1[i] = true;
                matched2[j] = true;
                ++numCommon;
                break;
            }
        }
        if (numCommon == 0) return 0.0;

        int numHalfTransposed = 0;
        int j = 0;
        for (int i = 0; i < s1.length(); ++i) {
            if (!matched1[i]) continue;
            while (!matched2[j]) ++j;
            if (s1.charAt(i) != s2.charAt(j))
                ++numHalfTransposed;
            ++j;
        }
        int numTransposed = numHalfTransposed/2;

        double numCommonD = numCommon;
        double weight = (numCommonD/s1.length()
                         + numCommonD/s2.length()
                         + (numCommon - numTransposed)/numCommonD)/3.0;

        if (weight <= 0.7) return weight;
        int max = Math.min(4, Math.min(s1.length(),s2.length()));
        int pos = 0;
        while (pos < max && s1.charAt(pos) == s2.charAt(pos))
            ++pos;
        if (pos == 0) return weight;
        return 1 - (weight + 0.1 * pos * (1.0 - weight));
	}

	/**
	 * The Damerau-Levenshtein Distance is given by counting the minimum number of operations needed to transform one string into the other, where an operation is defined as an insertion, deletion, or substitution of a single character, or a transposition of two adjacent characters.
	 * @return the distance.
	 */
	public int getDamerauLevenshteinDistance() {
		
		if(s1.length() == 0 && s2.length() == 0) {
			return 0;
		} else if(s1.length() == 0) {
			return s2.length();
		} else if(s2.length() == 0) {
			return s1.length();
		} else {
		
			int[][] matrix = new int[s1.length()+1][s2.length()+1];
			int lengthSum = s1.length() + s2.length();
	
			for (int i = 0; i < s1.length(); i++)
			{
				matrix[i+1][1] = i;
				matrix[i+1][0] = lengthSum;
			}
	
			for (int i = 0; i < s2.length(); i++)
			{
				matrix[1][i+1] = i;
				matrix[0][i+1] = lengthSum;
			}
	
			int[] dist = new int[24];
	
			for (int i = 0; i < 24; i++)
			{
				dist[i] = 0;
			}
	
			for (int i = 1; i < s1.length(); i++)
			{
				int db = 0;
	
				for (int j = 1; j < s2.length(); j++)
				{
					
					int i1 = dist[s2.indexOf(s2.charAt(j-1))];
					int j1 = db;
					int d = ((s1.charAt(i-1)==s2.charAt(j-1))?0:1);
					if (d == 0) db = j;
	
					matrix[i+1][j+1] = Math.min(Math.min(matrix[i][j]+d, matrix[i+1][j]+1),Math.min(matrix[i][j+1]+1,matrix[i1][j1]+(i - i1-1)+1+(j-j1-1)));
				}
				dist[s1.indexOf(s1.charAt(i-1))] = i;
			}

			return matrix[s1.length()][s2.length()];
		}
	}

	/**
	 * The Damerau-Levenshtein Percent is defined here as the percent of the longest String that must be changed to make the Strings equal. 
	 * @return the percent.
	 */
	public double getDamerauLevenshteinPercent() {
		double distance = getDamerauLevenshteinDistance();
		if(s1.length() > s2.length()) {
			return 1 - (distance / s1.length());
		} else {
			return 1 - (distance / s2.length());
		}
	}

	/**
	 * Returns the mean average distance of the various String distance metrics available.
	 * @return the average distance.
	 */
	public Double getAverageOfPercents() {
		Double sum = getHammingDistance() + getJaroWinklerDistance() + getDamerauLevenshteinDistance();
		return sum / 3;
	}

}
