/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import org.apache.commons.math3.genetics.Chromosome;

public class BayesNetMutation extends WekaMutation {
	
	public static final int NUM_PARAMS = 5;

	/**
	 * Default constructor to create a <code>BaggingMutation</code>.
	 */
	public BayesNetMutation() {}
	
	/**
	 * Mutate the given original <code>Chromosome</code> by randomly deciding how many genes to change and setting them to values randomly drawn from a range of valid values or return an error if the given <code>Chromosome</code> is not an instance of <code>BayesNetChromosome</code>.
	 * @param c the original <code>Chromosome</code>.
	 * @return the mutated <code>Chromosome</code>.
	 */
	public WekaChromosome mutate(Chromosome c) {
		if(c instanceof BayesNetChromosome) {
			BayesNetChromosome original = (BayesNetChromosome) c;

			// create a mutant representation with placeholder parameters
			BayesNetChromosome mutant = original;

			// randomly decide which gene to mutate
			Boolean[] paramsToChange = new Boolean[NUM_PARAMS];
			for(int i = 1; i < NUM_PARAMS; i++) {
				paramsToChange[i] = rng.nextBoolean();

				// mutate a gene if it was selected
				if(paramsToChange[i]) {
					switch(i) {
					case 0: mutant.setParents(selectParents());
					case 1: mutant.setGoodOps(selectGoodOps());
					case 2: mutant.setLookAhead(selectLookAhead());
					case 3: mutant.setScoreType(selectScoreType());
					case 4: mutant.setAlpha(selectAlpha());
					}
				}
			}

			return mutant;
		} else {
			//throw new MathIllegalArgumentException(new DummyLocalizable("BayesNetMutation works only with BayesNetChromosome, not "), original);
			System.out.println("BayesNetMutation works only with BayesNetChromosome");
			return null;
		}
	}

	/**
	 * Select a randomly chosen number of parents option from 1 to 25.
	 * @return the randomly chosen number of parents.
	 */
	public Integer selectParents() {
		return Math.abs(1 + rng.nextInt(25));
	}

	/**
	 * Select a randomly chosen number of good operations option from 1 to 25.
	 * @return the randomly chosen number of good operations.
	 */
	public Integer selectGoodOps() {
		return Math.abs(1 + rng.nextInt(25));
	}
	
	/**
	 * Select a randomly chosen look ahead option from 1 to 25.
	 * @return the randomly chosen look ahead option.
	 */
	public Integer selectLookAhead() {
		return Math.abs(1 + rng.nextInt(25));
	}
	
	/**
	 * Select a randomly chosen score type.
	 * @return the randomly chosen score type.
	 */
	public String selectScoreType() {
		Integer idx = rng.nextInt(5);
		String scoreType = "BAYES";
		switch(idx) {
			case 0: scoreType = "BAYES";
			case 1: scoreType = "BDeu";
			case 2: scoreType = "MDL";
			case 3: scoreType = "ENTROPY";
			case 4: scoreType = "AIC";
		}
		return scoreType;
	}
	
	/**
	 * Select a randomly chosen alpha option from 0.0 to 5.0.
	 * @return the randomly chosen alpha.
	 */
	public Double selectAlpha() {
		return new Double(Math.abs(rng.nextInt(6) - Math.abs((1/rng.nextInt()))));
	}
}
