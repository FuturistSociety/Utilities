/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.List;

import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import weka.classifiers.meta.ClassificationViaRegression;
import weka.core.Instances;
import weka.core.Utils;

public class RegressionChromosome extends WekaChromosome {

	protected Boolean unsmoothed;
	protected Integer instances;

	/**
	 * Default constructor to create a <code>RegressionChromosome</code> representing a <code>ClassificationViaRegression</code> classifier.
	 * @param representation the representation of the <code>ClassificationViaRegression</code>.
	 * @param trainingSet the Weka training set
	 * @param testingSet the Weka testing set
	 */
	public RegressionChromosome(List<Object> representation, Instances trainingSet, Instances testingSet) {
		super(representation, trainingSet, testingSet);
		
		if(representation.get(0) instanceof Boolean) {
			unsmoothed = (Boolean) representation.get(0);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The unsmoothed option must be a Boolean, but was set to " + representation.get(0) + "."), (Object) representation.get(0));
		}

		if(representation.get(1) instanceof Integer && (Integer) representation.get(1) >= 1 && (Integer) representation.get(1) <= 100) {
			instances = (Integer) representation.get(1);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The number of instances must be an Integer between 1 and 100, but was set to " + representation.get(1) + "."), (Object) representation.get(1));
		}
		
		wekaClassifier = new ClassificationViaRegression();
		
		String unsmoothedOption = "";
		if(unsmoothed) { 
			unsmoothedOption = "-U";
		}
		
		try {
			wekaClassifier.setOptions(Utils.splitOptions("-W weka.classifiers.trees.M5P -- " + unsmoothedOption + " -M " + instances));
			run();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the unsmoothed options for the <code>ClassificationViaRegression</code> represented by this <code>RegressionChromosome</code>.
	 * @param u the unsmoothed option.
	 */
	public void setUnsmooted(Boolean u) {
		unsmoothed = u;
	}
	
	/**
	 * Set the instances options for the <code>ClassificationViaRegression</code> represented by this <code>RegressionChromosome</code>.
	 * @param i the number of instances.
	 */
	public void setInstances(Integer i) {
		instances = i;
	}
	
	/**
	 * Get the <code>ClassificationViaRegression</code> classifier represented by this <code>RegressionChromosome</code>.
	 * @return the <code>ClassificationViaRegression</code> classifier.
	 */
	public ClassificationViaRegression getWekaClassifier() {
		return (ClassificationViaRegression) wekaClassifier;
	}

	/**
	 * Get a copy of this <code>RegressionChromosome</code>.
	 * @return the copy.
	 */
	@Override
	public RegressionChromosome newFixedLengthChromosome(List<Object> representation) {
		return new RegressionChromosome(representation, wekaTrainingInstances, wekaTestingInstances);
	}

}
