/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.List;

import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;
import weka.core.Utils;

public class BayesNetChromosome extends WekaChromosome {

	protected Integer parents;
	protected Integer goodOps;
	protected Integer lookAhead;
	protected String scoreType;
	protected Double alpha;

	/**
	 * Default constructor to create a <code>BayesNetChromosome</code> representing a <code>BayesNet</code> classifier.
	 * @param representation the representation of the <code>BayesNet</code>.
	 * @param trainingSet the Weka training set
	 * @param testingSet the Weka testing set
	 */
	public BayesNetChromosome(List<Object> representation, Instances trainingSet, Instances testingSet) {
		super(representation, trainingSet, testingSet);

		if(representation.get(0) instanceof Integer && (Integer) representation.get(0) >= 1 && (Integer) representation.get(0) <= 25) {
			parents = (Integer) representation.get(0);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The parents option must be an Integer greater than or equal to 1 and less than or equal to 25, but was set to " + representation.get(0) + "."), (Object) representation.get(0));
		}

		if(representation.get(1) instanceof Integer && (Integer) representation.get(1) >= 1 && (Integer) representation.get(1) <= 25) {
			goodOps = (Integer) representation.get(1);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The good operations option must be an Integer greater than or equal to 1 and less than or equal to 25, but was set to " + representation.get(1) + "."), (Object) representation.get(1));
		}
		
		if(representation.get(2) instanceof Integer && (Integer) representation.get(2) >= 1 && (Integer) representation.get(2) <= 25) {
			lookAhead = (Integer) representation.get(2);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The look ahead option must be an Integer greater than or equal to 1 and less than or equal to 25, but was set to " + representation.get(2) + "."), (Object) representation.get(2));
		}
		
		if(representation.get(3) instanceof String) {
			String s = (String) representation.get(3);
			if(s.equals("BAYES") || s.equals("BDeu") || s.equals("MDL") || s.equals("ENTROPY") || s.equals("AIC")) {
				scoreType = s;	
			} else {
				scoreType = "BAYES";
			}
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The validation threshold must be an Integer between 2 and 100, but was set to " + representation.get(3) + "."), (Object) representation.get(3));
		}
		
		if(representation.get(4) instanceof Double && (Double) representation.get(4) >= 0.0 && (Double) representation.get(4) <= 5.0) {
			alpha = (Double) representation.get(4);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The alpha option must be a Double greater than or equal to 0.0 and less than or equal to 5.0, but was set to " + representation.get(4) + "."), (Object) representation.get(4));
		}
		
		wekaClassifier = new BayesNet();

		try {
			wekaClassifier.setOptions(Utils.splitOptions("-D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L " + lookAhead + " -G " + goodOps + " -P " + parents + " -S " + scoreType + " -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A " + decimalFormat.format(alpha)));
			run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the parents option for the <code>BayesNet</code> represented by this <code>BayesNetChromosome</code>.
	 * @param p the parents option.
	 */
	public void setParents(Integer p) {
		parents = p;
	}
	
	/**
	 * Set the good operations option for the <code>BayesNet</code> represented by this <code>BayesNetChromosome</code>.
	 * @param p the parents option.
	 */
	public void setGoodOps(Integer g) {
		goodOps = g;
	}
	
	/**
	 * Set the look ahead option for the <code>BayesNet</code> represented by this <code>BayesNetChromosome</code>.
	 * @param l the look ahead option.
	 */
	public void setLookAhead(Integer l) {
		lookAhead = l;
	}
	
	/**
	 * Set the score type option for the <code>BayesNet</code> represented by this <code>BayesNetChromosome</code>.
	 * @param s the score type option.
	 */
	public void setScoreType(String s) {
		if(s.equals("BAYES") || s.equals("BDeu") || s.equals("MDL") || s.equals("ENTROPY") || s.equals("AIC")) {
			scoreType = s;	
		} else {
			scoreType = "BAYES";
		}
	}
	
	/**
	 * Set the alpha option for the <code>BayesNet</code> represented by this <code>BayesNetChromosome</code>.
	 * @param a the alpha option.
	 */
	public void setAlpha(Double a) {
		alpha = a;
	}

	/**
	 * Get the <code>BayesNet</code> classifier represented by this <code>BayesNetChromosome</code>.
	 * @return the <code>BayesNet</code> classifier.
	 */
	public BayesNet getWekaClassifier() {
		return (BayesNet) wekaClassifier;
	}
	
	/**
	 * Get a copy of this <code>BayesNetChromosome</code>.
	 * @return the copy.
	 */
	@Override
	public BayesNetChromosome newFixedLengthChromosome(List<Object> representation) {
		return new BayesNetChromosome(representation, wekaTrainingInstances, wekaTestingInstances);
	}

}
