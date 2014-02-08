/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.List;

import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import weka.classifiers.meta.Bagging;
import weka.core.Instances;
import weka.core.Utils;

public class BaggingChromosome extends WekaChromosome {
	
	protected Integer percent;
	protected Integer seed;
	protected Integer executionSlots;
	protected Integer iterations;
	protected Integer instances;
	protected Double minVariance;
	protected Integer folds;
	protected Integer REPTreeSeed;
	protected Integer maxDepth;
	protected Double initialCount;
	
	/**
	 * Default constructor to create a <code>BaggingChromosome</code> representing a <code>Bagging</code> classifier.
	 * @param representation the representation of the <code>Bagging</code>.
	 * @param trainingSet the Weka training set
	 * @param testingSet the Weka testing set
	 */
	public BaggingChromosome(List<Object> representation, Instances trainingSet, Instances testingSet) {
		super(representation, trainingSet, testingSet);

		if(representation.get(0) instanceof Integer && (Integer) representation.get(0) >= 0 && (Integer) representation.get(0) <= 100.0) {
			percent = (Integer) representation.get(0);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The percent option must be a Integer greater than or equal to 0.0 and less than or equal to 100.0, but was set to " + representation.get(0) + "."), (Object) representation.get(0));
		}
		
		if(representation.get(1) instanceof Integer && (Integer) representation.get(1) >= 0) {
			seed = (Integer) representation.get(1);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The seeds option must be an Integer greater than or equal to 0, but was set to " + representation.get(1) + "."), (Object) representation.get(1));
		}
		
		if(representation.get(2) instanceof Integer && (Integer) representation.get(2) >= 1 && (Integer) representation.get(2) <= 100) {
			executionSlots = (Integer) representation.get(2);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The execution slots option must be an Integer greater than or equal to 1 and less than or equal to 100, but was set to " + representation.get(2) + "."), (Object) representation.get(2));
		}
		
		if(representation.get(3) instanceof Integer && (Integer) representation.get(3) >= 1 && (Integer) representation.get(3) <= 10) {
			iterations = (Integer) representation.get(3);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The iterations option must be an Integer greater than or equal to 1 and less than or equal to 10, but was set to " + representation.get(3) + "."), (Object) representation.get(3));
		}
		
		if(representation.get(4) instanceof Integer && (Integer) representation.get(4) >= 2 && (Integer) representation.get(4) <= 10) {
			//instances = (Integer) representation.get(4);
			instances = 10;
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The instances option must be an Integer greater than or equal to 2 and less than or equal to 10, but was set to " + representation.get(4) + "."), (Object) representation.get(4));
		}
		
		if(representation.get(5) instanceof Double && (Double) representation.get(5) >= 0.0001 && (Double) representation.get(5) <= 100.0) {
			minVariance = (Double) representation.get(5);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The minimum variance option must be a Double greater than or equal to 0.0001 and less than or equal to 100.0, but was set to " + representation.get(5) + "."), (Object) representation.get(5));
		}
		
		if(representation.get(6) instanceof Integer && (Integer) representation.get(6) >= 1 && (Integer) representation.get(6) < instances) {
			//folds = (Integer) representation.get(6);
			folds = 5;
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The folds option must be an Integer greater than or equal to 1 and less than the number of instances (" + instances + "), but was set to " + representation.get(6) + "."), (Object) representation.get(6));
		}
		
		if(representation.get(7) instanceof Integer && (Integer) representation.get(7) >= 0) {
			REPTreeSeed = (Integer) representation.get(7);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The REP tree seeds option must be an Integer greater than or equal to 0, but was set to " + representation.get(7) + "."), (Object) representation.get(7));
		}
		
		if(representation.get(8) instanceof Integer && (Integer) representation.get(8) >= -1 && (Integer) representation.get(8) <= 100) {
			maxDepth = (Integer) representation.get(8);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The maximum depth option must be an Integer greater than or equal to -1 and less than or equal to 100, but was set to " + representation.get(8) + "."), (Object) representation.get(8));
		}
		
		if(representation.get(9) instanceof Double && (Double) representation.get(9) >= 0.0 && (Double) representation.get(9) <= 100.0) {
			initialCount = (Double) representation.get(9);
		} else {
			throw new InvalidRepresentationException(new DummyLocalizable("The initial count option must be a Double greater than or equal to 0.0 and less than or equal to 100.0, but was set to " + representation.get(9) + "."), representation.get(9));
		}
		
		wekaClassifier = new Bagging();
		
		try {
			wekaClassifier.setOptions(Utils.splitOptions("-P " + decimalFormat.format(percent) + " -S " + seed + " -num-slots " + executionSlots + " -I " + iterations + " -W weka.classifiers.trees.REPTree -- -M " + instances + " -V " + decimalFormat.format(minVariance) + " -N " + folds + " -S " + REPTreeSeed + " -L " + maxDepth + " -I " + decimalFormat.format(initialCount)));
			run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Set the percent option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param p the percent.
	 */
	public void setPercent(Integer p) {
		percent = p;
	}

	/**
	 * Set the seed option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param s the seed.
	 */
	public void setSeed(Integer s) {
		seed = s;
	}

	/**
	 * Set the number of execution slots option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param i the number of execution slots.
	 */
	public void setExecutionSlots(Integer e) {
		executionSlots = e;
	}

	/**
	 * Set the iterations option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param i the number of iterations.
	 */
	public void setIterations(Integer i) {
		iterations = i;
	}

	/**
	 * Set the instances option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param i the number of instances.
	 */
	public void setMinInstances(Integer i) {
		instances = i;
	}

	/**
	 * Set the minimum variance option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param m the minimum variance.
	 */
	public void setMinVariance(Double m) {
		minVariance = m;
	}

	/**
	 * Set the number of folds option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param f the number of folds.
	 */
	public void setFolds(Integer f) {
		folds = f;
	}

	/**
	 * Set the REP Tree seed option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param r REP Tree seed.
	 */
	public void setREPTreeSeed(Integer r) {
		REPTreeSeed = r;
	}

	/**
	 * Set the maximum depth option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param m the maximum depth. 
	 */
	public void setMaxDepth(Integer m) {
		maxDepth = m;
	}

	/**
	 * Set the initial count option for the <code>Bagging</code> represented by this <code>BaggingChromosome</code>.
	 * @param i the initial count.
	 */
	public void setInitialCount(Double i) {
		initialCount = i;
	}

	/**
	 * Get the <code>Bagging</code> classifier represented by this <code>MultilayerPerceptronChromosome</code>.
	 * @return the <code>Bagging</code> classifier.
	 */
	public Bagging getWekaClassifier() {
		return (Bagging) wekaClassifier;
	}
	
	/**
	 * Get a copy of this <code>BaggingChromosome</code>.
	 * @return the copy.
	 */
	@Override
	public BaggingChromosome newFixedLengthChromosome(List<Object> representation) {
		return new BaggingChromosome(representation, wekaTrainingInstances, wekaTestingInstances);
	}
	
}
