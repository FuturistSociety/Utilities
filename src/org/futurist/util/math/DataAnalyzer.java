/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util.math;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataAnalyzer {
	
	private double[] data;
	private DescriptiveStatistics description;
	
	/**
	 * Default constructor to load data for analysis.
	 * @param data the data to analyze.
	 */
	public DataAnalyzer(double input[]) {
		data = input;
		description = new DescriptiveStatistics(data);
	}
	
	/**
	 * Returns the number of times the given value occurs in the data.
	 * @param x the value to count.
	 * @return The frequency of x in the data.
	 */
	public int getFrequency(double x) {
		int count = 0;
		for(double d : data) {
			if(d == x) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Returns the minimum of the available values.
	 * @return The min or Double.NaN if no values have been added.
	 */
	public double getMin() {
		return description.getMin();
	}
	
	/**
	 * Returns the maximum of the available values.
	 * @return The max or Double.NaN if no values have been added.
	 */
	public double getMax() {
		return description.getMax();
	}
	
	/**
	 * Returns the arithmetic mean of the available values.
	 * @return The mean or Double.NaN if no values have been added.
	 */
	public double getMean() {
		return description.getMean();
	}
	
	/**
	 * This method returns the bias-corrected sample variance (using n - 1 in the denominator).
	 * @return The variance, Double.NaN if no values have been added or 0.0 for a single value set.
	 */
	public double getVariance() {
		return description.getVariance();
	}
	
	/**
	 * Returns the skewness of the available values. Skewness is a measure of the asymmetry of a given distribution.
	 * @return The skewness, Double.NaN if no values have been added or 0.0 for a value set <=2.
	 */
	public double getSkewness() {
		return description.getSkewness();
	}
	
	/**
	 * Returns the Kurtosis of the available values. Kurtosis is a measure of the "peakedness" of a distribution.  kurtosis(y) = E{y^4} - 3(E{y^2})^2
	 * @return The kurtosis, Double.NaN if no values have been added, or 0.0 for a value set <=3.
	 */
	public double getKurtosis() {
		return description.getKurtosis();
	}
	
	/**
	 * Returns the sum of the squares of the available values.
	 * @return The sum of the squares or Double.NaN if no values have been added.
	 */
	public double getSumOfSquares() {
		return description.getSumsq();
	}
	
	/**
	 * Returns the entropy of the available values.  H(y) = -SUM{P(y=a)log[P(y=a)]}
	 * @return The entropy.
	 */
	public double getEntropy() {
		double sum = 0.0;
		for(double d : data) {
			double prob = getFrequency(d) / data.length;
			sum += prob * Math.log(prob);
		}
		return sum * -1;
	}
	
	/**
	 * Returns the negentropy of the available values.  The formula used to calculate negentropy in this method is: J(y) ~ 1/12*E{y^3}^2 + 1/48*kurt(y)^2.  An alternative formula is: J(y) ~ SUM(k[E{G(y)} - E{G(v)}]) where k takes on the value of positive constants; G(u) = 1/a*log(cosh[au]) where 1 <= a <= 2 or G(u) = -EXP(-u^2/2).
	 * @return The sum of the squares or Double.NaN if no values have been added.
	 */
	public double getNegentropy() {
		double firstTerm = Math.pow(Math.pow(description.getMean(), 3), 2);
		double secondTerm = Math.pow(description.getKurtosis(), 2);
		return (firstTerm / 12) + (secondTerm / 48);
	}
	
}
