/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.List;

import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.Utils;

public class MultilayerPerceptronChromosome extends WekaChromosome {
	
	protected Double learnRate;
	protected Double momentum;
	protected Integer secTrainTime;
	protected Integer testSetSize;
	protected Integer seed;
	protected Integer validationThreshold;
	protected String hiddenLayers;
	protected Boolean reset;
	protected Boolean decay;

	/**
	 * Default constructor to create a <code>MultilayerPerceptronChromosome</code> representing a <code>MultilayerPerceptronChromosome</code> classifier.
	 * @param representation the representation of the <code>MultilayerPerceptron</code>.
	 * @param trainingSet the Weka training set
	 * @param testingSet the Weka testing set
	 */
	public MultilayerPerceptronChromosome(List<Object> representation, Instances trainingSet, Instances testingSet) {
		super(representation, trainingSet, testingSet);

		if(representation.get(0) instanceof Double && (Double) representation.get(0) >= 0.0 && (Double) representation.get(0) <= 100.0) {
			learnRate = (Double) representation.get(0);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The learning rate option must be a Double between 0.0 and 100.0, but was set to " + representation.get(0) + "."), (Object) representation.get(0));
		}
		if(representation.get(1) instanceof Double && (Double) representation.get(1) >= 0.0 && (Double) representation.get(1) <= 100.0) {
			momentum = (Double) representation.get(1);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The momentum must be an Double between 0.0 and 100.0, but was set to " + representation.get(1) + "."), (Object) representation.get(1));
		}
		if(representation.get(2) instanceof Integer && (Integer) representation.get(2) >= 2 && (Integer) representation.get(2) <= 100) {
			secTrainTime = (Integer) representation.get(2);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The second training size must be an Integer between 1 and 100, but was set to " + representation.get(2) + "."), (Object) representation.get(2));
		}
		if(representation.get(3) instanceof Integer && (Integer) representation.get(3) >= 1 && (Integer) representation.get(3) <= 1000) {
			testSetSize = (Integer) representation.get(3);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The validation set size must be an Integer between 1 and 1000, but was set to " + representation.get(3) + "."), (Object) representation.get(3));
		}
		if(representation.get(4) instanceof Integer && (Integer) representation.get(4) >= 0) {
			seed = (Integer) representation.get(4);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The seed must be greater than 0, but was set to " + representation.get(4) + "."), (Object) representation.get(4));
		}
		if(representation.get(5) instanceof Integer && (Integer) representation.get(5) >= 0) {
			validationThreshold = (Integer) representation.get(5);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The validation threshold must be an Integer between 2 and 100, but was set to " + representation.get(5) + "."), (Object) representation.get(5));
		}
		if(representation.get(6) instanceof String) {
			String h = (String) representation.get(6);
			if(h.equals("a") || h.equals("i") || h.equals("o") || h.equals("t")) {
				hiddenLayers = h;	
			} else {
				hiddenLayers = "a";
			}
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The validation threshold must be an Integer between 2 and 100, but was set to " + representation.get(6) + "."), (Object) representation.get(6));
		}
		if(representation.get(7) instanceof Boolean) {
			reset = (Boolean) representation.get(7);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The reset option must be a Boolean " + representation.get(7) + "."), (Object) representation.get(7));
		}
		if(representation.get(8) instanceof Boolean) {
			decay = (Boolean) representation.get(8);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The decay option must be a Boolean, but was set to " + representation.get(8) + "."), (Object) representation.get(8));
		}
		
		wekaClassifier = new MultilayerPerceptron();
		
		String resetOption = "";
		if(reset) { 
			resetOption = "-R ";
		}
		String decayOption = "";
		if(decay) { 
			decayOption = "-D";
		}
		
		try {
			wekaClassifier.setOptions(Utils.splitOptions("-L " + decimalFormat.format(learnRate) + " -M " + decimalFormat.format(momentum) + " -N " + secTrainTime + " -V " + testSetSize + " -S " + seed + " -E " + validationThreshold + " -H " + hiddenLayers + " " + resetOption + decayOption));
			run();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the learning rate option for the <code>MultilayerPeceptron</code> represented by this <code>MultilayerPeceptronChromosome</code>.
	 * @param r the learning rate option.
	 */
	public void setLearningRate(Double r) {
		learnRate = r;
	}

	/**
	 * Set the momentum option for the <code>MultilayerPeceptron</code> represented by this <code>MultilayerPeceptronChromosome</code>.
	 * @param m the momentum option.
	 */
	public void setMomentum(Double m) {
		momentum = m;
	}

	/**
	 * Set the second training size option for the <code>MultilayerPeceptron</code> represented by this <code>MultilayerPeceptronChromosome</code>.
	 * @param s the second training size option.
	 */
	public void setSecTrainSize(Integer s) {
		secTrainTime = s;
	}

	/**
	 * Set the validation set size option for the <code>MultilayerPeceptron</code> represented by this <code>MultilayerPeceptronChromosome</code>.
	 * @param t the validation set size option.
	 */
	public void setTestSetSize(Integer t) {
		testSetSize = t;
	}

	/**
	 * Set the seed option for the <code>MultilayerPeceptron</code> represented by this <code>MultilayerPeceptronChromosome</code>.
	 * @param s the seed option.
	 */
	public void setSeed(Integer s) {
		seed = s;
	}

	/**
	 * Set the validation threshold option for the <code>MultilayerPeceptron</code> represented by this <code>MultilayerPeceptronChromosome</code>.
	 * @param s the validation threshold option.
	 */

	public void setValidationThreshold(Integer v) {
		validationThreshold = v;
	}

	/**
	 * Set the hidden layers option for the <code>MultilayerPeceptron</code> represented by this <code>MultilayerPeceptronChromosome</code>.
	 * @param h the hidden layers option.
	 */
	public void setHiddenLayers(String h) {
		if(h.equals("a") || h.equals("i") || h.equals("o") || h.equals("t")) {
			hiddenLayers = h;	
		} else {
			hiddenLayers = "a";
		}
	}

	/**
	 * Set the reset option for the <code>MultilayerPeceptron</code> represented by this <code>MultilayerPeceptronChromosome</code>.
	 * @param r the reset option.
	 */
	public void setReset(Boolean r) {
		reset = r;
	}

	/**
	 * Set the decay option for the <code>MultilayerPerceptron</code> represented by this <code>MultilayerPerceptronChromosome</code>.
	 * @param d the decay option.
	 */
	public void setDecay(Boolean d) {
		decay = d;
	}

	/**
	 * Get the <code>MultilayerPerceptron</code> classifier represented by this <code>MultilayerPerceptronChromosome</code>.
	 * @return the <code>MultilayerPeceptron</code> classifier.
	 */
	public MultilayerPerceptron getWekaClassifier() {
		return (MultilayerPerceptron) wekaClassifier;
	}
	
	/**
	 * Get a copy of this <code>MultilayerPerceptronChromosome</code>.
	 * @return the copy.
	 */
	@Override
	public MultilayerPerceptronChromosome newFixedLengthChromosome(List<Object> representation) {
		return new MultilayerPerceptronChromosome(representation, wekaTrainingInstances, wekaTestingInstances);
	}
	
}
