/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public abstract class WekaChromosome extends AbstractListChromosome<Object> implements Runnable {
	
	protected SecureRandom rng;
	protected DecimalFormat decimalFormat;
	protected Instances wekaTrainingInstances;
	protected Instances wekaTestingInstances;
	protected AbstractClassifier wekaClassifier;
	protected Evaluation classifierEval;
	protected Double fitness;

	/**
	 * Default constructor to create a <code>WekaChromosome</code> representing a <code>Classifier</code>.
	 * @param representation the representation of the <code>Classifier</code>.
	 * @param trainingSet the Weka training set
	 * @param testingSet the Weka testing set
	 */
	public WekaChromosome(List<Object> representation, Instances trainingSet, Instances testingSet) {
		super(representation);
		
		decimalFormat = new DecimalFormat("#.###");
		wekaTrainingInstances = trainingSet;
		wekaTestingInstances = testingSet;
		fitness = Double.MIN_VALUE;
		
		try {
			rng = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run the Weka algorithm and set its fitness to its correlation coefficient.
	 */
	public void run() {
		try {
			final byte[] temp = new byte[512];
			rng.nextBytes(temp);
			
			wekaClassifier.buildClassifier(wekaTrainingInstances);
			classifierEval = new Evaluation(wekaTrainingInstances);
			classifierEval.evaluateModel(wekaClassifier, wekaTestingInstances);
			fitness = classifierEval.correlationCoefficient();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dummy method to satisfy the compiler.  Actual validity checking is done in the default constructor.
	 * UPGRADE: Implement the same checks as the default constructor since this class allows "set" methods.
	 */
	@Override
	protected void checkValidity(List<Object> representation) throws InvalidRepresentationException {

	}
	
	/**
	 * Get the Weka training dataset.
	 * @return the training set.
	 */
	public Instances getWekaTrainingInstances() {
		return wekaTrainingInstances;
	}

	/**
	 * Get the Weka testing (validation) dataset.
	 * @return the testing set.
	 */
	public Instances getWekaTestingInstances() {
		return wekaTestingInstances;
	}

	/**
	 * Get the <code>Classifier</code> represented by this <code>WekaChromosome</code>.
	 * @return the <code>Classifier</code>.
	 */
	public AbstractClassifier getWekaClassifier() {
		return wekaClassifier;
	}
	
	public void setWekaClassifier(AbstractClassifier c) {
		wekaClassifier = c;
	}

	/**
	 * Get the <code>Evaluation</code> for the <code>Classifier</code> represented by this <code>WekaChromosome</code>.
	 * @return the <code>Evaluation</code>.
	 */
	public Evaluation getClassifierEval() {
		return classifierEval;
	}

	/**
	 * Get the fitness of this <code>WekaChromosome</code>.
	 * @return the fitness.
	 */
	@Override
	public double fitness() {
		if(fitness == Double.MIN_VALUE) {
			run();
		}
		return fitness;
	}
	
}
