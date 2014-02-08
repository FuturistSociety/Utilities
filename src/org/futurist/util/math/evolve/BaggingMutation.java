/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import org.apache.commons.math3.genetics.Chromosome;

public class BaggingMutation extends WekaMutation {
	
	public static final int NUM_PARAMS = 10;

	/**
	 * Default constructor to create a <code>BaggingMutation</code>.
	 */
	public BaggingMutation() {}


	/**
	 * Mutate the given original <code>Chromosome</code> by randomly deciding how many genes to change and setting them to values randomly drawn from a range of valid values or return an error if the given <code>Chromosome</code> is not an instance of <code>BaggingChromosome</code>.
	 * @param c the original <code>Chromosome</code>.
	 * @return the mutated <code>Chromosome</code>.
	 */
	public WekaChromosome mutate(Chromosome c) {
		if(c instanceof BaggingChromosome) {
			BaggingChromosome original = (BaggingChromosome) c;

			// create a mutant representation with placeholder parameters
			BaggingChromosome mutant = original;
			Integer instances = 2;

			// randomly decide which gene to mutate
			Boolean[] paramsToChange = new Boolean[NUM_PARAMS];
			for(int i = 1; i < NUM_PARAMS; i++) {
				paramsToChange[i] = rng.nextBoolean();

				// mutate a gene if it was selected
				if(paramsToChange[i]) {
					switch(i) {
					case 0: mutant.setPercent(selectPercent());
					case 1: mutant.setSeed(selectSeed());
					case 2: mutant.setExecutionSlots(selectExecutionSlots());
					case 3: mutant.setIterations(selectIterations());
					instances = selectMinInstances();
					case 4: mutant.setMinInstances(instances);
					case 5: mutant.setMinVariance(selectMinVariance());
					case 6: mutant.setFolds(selectFolds(instances));
					case 7: mutant.setREPTreeSeed(selectREPTreeSeed());
					case 8: mutant.setMaxDepth(selectMaxDepth());
					case 9: mutant.setInitialCount(selectInitialCount());
					}
				}
			}

			return mutant;
		} else {
			//throw new MathIllegalArgumentException(new DummyLocalizable("BaggingMutation works only with BaggingChromosome, not "), original);
			System.out.println("BaggingMutation works only with BaggingChromosome");
			return null;
		}
	}

	/**
	 * Select a randomly chosen percent from 1 to 100.
	 * @return the randomly chosen percent.
	 */
	public Integer selectPercent() {
		return rng.nextInt(101);
	}
	
	/**
	 * Select a randomly chosen seed.
	 * @return the randomly chosen seed.
	 */
	public Integer selectSeed() {
		return Math.abs(rng.nextInt());
	}
	
	/**
	 * Select a randomly chosen number of execution slots from 1 to 100.
	 * @return the randomly chosen number of execution slots.
	 */
	public Integer selectExecutionSlots() {
		return Math.abs(1 + rng.nextInt(99));
	}
	
	/**
	 * Select a randomly chosen number of iterations from 1 to 10.
	 * @return the randomly chosen number of iterations.
	 */
	public Integer selectIterations() {
		return Math.abs(1 + rng.nextInt(9));
	}
	
	/**
	 * Select a randomly chosen minimum number of instances from 2 to 10.
	 * @return the randomly chosen minimum number of instances.
	 */
	public Integer selectMinInstances() {
		return 10;
	}
	
	/**
	 * Select a randomly chosen minimum variance from 0.0001 to 100.
	 * @return the randomly chosen minimum variance.
	 */
	public Double selectMinVariance() {
		return Math.abs(0.0001 + rng.nextInt(100) - Math.abs(1/rng.nextInt()));
	}
	
	/**
	 * Select a randomly chosen number of folds from 2 to the number of instances.
	 * @return the randomly chosen number of folds.
	 */
	public Integer selectFolds(Integer i) {
		//return Math.abs(2 + rng.nextInt(i - 2));
		return 5;
	}
	
	/**
	 * Select a randomly chosen number of REP Tree seeds.
	 * @return the randomly chosen number of REP Tree seeds.
	 */
	public Integer selectREPTreeSeed() {
		return rng.nextInt();
	}
	
	/**
	 * Select a randomly chosen max depth from 1 to 100.
	 * @return the randomly chosen max depth.
	 */
	public Integer selectMaxDepth() {
		return -1 + rng.nextInt(101);
	}

	/**
	 * Select a randomly chosen number initial count from 1 to 100.
	 * @return the randomly chosen number of initial count.
	 */
	public Double selectInitialCount() {
		return new Double(0.9 + rng.nextInt(100) - Math.abs(1/rng.nextInt()));
	}
}
