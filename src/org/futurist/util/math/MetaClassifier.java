package org.futurist.util.math;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.futurist.neuralnet.Network;

import weka.associations.Apriori;
import weka.associations.FPGrowth;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.MultiClassClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

public class MetaClassifier {

	private Double[] yData;
	private Double[][] xData;
	private String[] parameterNames;
	private int numObservations;
	private int numParameters;
	private double[][] processedData;
	private Double trainingPercent;
	private Random rng;
	private ArrayList<Classifier> algorithms;
	private TreeMap<Double, String> parameterScores;
	private TreeMap<Double, String> algorithmScores;
	private boolean trained;

	/**
	 * Default constructor to create a MetaClassifier for the given data through training rounds.
	 * @param y the target/ideal/correct values that the MetaClassifer should learn to classify.
	 * @param x independent observations (indexed in the outer array) of each parameter's value (indexed in the inner array).
	 * @param p independent variable (model parameter) names or descriptions.
	 * @param trainRatio the percentage of the dataset to use as training data in each round.
	 */
	public MetaClassifier(Double[] y, Double[][] x, String[] p, Double trainRatio) {
		// initialize data variables
		yData = y;
		xData = x;
		parameterNames = p;
		numObservations = xData.length;
		numParameters = parameterNames.length;
		trainingPercent = trainRatio;
		algorithms = new ArrayList<Classifier>();
		trained = false;

		System.out.println("x[" + x.length + "][" + x[0].length + "]");
		System.out.println("y[" + y.length + "]");
		System.out.println("# obs: " + numObservations);
		System.out.println("# params: " + numParameters);

		// pre-process data per parameter and generate statistical reports as we go
		processedData = new double[numObservations][numParameters];
		for(int j = 0; j < numParameters; j++) {
			// load current parameter's data
			System.out.println("Analyzing statistical properties of parameter " + j);
			double preProcess[] = new double[numObservations];
			for(int i = 0; i < numObservations; i++) {
				preProcess[i] = xData[i][j];
			}

			// run reports on unmodified and transformed device data
			StatisticalReporter rawDeviceDataReport = new StatisticalReporter(preProcess);
			rawDeviceDataReport.saveReportToFile("Paramenter " + parameterNames[j] + " - Raw Unmodified");
			StatisticalReporter rawArcSineDeviceDataReport = new StatisticalReporter(DataPreparer.arcSineTransform(preProcess));
			rawArcSineDeviceDataReport.saveReportToFile("Paramenter " + parameterNames[j] + " - Arcsine Transformed");
			StatisticalReporter rawJoukowskiTransformDataReport = new StatisticalReporter(DataPreparer.joukowskiTransform(preProcess));
			rawJoukowskiTransformDataReport.saveReportToFile("Paramenter " + parameterNames[j] + " - Arcsine Transformed");
			StatisticalReporter rawLogEDeviceDataReport = new StatisticalReporter(DataPreparer.logBaseETransform(preProcess));
			rawLogEDeviceDataReport.saveReportToFile("Paramenter " + parameterNames[j] + " - Log Base e Transformed");
			StatisticalReporter rawLog10DeviceDataReport = new StatisticalReporter(DataPreparer.logBase10Transform(preProcess));
			rawLog10DeviceDataReport.saveReportToFile("Paramenter " + parameterNames[j] + " - Log Base 10 Transformed");
			StatisticalReporter rawSqrRtDeviceDataReport = new StatisticalReporter(DataPreparer.sqrRtTransform(preProcess));
			rawSqrRtDeviceDataReport.saveReportToFile("Paramenter " + parameterNames[j] + " - Square Root Transformed");

			// center and whiten data; run report on prepared data; and store processed device data
			DataPreparer dataProcessor = new DataPreparer(preProcess);
			double postProcess[] = dataProcessor.getData();
			StatisticalReporter processedDataReport = new StatisticalReporter(postProcess);
			processedDataReport.saveReportToFile("Paramenter " + parameterNames[j]);
			for(int i = 0; i < numObservations; i++) {
				processedData[i][j] = postProcess[i];
			}

			// FIX: for each [i] add [j+1] ordinal mapping to classIdx (which is needed to set Weka's class index for model evaluation); must be done after data processing to prevent diseaseIDs from being centered/whitened away 
		}
	}

	/**
	 * Train the MetaClassifier on the dataset for the given number of rounds. 
	 * @param numRounds the number of training rounds to run.
	 */
	public void runTrainingRounds(int numRounds) {

		int trainingSetSize = new Double(Math.round(numObservations * trainingPercent)).intValue();
		rng = new Random();
		for(int round = 0; round < numRounds; round++) {

			// create a copy of the processed data to fill main training and testing sets for this round
			ArrayList<double[]> dataCopy = new ArrayList<double[]>(numObservations);
			double[][] trainingSet = new double[trainingSetSize][numParameters+1];  // +1 allows class (y-value corresponding to this observation) to be stored
			double[][] testingSet = new double[numObservations-trainingSetSize][numParameters+1];  // +1 allows class (y-value corresponding to this observation) to be stored
			for(double[] obs : processedData) {
				dataCopy.add(obs);
			}

			// instantiate Weka data sets
			ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>(numParameters);
			for(String p : parameterNames) { wekaAttributes.add(new Attribute(p)); }
			wekaAttributes.add(new Attribute("Class"));
			Instances wekaTrainingInstances = new Instances("Roud " + round + " Training Instances", wekaAttributes, trainingSetSize);
			Instances wekaTestingInstances = new Instances("Roud " + round + " Testing Instances", wekaAttributes, testingSet.length);

			// set the class instance that we want to predict
			wekaTrainingInstances.setClass(new Attribute("Class"));
			wekaTrainingInstances.setClassIndex(numParameters);
			wekaTestingInstances.setClass(new Attribute("Class"));
			wekaTestingInstances.setClassIndex(numParameters);

			// copy randomly chosen observations into the main testing set and Weka's testing
			for(int obs = 0; obs < dataCopy.size()-trainingSetSize; obs++) {
				int idxToCopy = rng.nextInt(dataCopy.size());
				double[] o = dataCopy.get(idxToCopy);
				for(int p = 0; p < o.length; p++) {
					testingSet[obs][p] = o[p];
				}
				testingSet[obs][numParameters] = yData[obs];  // store class/target value
				DenseInstance instance = new DenseInstance(1.0, o);
				instance.setDataset(wekaTestingInstances);
				instance.setClassValue(new Double(testingSet[obs][numParameters]).toString());
				wekaTestingInstances.add(instance);
				dataCopy.remove(idxToCopy);
			}

			// copy the remaining data into the main training set and Weka's training set
			for(int obs = 0; obs < trainingSetSize; obs++) {
				double[] o = dataCopy.get(obs);
				for(int p = 0; p < o.length; p++) {
					trainingSet[obs][p] = o[p];
				}
				trainingSet[obs][numParameters] = yData[obs];  // store class/target value
				DenseInstance instance = new DenseInstance(1.0, o);
				instance.setDataset(wekaTrainingInstances);
				instance.setClassValue(new Double(trainingSet[obs][numParameters]).toString());
				wekaTrainingInstances.add(instance);
			}

			System.out.println("Running training round " + round + " with " + trainingSet.length + " training observations (" + wekaTrainingInstances.size() + " Weka instances) and " + testingSet.length + " testing observations (" + wekaTestingInstances.size() + " Weka instances). ");

			// UPGRADE: change this outer loop to iterate over power set of parameters later
			for(String p : parameterNames) {

				/*
				// test ordinary least squares linear regression
 				OLSMultipleLinearRegression linearRegression = new OLSMultipleLinearRegression();
				linearRegression.newSampleData(yData, trainingSet);
				double r2 = linearRegression.calculateAdjustedRSquared();
				parameterScores.put(r2, p + " with OLS Linear Regression R^2");
				algorithmScores.put(r2, "OLS Linear Regression R^2 for " + p);
				 */

				// test Weka meta-classification
				// run: Bagging -P 100 -S 1 -num-slots 1 -I 10 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0
				// options: bag 100 percent with 1 seed, 1 execution slot, and 10 iterations using REPTRee with a minimum of 2 instances, minimum variance of 0.001, 3 folds, 1 seed, max depth of -1, and an initial count of 0.0
				Bagging wekaBagging = new Bagging();
				try {
					//wekaBagging.setOptions(Utils.splitOptions("-P 100 -S 1 -num-slots 1 -I 10 -W \"weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0\""));
					wekaBagging.buildClassifier(wekaTrainingInstances);
					Evaluation baggingEval = new Evaluation(wekaTrainingInstances);
					baggingEval.evaluateModel(wekaBagging, wekaTestingInstances);
					parameterScores.put(baggingEval.correlationCoefficient(), p + " with Weka Bagging Classification R^2");
					algorithmScores.put(baggingEval.correlationCoefficient(), "Weka Bagging Classification R^2 for " + p);
					System.out.println(baggingEval.toSummaryString("\nResults\n======\n", false));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// run: ClassificationViaRegression -W weka.classifiers.trees.M5P -- -U -M 4.0
				// options: use un-smoothed with a minimum of 2 instances
				ClassificationViaRegression wekaRegression = new ClassificationViaRegression();
				try {
					//wekaRegression.setOptions(Utils.splitOptions("-W \"weka.classifiers.trees.M5P -- -U -M 4.0\""));
					wekaRegression.buildClassifier(wekaTrainingInstances);
					Evaluation regressionEval = new Evaluation(wekaTrainingInstances);
					regressionEval.evaluateModel(wekaRegression, wekaTestingInstances);
					parameterScores.put(regressionEval.correlationCoefficient(), " with Weka Regression Classification R^2");
					algorithmScores.put(regressionEval.correlationCoefficient(), "Weka Bagging Classification R^2 for " + p);
					System.out.println(regressionEval.toSummaryString("\nResults\n======\n", false));
					algorithms.add(wekaRegression);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// run: MultiClassClassifier -M 0 -R 2.0 -S 1 -W weka.classifiers.functions.SGD -F 0 -L 0.01 -R 1.0E-4 -E 500
				// options: use the "1-against-all" method with a random width factor of 2 and 1 seed using a stocastic gradient descent (SGD) with a hingle loss (SVM) function, learning rate of 0.01, lambda of .00001, and 500 epochs
				// alternative: -W weka.classifiers.functions.Logistic -- -R 1.0E-8 -M -1
				// alternative: -W weka.classifiers.functions.SMO -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -M -V -1 -W 1 -K "weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 0.25"
				// alternative: -W weka.classifiers.functions.MultilayerPerceptron -L 0.75 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a -R
				// alternative: -W weka.classifiers.functions.LibLINEAR -S 0 -C 1.0 -E 0.01 -B 2.5
				MultiClassClassifier wekaMultiClass = new MultiClassClassifier();
				try {
					//wekaMultiClass.setOptions(Utils.splitOptions("-W weka.classifiers.functions.SGD -F 0 -L 0.01 -R 1.0E-4 -E 500"));
					wekaMultiClass.buildClassifier(wekaTrainingInstances);
					Evaluation multiClassEval = new Evaluation(wekaTrainingInstances);
					multiClassEval.evaluateModel(wekaRegression, wekaTestingInstances);
					parameterScores.put(multiClassEval.correlationCoefficient(), " with Weka Multi-Class Classification R^2");
					algorithmScores.put(multiClassEval.correlationCoefficient(), "Weka Multi-Class Classification R^2 for " + p);
					System.out.println(multiClassEval.toSummaryString("\nResults\n======\n", false));
					algorithms.add(wekaMultiClass);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// run: MultilayerPerceptron -L 0.75 -M 0.2 -N 500 -V 0 -S 0 -E 25 -H a -R -D
				// options: backpropagate with a learning rate of 0.75, momentum of 0.2, 500 second training time, 0 validation set size, seed of 0, validation threshold of 25, (number of attributes + number of classes) / 2 hidden layers, no reset, and decay enabled
				MultilayerPerceptron wekaPerceptron = new MultilayerPerceptron();
				try {
					//wekaPerceptron.setOptions(Utils.splitOptions("-W weka.classifiers.functions.MultilayerPerceptron -L 0.75 -M 0.2 -N 500 -V 0 -S 0 -E 25 -H a -R -D"));
					wekaPerceptron.buildClassifier(wekaTrainingInstances);
					Evaluation perceptronEval = new Evaluation(wekaTrainingInstances);
					perceptronEval.evaluateModel(wekaRegression, wekaTestingInstances);
					parameterScores.put(perceptronEval.correlationCoefficient(), " with Weka Multi-Layer Perceptron Classification R^2");
					algorithmScores.put(perceptronEval.correlationCoefficient(), "Weka Multi-Layer Perceptron Classification R^2 for " + p);
					System.out.println(perceptronEval.toSummaryString("\nResults\n======\n", false));
					algorithms.add(wekaPerceptron);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// run: BayesNet -D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 3 -G 7 -P 2 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5
				// options: use the LAGD Hill Climber search algorithm with 2 parents, 7 good operations, 3 look ahead steps, and the Bayes score type; use the Simple Estimator with an alpha of 0.5
				BayesNet wekaBayesNet = new BayesNet();
				try {
					//wekaBayesNet.setOptions(Utils.splitOptions("-D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 3 -G 7 -P 2 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"));
					wekaBayesNet.buildClassifier(wekaTrainingInstances);
					Evaluation BayesNetEval = new Evaluation(wekaTrainingInstances);
					BayesNetEval.evaluateModel(wekaRegression, wekaTestingInstances);
					parameterScores.put(BayesNetEval.correlationCoefficient(), " with Weka Bayes Network Classification R^2");
					algorithmScores.put(BayesNetEval.correlationCoefficient(), "Weka Bayes Network Classification R^2 for " + p);
					System.out.println(BayesNetEval.toSummaryString("\nResults\n======\n", false));
					algorithms.add(wekaBayesNet);
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*
				// test Weka association
				// run: Apriori -N 25 -T 0 -C 0.95 -D 0.25 -U 1.0 -M 0.25 -S -1.0 -c -1
				// options: use 25 rules, a confidence metric type, a minimum metric of 0.95, delta of 0.25, upper bound minimum support of 1.0, lower bound minimum support of 0.25, a significance level of -1.0, and ???
				// default: Apriori -N 10 -T 0 -C 0.9 -D 0.25 -U 1.0 -M 0.1 -S -1.0 -c -1
				try {
					Apriori wekaApriori = new Apriori();
					wekaApriori.setOptions(Utils.splitOptions("-N 25 -T 0 -C 0.95 -D 0.25 -U 1.0 -M 0.25 -S -1.0 -c -1"));
					wekaApriori.buildAssociations(wekaTrainingInstances);
					System.out.println(wekaApriori);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// run: FPGrowth -P 2 -I -1 -N 25 -T 0 -C 0.95 -D 0.25 -U 1.0 -M 0.1
				// options: use a positive index of 2, a maximum number of items of -1, 25 rules, a confidence metric type, a minimum metric of 0.95, delta of 0.25, upper bound minimum support of 1.0, and lower bound minimum support of 0.25
				// default: FPGrowth -P 2 -I -1 -N 10 -T 0 -C 0.9 -D 0.05 -U 1.0 -M 0.1
				try {
					FPGrowth wekaFPGrowth = new FPGrowth();
					wekaFPGrowth.setOptions(Utils.splitOptions("-P 2 -I -1 -N 25 -T 0 -C 0.95 -D 0.25 -U 1.0 -M 0.1"));
					wekaFPGrowth.buildAssociations(wekaTrainingInstances);
					System.out.println(wekaFPGrowth);
				} catch (Exception e) {
					e.printStackTrace();
				}
				 */

				// test Futurist Society neural network
				Network neuralnet = new Network(0, new ChiSquaredDistribution(xData.length*yData.length), xData.length, (xData.length*yData.length) / 2, yData.length, 2 * xData.length, 3, 0.025, 1.25, yData, 100, 100, 100);
				neuralnet.run();
				parameterScores.put(neuralnet.getAverageError(), " with Futurist Society Neural Network Classification R^2");
				algorithmScores.put(neuralnet.getAverageError(), "Weka Futurist Society Neural Network Classification R^2 for " + p);

			}
			//}
		}

		trained = true;
	}

	/**
	 * Returns the number of independent variables (model parameters) in the dataset.
	 * @return the number of parameters.
	 */
	public int getNumParameters() {
		return numParameters;
	}

	/**
	 * Returns the number of algorithms used to train the model.
	 * @return the number of algorithms.
	 */
	public int getNumAlgorithms() {
		return algorithmScores.size();
	}

	/**
	 * Returns a <code>TreeMap<Double, String></code> of each parameter's performance score wherein each <code>Double</code> key is mapped to a <code>String</code> description of the parameter.
	 * @return a <code>TreeMap<Double, String></code> of each parameter's performance score.
	 */
	public TreeMap<Double, String> getparameterScores() {
		return parameterScores;
	}

	/**
	 * Generates a report on the performance of each parameter.
	 * @return A multi-line String describing the performance of each parameter.
	 */
	public String getParameterScoreReport() {
		String report = "Rank\tScore\tParameter";
		int deviceRank = getNumParameters();
		for(Double r : getparameterScores().keySet()) {
			report += deviceRank + "\t" + r + getparameterScores().get(r) + "\n";
			deviceRank--;
		}
		return report;
	}

	/**
	 * Returns a <code>TreeMap<Double, String></code> of each algorithm's performance score wherein each <code>Double</code> key is mapped to a <code>String</code> description of the algorithm.
	 * @return a <code>TreeMap<Double, String></code> of each algorithm's performance score.
	 */
	public TreeMap<Double, String> getAlgorithmScores() {
		return algorithmScores;
	}
	
	/**
	 * Generates a report on the performance of each algorithm.
	 * @return A multi-line String describing the performance of each algorithm.
	 */
	public String getAlgorithmScoreReport() {
		String report = "Rank\tScore\tAlgorithm Name";
		int algoRank = getNumAlgorithms();
		for(Double r : getAlgorithmScores().keySet()) {
			report += algoRank + "\t" + r + getAlgorithmScores().get(r) + "\n";
			algoRank--;
		}
		return report;
	}

	/**
	 * Classifies new unseen data based on what the MetaClassifer learned during training.
	 * @param data the new unseen data to classify.
	 * @return the resulting data classified by the MetaClassifer.
	 */
	public double[] classify(double[][] data) {
		double[] results = new double[numParameters];

		if(trained) {
			if(numParameters == data[0].length) {
				// instantiate Weka data sets
				ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>(numParameters);
				for(String p : parameterNames) { wekaAttributes.add(new Attribute(p)); }
				wekaAttributes.add(new Attribute("Class"));
				Instances newInstances = new Instances("New Data", wekaAttributes, data.length);

				// set the class instance that we want to predict
				newInstances.setClass(new Attribute("Class"));
				newInstances.setClassIndex(numParameters);

				// copy newInstances chosen observations into the main testing set and Weka's testing
				for(int obs = 0; obs < data.length; obs++) {
					DenseInstance instance = new DenseInstance(1.0, data[obs]);
					instance.setDataset(newInstances);
					newInstances.add(instance);
				}

				// classify a single instance using the mean average of all algorithms' individual classifications
				for(int i = 0; i < newInstances.size(); i++) {
					Double instanceResultSum = 0.0;
					for(Classifier a : algorithms) {
						try {
							instanceResultSum += a.classifyInstance(newInstances.get(i));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					results[i] = instanceResultSum / algorithms.size();
				}
				
			} else {
				throw new IllegalArgumentException("The number of parameters (independent x variables) must match the number of parameters the algorithms were trained with: Given " + data[0].length + " to classify, but trained with " + numParameters + ".");
			}
		} else {
			throw new IllegalStateException("You must train the machine learning algoirthms with runTrainingRounds(n) first.");
		}

		return results;
	}

}