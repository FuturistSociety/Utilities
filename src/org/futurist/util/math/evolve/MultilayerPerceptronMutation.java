/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import org.apache.commons.math3.genetics.Chromosome;

public class MultilayerPerceptronMutation extends WekaMutation {

	public static final int NUM_PARAMS = 8;

	protected Double learnRate;
	protected Double momentum;
	protected Integer secTrainTime;
	protected Integer testSetSize;
	protected Integer seed;
	protected Integer validationThreshold;
	protected String hiddenLayers;
	protected Boolean reset;
	protected Boolean deacy;

	/**
	 * Default constructor to create a <code>MultilayerPerceptronMutation</code>.
	 */
	public MultilayerPerceptronMutation() {}
	
	/**
	 * Mutate the given original <code>WekaChromosome</code> by randomly deciding how many genes to change and setting them to values randomly drawn from a range of valid values or return an error if the given <code>Chromosome</code> is not an instance of <code>MultilayerPerceptronChromosome</code>.
	 * @param c the original <code>WekaChromosome</code>.
	 * @return the mutated <code>WekaChromosome</code>.
	 */
	public Chromosome mutate(Chromosome c) {
		if(c instanceof MultilayerPerceptronChromosome) {
			MultilayerPerceptronChromosome original = (MultilayerPerceptronChromosome) c;

			// create a mutant representation with placeholder parameters
			MultilayerPerceptronChromosome mutant = original;

			// randomly decide which gene to mutate
			Boolean[] paramsToChange = new Boolean[NUM_PARAMS];
			for(int i = 1; i < NUM_PARAMS; i++) {
				paramsToChange[i] = rng.nextBoolean();

				// mutate a gene if it was selected
				if(paramsToChange[i]) {
					switch(i) {
					case 0: mutant.setLearningRate(selectLearnRate());
					case 1: mutant.setMomentum(selectMomentum());
					case 2: mutant.setSecTrainSize(selectSecondTrainingTime());
					case 3: mutant.setTestSetSize(selectValidationSetSize());
					case 4: mutant.setSeed(selectSeed());
					case 5: mutant.setValidationThreshold(selectValidationThreshold());
					case 6: mutant.setHiddenLayers(selectHiddenLayers());
					case 7: mutant.setReset(selectReset());
					case 8: mutant.setDecay(selectDecay());
					}
				}
			}

			return mutant;
		} else {
			//throw new MathIllegalArgumentException(new DummyLocalizable("MultilayerPerceptronMutation works only with MultilayerPerceptronChromosome, not "), original);
			System.out.println("MultilayerPerceptronMutation works only with MultilayerPerceptronChromosome");
			return null;
		}
	}

	/**
	 * Select a randomly chosen learning rate from 0.0 to 100.0.
	 * @return the randomly chosen learning rate.
	 */
	public Double selectLearnRate() {
		Double rate = 0.0;
		while(rate <= 0.0 || rate > 100.0) {
			rate = new Double(0.9 + rng.nextInt(100) - Math.abs((1/rng.nextInt())));
		}
		return Math.abs(rate);
	}	
	/**
	 * Select a randomly chosen momentum from 0.0 to 100.0.
	 * @return the randomly chosen momentum.
	 */
	public Double selectMomentum() {
		Double momentum = 0.0;
		while(momentum <= 0.0 || momentum > 100.0) {
			momentum = new Double(0.9 + rng.nextInt(100) - Math.abs((1/rng.nextInt())));
		}
		return Math.abs(momentum);
	}

	/**
	 * Select a randomly chosen second training time from 1 to 1000.
	 * @return the randomly chosen second training time.
	 */
	public Integer selectSecondTrainingTime() {
		return Math.abs(1 + rng.nextInt(99));
	}

	/**
	 * Select a randomly chosen validation set size from 1 to 1000.
	 * @return the randomly chosen validation (testing) set size.
	 */
	public Integer selectValidationSetSize() {
		return Math.abs(1 + rng.nextInt(999));
	}

	/**
	 * Select a randomly chosen seed.
	 * @return the randomly chosen seed.
	 */
	public Integer selectSeed() {
		return Math.abs(rng.nextInt());
	}

	/**
	 * Select a randomly chosen number validation threshold from 1 to 100.
	 * @return the randomly chosen validation threshold.
	 */
	public Integer selectValidationThreshold() {
		return Math.abs(1 + rng.nextInt(99));
	}

	/**
	 * Select a randomly chosen number of instances from 1 to 100.
	 * @return the randomly chosen number of instances.
	 */
	public String selectHiddenLayers() {
		Integer idx = rng.nextInt(4);
		String hiddenLayers = "a";
		switch(idx) {
			case 0: hiddenLayers = "a";  // (attributes + classes) / 2
			case 1: hiddenLayers = "i";  // attributes
			case 2: hiddenLayers = "o";  // classes
			case 3: hiddenLayers = "t";  // attributes + classes
		}
		return hiddenLayers;
	}

	/**
	 * Select whether the reset option is enabled.
	 * @return true if the reset options is enabled.
	 */
	public Boolean selectReset() {
		return rng.nextBoolean();
	}

	/**
	 * Select whether the decay option is enabled.
	 * @return true if the decay options is enabled.
	 */
	public Boolean selectDecay() {
		return rng.nextBoolean();
	}

}