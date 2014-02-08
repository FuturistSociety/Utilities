/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.ArrayList;
import java.security.SecureRandom;
import java.util.TreeMap;

import org.futurist.neuralnet.Network;
import org.futurist.neuralnet.evolve.Evolver;
import org.futurist.util.math.DataPreparer;
import org.futurist.util.math.StatisticalReporter;

import weka.classifiers.Classifier;
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

public class EvolvingMetaClassifier extends Thread {

	private Double[] yData;
	private String[] yDataNominal;
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
	private boolean nominal;
	private boolean trained;

	/**
	 * Default constructor to create a MetaClassifier for the given data through training rounds.
	 * @param y the target/ideal/correct values that the MetaClassifer should learn to classify.
	 * @param x independent observations (indexed in the outer array) of each parameter's value (indexed in the inner array).
	 * @param p independent variable (model parameter) names or descriptions.
	 * @param trainRatio the percentage of the dataset to use as training data in each round.
	 */
	public EvolvingMetaClassifier(Object[] y, Double[][] x, String[] p, Double trainRatio) {
		// initialize correct/ideal/target values observed for the dependent variable
		if(y instanceof Number[]) {
			nominal = false;
			yData = dataToDouble((Number[]) y);
		} else {
			nominal = true;
			yDataNominal = new String[y.length];
			for(int i = 0; i < y.length; i++) {
				yDataNominal[i] = y[i].toString();
				yData[i] = new Double(i);
			}
		}

		// initialize other data variables
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
		processedData = new double[numObservations][numParameters+1];  // +1 allows class (y-value corresponding to this observation) to be stored
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
			for(int i = 0; i < postProcess.length; i++) {
				processedData[i][j] = postProcess[i];
				processedData[i][numParameters] = j;
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
				wekaAttributes.addElement(new Attribute(p));
			}
			wekaAttributes.addElement(new Attribute("Class"));
			Instances wekaTrainingInstances = new Instances("Roud " + round + " Training Instances", wekaAttributes, trainingSetSize);
			Instances wekaTestingInstances = new Instances("Roud " + round + " Testing Instances", wekaAttributes, testingSet.length);

			// set the class instance that we want to predict
			wekaTrainingInstances.setClass(new Attribute("Class"));
			wekaTrainingInstances.setClassIndex(numParameters);
			wekaTestingInstances.setClass(new Attribute("Class"));
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
				testingSet[obs][numParameters] = yData[obs];  // store class/target value
				Instance instance = new DenseInstance(1.0, testingSet[obs]);
				instance.setDataset(wekaTestingInstances);
				if(nominal) {
					instance.setClassValue(yDataNominal[obs]);
				} else {
					instance.setClassValue(testingSet[obs][numParameters]);
				}
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
					trainingSet[trainingObs][numParameters] = yData[obs];  // store class/target value
					Instance instance = new DenseInstance(1.0, trainingSet[trainingObs]);
					instance.setDataset(wekaTrainingInstances);
					if(nominal) {
						instance.setClassValue(yDataNominal[obs]);
					} else {
						instance.setClassValue(trainingSet[trainingObs][numParameters]);
					}
					wekaTrainingInstances.add(instance);
					trainingObs++;
				}
			}

			// test Weka meta-classification
			System.out.println("\nRunning training round " + round + " with " + trainingSet.length + " training observations (" + wekaTrainingInstances.numInstances() + " Weka instances) and " + testingSet.length + " testing observations (" + wekaTestingInstances.numInstances() + " Weka instances). ");

			if(nominal) {
				
				// evolve the fittest parameters to use in Weka's ClassificationViaRegression, run the Classifier, and save its performance score
				RegressionEvolver regressionEvolver = new RegressionEvolver(wekaTrainingInstances, wekaTestingInstances, new RegressionMutation());
				ClassificationViaRegression wekaRegression = regressionEvolver.getFittestClassifier();
				Double regressionCorrelation = regressionEvolver.getBestFinalChromosome().fitness();		
				parameterScores.put(regressionCorrelation, "Weka Regression Classification R^2");
				algorithmScores.put(regressionCorrelation, "Weka Regression Classification R^2");
				System.out.println(regressionEvolver.getBestFinalChromosome().getClassifierEval().toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaRegression);

				// evolve the fittest parameters to use in Weka's MultiClassClassifier, run the Classifier, and save its performance score
				MultiClassEvolver multiClassEvolver = new MultiClassEvolver(wekaTrainingInstances, wekaTestingInstances, new BaggingMutation());
				MultiClassClassifier wekaMultiClass = multiClassEvolver.getFittestClassifier();
				Double multiClassCorrelation = multiClassEvolver.getBestFinalChromosome().fitness();
				parameterScores.put(multiClassCorrelation, "Weka Multi-Class Classification R^2");
				algorithmScores.put(multiClassCorrelation, "Weka Multi-Class Classification R^2");
				System.out.println(multiClassEvolver.getBestFinalChromosome().getClassifierEval().toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaMultiClass);

				// evolve the fittest parameters to use in Weka's BayesNet, run the Classifier, and save its performance score
				BayesNetEvolver bayesNetEvolver = new BayesNetEvolver(wekaTrainingInstances, wekaTestingInstances, new MultilayerPerceptronMutation());
				BayesNet wekaBayesNet = bayesNetEvolver.getFittestClassifier();
				Double bayesNetCorrelation = bayesNetEvolver.getBestFinalChromosome().fitness();
				parameterScores.put(bayesNetCorrelation, "Weka Bayes Network Classification R^2");
				algorithmScores.put(bayesNetCorrelation, "Weka Bayes Network Classification R^2");
				System.out.println(bayesNetEvolver.getBestFinalChromosome().getClassifierEval().toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaBayesNet);
				
			} else {

				// evolve fittest parameters to use in Weka's Bagging, run the Classifier, and save its performance score
				BaggingEvolver baggingEvolver = new BaggingEvolver(wekaTrainingInstances, wekaTestingInstances, new BaggingMutation());
				Bagging wekaBagging = baggingEvolver.getFittestClassifier();
				Double baggingCorrelation = baggingEvolver.getBestFinalChromosome().fitness();
				parameterScores.put(baggingCorrelation, "Weka Bagging Classification R^2");
				algorithmScores.put(baggingCorrelation, "Weka Bagging Classification R^2");
				System.out.println(baggingEvolver.getBestFinalChromosome().getClassifierEval().toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaBagging);

				// evolve the fittest parameters to use in Weka's MultilayerPerceptron, run the Classifier, and save its performance score
				MultilayerPerceptronEvolver multilayerPerceptronEvolver = new MultilayerPerceptronEvolver(wekaTrainingInstances, wekaTestingInstances, new MultilayerPerceptronMutation());
				MultilayerPerceptron wekaPerceptron = multilayerPerceptronEvolver.getFittestClassifier();
				Double perceptronCorrelation = multilayerPerceptronEvolver.getBestFinalChromosome().fitness();
				parameterScores.put(perceptronCorrelation, "Weka Multi-Layer Perceptron Classification R^2");
				algorithmScores.put(perceptronCorrelation, "Weka Multi-Layer Perceptron Classification R^2");
				System.out.println(multilayerPerceptronEvolver.getBestFinalChromosome().getClassifierEval().toSummaryString("\nResults\n======\n", false));
				algorithms.add(wekaPerceptron);

				// evolve the fittest parameters to use in the Futurist Society's neural network, run the neural network, and save its performance score 
				Evolver neuroEvolver = new Evolver(yData);
				Network fittestNetwork = neuroEvolver.getFittestNetwork();
				parameterScores.put(1-fittestNetwork.getAverageError(), "Futurist Society Neural Network Classification R^2");
				algorithmScores.put(1-fittestNetwork.getAverageError(), "Futurist Society Neural Network Classification R^2");
				
			}
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
				for(String p : parameterNames) { wekaAttributes.addElement(new Attribute(p)); }
				wekaAttributes.addElement(new Attribute("Class"));
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

	/**
	 * Utility method to transform any numeric data into an array of Doubles.
	 * @return the transformed data.
	 */
	public static Double[] dataToDouble(Number[] data) {
		Double[] result = new Double[data.length];
		if(data instanceof Integer[]) {
			for(int i = 0; i < data.length; i++) {
				result[i] = new Double(data[i].intValue());
			}
		} else if(data instanceof Long[]) {
			for(int i = 0; i < data.length; i++) {
				result[i] = new Double(data[i].longValue());
			}
		} else if(data instanceof Short[]) {
			for(int i = 0; i < data.length; i++) {
				result[i] = new Double(data[i].shortValue());
			}
		} else if(data instanceof Float[]) {
			for(int i = 0; i < data.length; i++) {
				result[i] = new Double(data[i].floatValue());
			}
		} else if(data instanceof Byte[]) {
			for(int i = 0; i < data.length; i++) {
				result[i] = new Double(data[i].byteValue());
			}
		} else {
			for(int i = 0; i < data.length; i++) {
				result[i] = new Double(data[i].doubleValue());
			}
		}
		return result;
	}

}
