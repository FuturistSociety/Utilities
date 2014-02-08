package org.futurist.util.math.evolve;

import org.apache.commons.math3.genetics.Chromosome;

public class MultiClassMutation extends WekaMutation {

	public static final int NUM_PARAMS = 7;

	/**
	 * Default constructor to create a <code>MultiClassMutation</code>.
	 */
	public MultiClassMutation() {}
	
	/**
	 * Mutate the given original <code>Chromosome</code> by randomly deciding how many genes to change and setting them to values randomly drawn from a range of valid values or return an error if the given <code>Chromosome</code> is not an instance of <code>MultiClassChromosome</code>.
	 * @param c the original <code>Chromosome</code>.
	 * @return the mutated <code>Chromosome</code>.
	 */
	public WekaChromosome mutate(Chromosome c) {
		if(c instanceof MultiClassChromosome) {
			MultiClassChromosome original = (MultiClassChromosome) c;

			// create a mutant representation with placeholder parameters
			MultiClassChromosome mutant = original;

			// randomly decide which gene to mutate
			Boolean[] paramsToChange = new Boolean[NUM_PARAMS];
			for(int i = 1; i < NUM_PARAMS; i++) {
				paramsToChange[i] = rng.nextBoolean();

				// mutate a gene if it was selected
				if(paramsToChange[i]) {
					switch(i) {
					case 0: mutant.setMethod(selectMethod());
					case 1: mutant.setRandomWidth(selectRandomWidth());
					case 2: mutant.setSeed(selectSeed());
					case 3: mutant.setLossFunction(selectLossFunction());
					case 4: mutant.setLearnRate(selectLearnRate());
					case 5: mutant.setLambda(selectLambda());
					case 6: mutant.setEpochs(selectEpochs());
					}
				}
			}

			return mutant;
		} else {
			//throw new MathIllegalArgumentException(new DummyLocalizable("MultiClassMutation works only with RegressionChromosome, not "), original);
			System.out.println("MultiClassMutation works only with RegressionChromosome");
			return null;
		}
	}

	/**
	 * Select a randomly chosen method.  0 means "1-against-all"; 1 means "random correction code"; 2 means "exhaustive correction code"; and 3 means "1-against-1".
	 * @return the randomly chosen method.
	 */
	public Integer selectMethod() {
		return Math.abs(rng.nextInt(4));
	}
	
	/**
	 * Select a randomly width from 1.0 to 100.0.
	 * @return the random width.
	 */
	public Double selectRandomWidth() {
		return Math.abs(1.0 + rng.nextInt(99) - Math.abs((1/rng.nextInt())));
	}
	
	/**
	 * Select a randomly chosen seed.
	 * @return the randomly chosen seed.
	 */
	public Integer selectSeed() {
		return Math.abs(rng.nextInt());
	}
	
	/**
	 * Select a randomly chosen loss function.  0 means "hinge loss"; 1 means "logistic loss"; and 2 means "squared loss".
	 * @return the randomly chosen loss function.
	 */
	public Integer selectLossFunction() {
		return Math.abs(rng.nextInt(3));
	}
	
	/**
	 * Select a randomly chosen learning rate from 0.0 to 100.0.
	 * @return the randomly chosen learning rate.
	 */
	public Double selectLearnRate() {
		return new Double(Math.abs(rng.nextInt(100) - Math.abs((1/rng.nextInt()))));
	}
	
	/**
	 * Select a randomly lambda from 0.0 to 100.0.
	 * @return the randomly chosen lambda value.
	 */
	public Double selectLambda() {
		return new Double(Math.abs(rng.nextInt(100) - Math.abs((1/rng.nextInt()))));
	}
	
	/**
	 * Select a randomly chosen number of instances from 1 to 2500.
	 * @return the randomly chosen number of instances.
	 */
	public Integer selectEpochs() {
		return Math.abs(1 + rng.nextInt(2500));
	}

}