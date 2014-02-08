/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import org.apache.commons.math3.genetics.Chromosome;

public class RegressionMutation extends WekaMutation {

	public static final int NUM_PARAMS = 2;

	/**
	 * Default constructor to create a <code>RegressionMutation</code>.
	 */
	public RegressionMutation() {}


	/**
	 * Mutate the given original <code>Chromosome</code> by randomly deciding how many genes to change and setting them to values randomly drawn from a range of valid values or return an error if the given <code>Chromosome</code> is not an instance of <code>RegressionChromosome</code>.
	 * @param c the original <code>Chromosome</code>.
	 * @return the mutated <code>Chromosome</code>.
	 */
	public WekaChromosome mutate(Chromosome c) {
		if(c instanceof RegressionChromosome) {
			RegressionChromosome original = (RegressionChromosome) c;

			// create a mutant representation with placeholder parameters
			RegressionChromosome mutant = original;

			// randomly decide which gene to mutate
			Boolean[] paramsToChange = new Boolean[NUM_PARAMS];
			for(int i = 1; i < NUM_PARAMS; i++) {
				paramsToChange[i] = rng.nextBoolean();

				// mutate a gene if it was selected
				if(paramsToChange[i]) {
					switch(i) {
					case 0: mutant.setUnsmooted(rng.nextBoolean());
					case 1: mutant.setInstances(selectInstances());
					}
				}
			}

			return mutant;
		} else {
			//throw new MathIllegalArgumentException(new DummyLocalizable("RegressionMutation works only with RegressionChromosome, not "), original);
			System.out.println("RegressionMutation works only with RegressionChromosome");
			return null;
		}
	}

	/**
	 * Select a randomly chosen number of instances from 1 to 100.
	 * @return the randomly chosen number of instances.
	 */
	public Integer selectInstances() {
		return Math.abs(1 + rng.nextInt(99));
	}

}
