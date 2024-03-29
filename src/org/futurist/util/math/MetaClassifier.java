/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math;

import java.util.ArrayList;
import java.security.SecureRandom;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.futurist.neuralnet.Network;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.MultiClassClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class MetaClassifier extends Thread {

	private Double[] yData;
	private Double[][] xData;
	private String[] parameterNames;
	private int numObservations;
	private int numParameters;
	private double[][] processedData;
	private Double trainingPercent;
	private SecureRandom rng;
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

		//System.out.println("x[" + x.length + "][" + x[0].length + "]");
		//System.out.println("y[" + y.length + "]");
		System.out.println("# obs: " + numObservations);
		System.out.println("# params: " + numParameters);

		// pre-process data per parameter and generate statistical reports as we go
		processedData = new double[numObservations][numParameters];
		for(int j = 0; j < numParameters; j++) {
			// load current parameter's data
			//System.out.println("Analyzing statistical properties of parameter " + j);
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
			for(int i = 0; i < postProcess.length; i++) {
				processedData[i][j] = postProcess[i];
			}

		}
	}

	/**
	 * Train the MetaClassifier on the dataset for the given number of rounds. 
	 * @param numRounds the number of training rounds to run.
	 */
	public void run(int numRounds) {

		parameterScores = new TreeMap<Double, String>();
		algorithmScores = new TreeMap<Double, String>();
		int trainingSetSize = new Double(Math.round(numObservations * trainingPercent)).intValue();

		rng = new SecureRandom();
		final byte[] temp = new byte[512];
		rng.nextBytes(temp);

		for(int round = 0; round < numRounds; round++) {

			// create a copy of the processed data to fill main training and testing sets for this round
			double[][] trainingSet = new double[trainingSetSize][numParameters+1];  // +1 allows class (y-value corresponding to this observation) to be stored
			double[][] testingSet = new double[numObservations-trainingSetSize][numParameters+1];  // +1 allows class (y-value corresponding to this observation) to be stored

			// instantiate Weka data sets
			FastVector<Attribute> wekaAttributes = new FastVector<Attribute>(numParameters);
			for(String p : parameterNames) { 
				wekaAttributes.add(new Attribute(p));
			}
			wekaAttributes.add(new Attribute("Class"));
			Instances wekaTrainingInstances = new Instances("Roud " + round + " Training Instances", wekaAttributes, trainingSetSize);
			Instances wekaTestingInstances = new Instances("Roud " + round + " Testing Instances", wekaAttributes, testingSet.length);
			Instances wekaTrainingInstancesString = new Instances("Roud " + round + " Training Instances with String Class", wekaAttributes, trainingSetSize);
			Instances wekaTestingInstancesString = new Instances("Roud " + round + " Testing Instances with String Class", wekaAttributes, testingSet.length);

			// set the class instance that we want to predict
			FastVector<String> classes = new FastVector<String>(3);
			classes.add("0");
			classes.add("1");
			classes.add("2");
			Attribute nominalClass = new Attribute("Class", classes);
			wekaTrainingInstances.setClass(nominalClass);
			wekaTrainingInstances.setClassIndex(numParameters);
			wekaTestingInstances.setClass(nominalClass);
			wekaTestingInstances.setClassIndex(numParameters);

			// randomly choose observations to copy
			ArrayList<Integer> testIndexes = new ArrayList<Integer>(numObservations-trainingSetSize);
			for(int i = 0; i < numObservations-trainingSetSize; i++) {
				int idxToCopy = rng.nextInt(numObservations);
				while(testIndexes.contains(idxToCopy)) {
					idxToCopy = rng.nextInt(numObservations);
				}
				testIndexes.add(idxToCopy);
			}

			// copy the randomly chosen observations into the main testing set and Weka's testing set 
			for(int obs = 0; obs < testIndexes.size(); obs++) {
				for(int p = 0; p < numParameters; p++) {
					testingSet[obs][p] = processedData[testIndexes.get(obs)][p];
				}
				testingSet[obs][numParameters] = yData[obs].doubleValue();  // store class/target value
				Instance instance = new DenseInstance(1.0, testingSet[obs]);
				instance.setDataset(wekaTestingInstances);
				instance.setClassValue(testingSet[obs][numParameters]);
				wekaTestingInstances.add(instance);
			}

			// copy the remaining data into the main training set and Weka's training set
			int trainingObs = 0;
			for(int obs = 0; obs < processedData.length; obs++) {
				// get an observation that was not added to the testing set
				if(!testIndexes.contains(obs)) {
					for(int p = 0; p < numParameters; p++) {
						trainingSet[trainingObs][p] = processedData[obs][p];
					}
					trainingSet[trainingObs][numParameters] = yData[obs].doubleValue();  // store class/target value
					Instance instance = new DenseInstance(1.0, trainingSet[trainingObs]);
					instance.setDataset(wekaTrainingInstances);
					instance.setClassValue(trainingSet[trainingObs][numParameters]);
					wekaTrainingInstances.add(instance);
					trainingObs++;
				}
			}

			System.out.println("\nRunning training round " + round + " with " + trainingSet.length + " training observations (" + wekaTrainingInstances.numInstances() + " Weka instances) and " + testingSet.length + " testing observations (" + wekaTestingInstances.numInstances() + " Weka instances). ");

			// test Weka meta-classification with a numeric class
			// run: Bagging -P 100 -S 1 -num-slots 1 -I 1 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0
			// options: bag 100 percent with 1 seed, 1 execution slot, and 1 iteration using REPTRee with a minimum of 2 instances, minimum variance of 0.001, 3 folds, 1 seed, max depth of -1, and an initial count of 0.0
			Bagging wekaBagging = new Bagging();
			try {
				wekaBagging.setOptions(Utils.splitOptions("-P 100 -S 1 -num-slots 1 -I 1 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0"));
				wekaBagging.buildClassifier(wekaTrainingInstances);
				Evaluation baggingEval = new Evaluation(wekaTrainingInstances);
				baggingEval.evaluateModel(wekaBagging, wekaTestingInstances);
				parameterScores.put(baggingEval.correlationCoefficient(), "Weka Bagging Classification R^2");
				algorithmScores.put(baggingEval.correlationCoefficient(), "Weka Bagging Classification R^2");
				System.out.println(baggingEval.toSummaryString("\nResults\n======\n", false));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// run: MultilayerPerceptron -L 0.75 -M 0.2 -N 500 -V 0 -S 0 -E 25 -H a -R -D
			// options: backpropagate with a learning rate of 0.75, momentum of 0.2, 500 second training time, 0 validation set size, seed of 0, validation threshold of 25, (number of attributes + number of classes) / 2 hidden layers, no reset, and decay enabled
			MultilayerPerceptron wekaPerceptron = new MultilayerPerceptron();
			try {
				wekaPerceptron.setOptions(Utils.splitOptions("-L 0.75 -M 0.2 -N 500 -V 0 -S 0 -E 25 -H a -R -D"));
				wekaPerceptron.buildClassifier(wekaTrainingInstances);
				Evaluation perceptronEval = new Evaluation(wekaTrainingInstances);
				perceptronEval.evaluateModel(wekaPerceptron, wekaTestingInstances);
				parameterScores.put(perceptronEval.correlationCoefficient(), "Weka Multi-Layer Perceptron Classification R^2");
				algorithmScores.put(perceptronEval.correlationCoefficient(), "Weka Multi-Layer Perceptron Classification R^2");
				System.out.println(perceptronEval.toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaPerceptron);
			} catch (Exception e) {
				e.printStackTrace();
			}

			/*
			// test Weka meta-classification with a nominal or string class
			// run: ClassificationViaRegression -W weka.classifiers.trees.M5P -- -U -M 4.0
			// options: use un-smoothed with a minimum of 2 instances
			ClassificationViaRegression wekaRegression = new ClassificationViaRegression();
			try {
				wekaRegression.setOptions(Utils.splitOptions("-W weka.classifiers.trees.M5P -- -U -M 4.0"));
				wekaRegression.buildClassifier(wekaTrainingInstances);
				Evaluation regressionEval = new Evaluation(wekaTrainingInstancesString);
				regressionEval.evaluateModel(wekaRegression, wekaTestingInstancesString);
				parameterScores.put(regressionEval.correlationCoefficient(), "Weka Regression Classification R^2");
				algorithmScores.put(regressionEval.correlationCoefficient(), "Weka Bagging Classification R^2");
				System.out.println(regressionEval.toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaRegression);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// run: MultiClassClassifier -M 0 -R 2.0 -S 1 -W weka.classifiers.functions.SGD -F 0 -L 0.01 -R 1.0E-4 -E 500
			// options: use the "1-against-all" method with a random width factor of 2 and 1 seed using a stochastic gradient descent (SGD) with a hingle loss (SVM) function, learning rate of 0.01, lambda of .00001, and 500 epochs
			// alternative: -W weka.classifiers.functions.Logistic -- -R 1.0E-8 -M -1
			// alternative: -W weka.classifiers.functions.SMO -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -M -V -1 -W 1 -K "weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 0.25"
			// alternative: -W weka.classifiers.functions.MultilayerPerceptron -L 0.75 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a -R
			// alternative: -W weka.classifiers.functions.LibLINEAR -S 0 -C 1.0 -E 0.01 -B 2.5
			MultiClassClassifier wekaMultiClass = new MultiClassClassifier();
			try {
				wekaMultiClass.setOptions(Utils.splitOptions("-W weka.classifiers.functions.SGD -F 0 -L 0.01 -R 1.0E-4 -E 500"));
				wekaMultiClass.buildClassifier(wekaTrainingInstances);
				Evaluation multiClassEval = new Evaluation(wekaTrainingInstancesString);
				multiClassEval.evaluateModel(wekaMultiClass, wekaTestingInstancesString);
				parameterScores.put(multiClassEval.correlationCoefficient(), "Weka Multi-Class Classification R^2");
				algorithmScores.put(multiClassEval.correlationCoefficient(), "Weka Multi-Class Classification R^2");
				System.out.println(multiClassEval.toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaMultiClass);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// run: BayesNet -D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 3 -G 7 -P 2 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5
			// options: use the LAGD Hill Climber search algorithm with 2 parents, 7 good operations, 3 look ahead steps, and the Bayes score type; use the Simple Estimator with an alpha of 0.5
			BayesNet wekaBayesNet = new BayesNet();
			try {
				wekaBayesNet.setOptions(Utils.splitOptions("-D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 3 -G 7 -P 2 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"));
				wekaBayesNet.buildClassifier(wekaTrainingInstances);
				Evaluation BayesNetEval = new Evaluation(wekaTrainingInstancesString);
				BayesNetEval.evaluateModel(wekaBayesNet, wekaTestingInstancesString);
				parameterScores.put(BayesNetEval.correlationCoefficient(), "Weka Bayes Network Classification R^2");
				algorithmScores.put(BayesNetEval.correlationCoefficient(), "Weka Bayes Network Classification R^2");
				System.out.println(BayesNetEval.toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaBayesNet);
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			
			
			// test Futurist Society neural network (requires jfxrt.jar library from JDK)
			Network neuralnet = new Network(0, new ChiSquaredDistribution(xData.length*yData.length), xData.length, (xData.length*yData.length) / 2, yData.length, 2 * xData.length, 3, 0.025, 1.25, yData, 100, 100, 100);
			neuralnet.run();
			parameterScores.put(1-neuralnet.getAverageError(), "Futurist Society Neural Network Classification R^2");
			algorithmScores.put(1-neuralnet.getAverageError(), "Futurist Society Neural Network Classification R^2");
			
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
		return algorithmScores.keySet().size();
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
		String report = "Rank\tScore\t\t\tParameter\n";
		int deviceRank = getNumParameters();
		for(Double r : getparameterScores().keySet()) {
			report += deviceRank + "\t" + r + "\t" + getparameterScores().get(r) + "\n";
			deviceRank--;
		}
		return report + "\n";
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
		String report = "Rank\tScore\t\t\tAlgorithm Name\n";
		int algoRank = getNumAlgorithms();
		for(Double r : getAlgorithmScores().keySet()) {
			report += algoRank + "\t" + r + "\t" + getAlgorithmScores().get(r) + "\n";
			algoRank--;
		}
		return report + "\n";
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
				FastVector<Attribute> wekaAttributes = new FastVector<Attribute>(numParameters);
				for(String p : parameterNames) { wekaAttributes.add(new Attribute(p)); }
				wekaAttributes.add(new Attribute("Class"));
				Instances newInstances = new Instances("New Data", wekaAttributes, data.length);

				// set the class instance that we want to predict
				newInstances.setClass(new Attribute("Class"));
				newInstances.setClassIndex(numParameters);

				// copy newInstances chosen observations into the main testing set and Weka's testing
				for(int obs = 0; obs < data.length; obs++) {
					Instance instance = new DenseInstance(1.0, data[obs]);
					instance.setDataset(newInstances);
					newInstances.add(instance);
				}

				// classify a single observation/Weka instance using the mean average of all algorithms' individual classifications
				// UPGRADE: find more interesting ways than the mean to produce the classification
				for(int i = 0; i < newInstances.numInstances(); i++) {
					Double instanceResultSum = 0.0;
					for(Classifier a : algorithms) {
						try {
							instanceResultSum += a.classifyInstance(newInstances.instance(i));
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