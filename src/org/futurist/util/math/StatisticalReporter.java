/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util.math;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class StatisticalReporter {

	private double[] yData;
	private double[][] xData;
	DescriptiveStatistics description;
	private String report;

	/**
	 * Default constructor to load some known dependent variable (y-axis) data.
	 * @param data the dependent variable data.
	 */
	public StatisticalReporter(double data[]) {
		yData = data;
		description = new DescriptiveStatistics(yData);
		report = "";
	}

	/**
	 * Overwrite any existing independent variable data (x-axis) with the given data.
	 * @param data the data to load wherein each independent variable (indices in the outer array) may have been observed multiple times (in the inner array). 
	 */
	public void loadData(double[][] data) {
		xData = data;
	}

	/*
	public double getUncorrelatedness() {

	}
	*/

	/**
	 * Returns the number of times the given value occurs in the data.
	 * @param x the value to count.
	 * @return The frequency of x in the data.
	 */
	public int getFrequency(double x) {
		int count = 0;
		for(double d : yData) {
			if(d == x) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Returns the entropy of the available values.  H(y) = -SUM{P(y=a)log[P(y=a)]}
	 * @return The entropy.
	 */
	public double getEntropy() {
		double sum = 0.0;
		for(double d : yData) {
			double prob = getFrequency(d) / yData.length;
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

	/**
	 * Generates a report on the statistical properties of the dependent variable data and its covariance with the independent variables (if they have been loaded).
	 * @return A multi-line String describing the data.
	 */
	public String getReport() {
		report = "";
		report += "Minimum Value:\t" + description.getMin() + "\n";
		report += "Maximim Value:\t" + description.getMax() + "\n";
		report += "Mean:\t" + description.getMean() + "\n";
		report += "Variance:\t" + description.getVariance() + "\n";
		report += "Skewness:\t" + description.getSkewness() + "\n";
		report += "Kurtosis:\t" + description.getKurtosis() + "\n";
		report += "Sum of Squares:\t" + description.getSumsq() + "\n";
		report += "Entropy:\t" + getEntropy() + "\n";
		report += "NegEntropy:\t" + getNegentropy() + "\n";

		if(xData != null) {
			Covariance covar = new Covariance();
			for(int i = 0; i < xData.length; i++) {
				report += "Covariance between variable " + xData[i] + " and y data:\t" + covar.covariance(xData[i], yData) + "\n";
			}

			//report += "Uncorrelatedness:\t" + getUncorrelatedness() + "\n";

			// may need to flip rows and columns in xData due to assumption of data structure by OLSMultipleLinearRegression
			OLSMultipleLinearRegression leastSqRegress = new OLSMultipleLinearRegression();
			leastSqRegress.newSampleData(yData, xData);
			report += "OLS Regression Adjusted R^2:\t" + leastSqRegress.calculateAdjustedRSquared();
		}

		return report;
	}
	
	/**
	 * Saves the given String to a text file.
	 * @param fName the name of the file to which the extension .txt will automatically be added.
	 */
	public void saveReportToFile(String fName) {
		File file = new File(fName + ".txt");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(getReport());
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
