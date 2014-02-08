/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util.math;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataPreparer {
	
	private double[] data;
	
	/**
	 * Default constructor to load data for preparation.
	 * @param data the data to prepare.
	 */
	public DataPreparer(double input[]) {
		data = input;
		data = removeOutliers(data);
		//data = centerData(data);
	}
	
	/**
	 * Removes the minimum and maximum values from the original dataset to eliminate possible outliers.
	 * @param input the data from which to remove outliers.
	 * @return the data without the original outliers.
	 */
	public static double[] removeOutliers(double input[]) {
		DescriptiveStatistics desc = new DescriptiveStatistics(input);
		ArrayList<Double> outliersRemoved = new ArrayList<Double>();
		
		// remove min and max
		for(int i = 0; i < input.length; i++) {
			if(input[i] != desc.getMax() && input[i] != desc.getMin()) {
				outliersRemoved.add(input[i]);
			}
		}
		
		double[] output = new double[outliersRemoved.size()];
		for(int i = 0; i < outliersRemoved.size(); i++) { output[i] = outliersRemoved.get(i); }
		return output;
	}
	
	/**
	 * Removes any values from the original dataset that are more than the given sigma level away from the mean in order to eliminate outliers.
	 * @param input the data from which to remove outliers.
	 * @return the data without the original outliers.
	 */
	public static double[] removeOutliers(double input[], double sigma) {
		ArrayList<Double> outliersRemoved = new ArrayList<Double>();
		DescriptiveStatistics desc = new DescriptiveStatistics(input);
		
		// remove x-sigma observations
		for(int i = 0; i < input.length; i++) {
			if(Math.abs(input[i]) >= (sigma * desc.getStandardDeviation())) {
				outliersRemoved.add(input[i]);
			}
		}
		
		double[] output = new double[outliersRemoved.size()];
		for(int i = 0; i < outliersRemoved.size(); i++) { output[i] = outliersRemoved.get(i); }
		return output;
	}
	
	/**
	 * Center the given data by subtracting the sample mean from each data point
	 * @param input the original data.
	 * @return the centered data.
	 */
	public static double[] centerData(double input[]) {
		double output[] = new double[input.length];
		DescriptiveStatistics desc = new DescriptiveStatistics(input);
		for(int i = 0; i < input.length; i++) {
			output[i] = input[i] - desc.getMean();
		}
		return output;
	}
	
	/**
	 * Whiten the data using a linear transformation that allows fewer parameters to be estimated.
	 * @param input the original data.
	 * @return the whitened data.
	 */
	/*
	public double[] whitenData(double input[]) {
		double output[] = new double[input.length];
		
		return output;
	}
	*/
	
	/**
	 * Transform the given data by replacing each data point with its arcsine value.
	 * @param input the original data.
	 * @return the arcsine transformed data.
	 */
	public static double[] arcSineTransform(double input[]) {
		double output[] = new double[input.length];
		for(int i = 0; i < input.length; i++) {
			output[i] = Math.asin(input[i]);
		}
		return output;
	}
	
	/**
	 * Transform the given data by replacing each data point with its inverse value.
	 * @param input the original data.
	 * @return the inverse data.
	 */
	public static double[] inverseTransform(double input[]) {
		double output[] = new double[input.length];
		for(int i = 0; i < input.length; i++) {
			output[i] = 1 / input[i];
		}
		return output;
	}
	
	/**
	 * Transform the given data by replacing each data point with its Joukowsky value.
	 * @param input the original data.
	 * @return the Joukowsky transformed data.
	 */
	public static double[] joukowskiTransform(double input[]) {
		double output[] = new double[input.length];
		for(int i = 0; i < input.length; i++) {
			output[i] = input[i] + (1 / input[i]);
		}
		return output;
	}
	
	/**
	 * Transform the given data by replacing each data point with its base-10 logarithm.
	 * @param input the original data.
	 * @return the base-10 logarithm transformed data.
	 */
	public static double[] logBase10Transform(double input[]) {
		double output[] = new double[input.length];
		for(int i = 0; i < input.length; i++) {
			output[i] = Math.log10(input[i]);
		}
		return output;
	}
	
	/**
	 * Transform the given data by replacing each data point with its base-e logarithm.
	 * @param input the original data.
	 * @return the base-e transformed data.
	 */
	public static double[] logBaseETransform(double input[]) {
		double output[] = new double[input.length];
		for(int i = 0; i < input.length; i++) {
			output[i] = Math.log(input[i]);
		}
		return output;
	}
	
	/**
	 * Transform the given data by replacing each data point with its square root.
	 * @param input the original data.
	 * @return the square root data.
	 */
	public static double[] sqrRtTransform(double input[]) {
		double output[] = new double[input.length];
		for(int i = 0; i < input.length; i++) {
			output[i] = Math.sqrt(input[i]);
		}
		return output;
	}

	/**
	 * Transform each data point by weighting the square of the difference between the original data point and a target value, divided by the target value raised to a constant power k.<p>When k=0, absolute weighting is used.  When k=2, Poisson weighting is used.  When k=2, relative weighting is used.
	 * @param input the original data.
	 * @param targetCureve the target/ideal/correct values for the region of the curve in question
	 * @param k a constant that determines the type of weighting.
	 * @return the square root data.
	 * @see http://graphpad.com/guides/prism/6/curve-fitting/reg_how_weigting_works.htm
	 */
	public static double[] weightedTransform(double input[], double targetCurve[], double k) {
		double output[] = new double[input.length];
		for(int i = 0; i < input.length; i++) {
			double numerator = Math.pow(input[i] - targetCurve[i], 2);
			output[i] = numerator / Math.pow(targetCurve[i], k);
		}
		return output;
	}
	
	/**
	 * Returns the processed data.
	 * @return the data after all processing has taken place.
	 */
	public double[] getData() {
		return data;
	}

}
