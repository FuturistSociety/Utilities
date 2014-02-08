/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.List;

import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import weka.classifiers.meta.MultiClassClassifier;
import weka.core.Instances;
import weka.core.Utils;

public class MultiClassChromosome extends WekaChromosome {
	
	protected Integer method;
	protected Double randomWidth;
	protected Integer seed;
	protected Integer lossFunction;
	protected Double learnRate;
	protected Double lambda;
	protected Integer epochs;
	
	/**
	 * Default constructor to create a <code>MultiClassChromosome</code> representing a <code>MultiClassClassifier</code> classifier.
	 * @param representation the representation of the <code>MultiClassClassifier</code>.
	 * @param trainingSet the Weka training set
	 * @param testingSet the Weka testing set
	 */
	public MultiClassChromosome(List<Object> representation, Instances trainingSet, Instances testingSet) {
		super(representation, trainingSet, testingSet);
		
		if(representation.get(0) instanceof Integer && (Integer) representation.get(0) >= 0 && (Integer) representation.get(0) <= 3) {
			method = (Integer) representation.get(0);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The method option must be an Integer greater than or equal to 0 and less than or equal to 3, but was set to " + representation.get(0) + "."), (Object) representation.get(0));
		}
		
		if(representation.get(1) instanceof Double && (Double) representation.get(1) >= 1.0 && (Double) representation.get(1) <= 100.0) {
			randomWidth = (Double) representation.get(1);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The random width option must be an Integer greater than or equal to 1.0 and less than or equal to 100.0, but was set to " + representation.get(1) + "."), (Object) representation.get(1));
		}
		
		if(representation.get(2) instanceof Integer && (Integer) representation.get(2) >= 0) {
			seed = (Integer) representation.get(2);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The seed option must be an Integer greater than or equal to 0, but was set to " + representation.get(2) + "."), (Object) representation.get(2));
		}
		
		if(representation.get(3) instanceof Integer && (Integer) representation.get(3) >= 0 && (Integer) representation.get(3) <= 2) {
			lossFunction = (Integer) representation.get(3);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The loss function option must be an Integer greater than or equal to 0 and less than or equal to 2, but was set to " + representation.get(3) + "."), (Object) representation.get(3));
		}
		
		if(representation.get(4) instanceof Double && (Double) representation.get(4) >= 0.0 && (Double) representation.get(4) <= 100.0) {
			learnRate = (Double) representation.get(4);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The learning rate option must be a Double greater than or equal to 0.0 and less than or equal to 100.0, but was set to " + representation.get(4) + "."), (Object) representation.get(4));
		}
		
		if(representation.get(5) instanceof Double && (Double) representation.get(5) >= 0.0 && (Double) representation.get(5) <= 100.0) {
			lambda = (Double) representation.get(5);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The lambda option must be an Double greater than or equal to 0.0 and less than or equal to 100.0, but was set to " + representation.get(5) + "."), (Object) representation.get(5));
		}
		
		if(representation.get(6) instanceof Integer && (Integer) representation.get(6) >= 1 && (Integer) representation.get(6) <= 2500) {
			epochs = (Integer) representation.get(6);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The instances option must be an Integer greater than or equal to 1 and less than or equal to 2500, but was set to " + representation.get(6) + "."), (Object) representation.get(6));
		}
		
		wekaClassifier = new MultiClassClassifier();
		
		try {
			wekaClassifier.setOptions(Utils.splitOptions("-M " + method + " -R " + decimalFormat.format(randomWidth) + " -S " + seed + " -W weka.classifiers.functions.SGD -F " + lossFunction + " -L " + decimalFormat.format(learnRate) + " -R " + decimalFormat.format(lambda) + " -E " + epochs));
			run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Set the method option for the <code>MultiClassClassifier</code> represented by this <code>MultiClassChromosome</code>.
	 * @param m the method.
	 */
	public void setMethod(Integer m) {
		method = m;
	}

	/**
	 * Set the random width option for the <code>MultiClassClassifier</code> represented by this <code>MultiClassChromosome</code>.
	 * @param r the random width.
	 */
	public void setRandomWidth(Double r) {
		randomWidth = r;
	}

	/**
	 * Set the seed option for the <code>MultiClassClassifier</code> represented by this <code>MultiClassChromosome</code>.
	 * @param s the seed.
	 */
	public void setSeed(Integer s) {
		seed = s;
	}

	/**
	 * Set the loss function option for the <code>MultiClassClassifier</code> represented by this <code>MultiClassChromosome</code>.
	 * @param f the loss function.
	 */
	public void setLossFunction(Integer f) {
		lossFunction = f;
	}

	/**
	 * Set the learning rate option for the <code>MultiClassClassifier</code> represented by this <code>MultiClassChromosome</code>.
	 * @param l the learning rate.
	 */
	public void setLearnRate(Double l) {
		learnRate = l;
	}

	/**
	 * Set the lambda option for the <code>MultiClassClassifier</code> represented by this <code>MultiClassChromosome</code>.
	 * @param r the lambda.
	 */
	public void setLambda(Double r) {
		lambda = r;
	}

	/**
	 * Set the the number of epochs option for the <code>MultiClassClassifier</code> represented by this <code>MultiClassChromosome</code>.
	 * @param e the number of epochs.
	 */
	public void setEpochs(Integer e) {
		epochs = e;
	}

	/**
	 * Get the <code>MultiClassClassifier</code> classifier represented by this <code>MultilayerPerceptronChromosome</code>.
	 * @return the <code>MultiClassClassifier</code> classifier.
	 */
	public MultiClassClassifier getWekaClassifier() {
		return (MultiClassClassifier) wekaClassifier;
	}
	
	/**
	 * Get a copy of this <code>MultiClassChromosome</code>.
	 * @return the copy.
	 */
	@Override
	public MultiClassChromosome newFixedLengthChromosome(List<Object> representation) {
		return new MultiClassChromosome(representation, wekaTrainingInstances, wekaTestingInstances);
	}
	
}
