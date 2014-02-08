/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.MutationPolicy;

import weka.classifiers.meta.Bagging;
import weka.core.Instances;

public class BaggingEvolver extends WekaEvolver {
	
	/**
	 * Default constructor to create a <code>BaggingEvolver<code>.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param m the Mutation Policy to apply.
	 */
	public BaggingEvolver(Instances trainSet, Instances testSet, MutationPolicy m) {
		super(trainSet, testSet, m);
	}

	/**
	 * Constructor to create a <code>BaggingEvolver<code> with the given parameters.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param popSize the size of the initial population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @param numGen the number of generations to run before the evolution is considered finished.
	 * @param crossoverRate the probability of the <code>CrossoverPolicy</code> being applied to parent chromosomes.
	 * @param m the Mutation Policy to apply.
	 * @param mutationRate the probability of the <code>MutationPolicy</code> being applied to offspring chromosomes.
	 */
	public BaggingEvolver(Instances trainSet, Instances testSet, int popSize, int eliteRate, int numGen, double crossoverRate, MutationPolicy m, double mutationRate) {
		super(trainSet, testSet, popSize, eliteRate, numGen, crossoverRate, m, mutationRate);
	}

	/**
	 * Generate a random <code>ElitisticListPopulation</code> of <code>BaggingChromosomes</code> of the given size with the given elitism rate.
	 * @param size the size of the population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @return the random random <code>ElitisticListPopulation</code> of <code>BaggingChromosomes</code>.
	 */
	public ElitisticListPopulation getRandomPopulation(int size, double eliteRate) {
		ElitisticListPopulation pop = new ElitisticListPopulation(size, eliteRate);
		Random rng = new Random();
		for(int i = 0; i < size; i++) {
			ArrayList<Object> chromosome = new ArrayList<Object>();
			chromosome.add(0, Math.abs(rng.nextInt(101)));
			chromosome.add(1, Math.abs(rng.nextInt()));
			chromosome.add(2, Math.abs(1 + rng.nextInt(99)));
			chromosome.add(3, Math.abs(1 + rng.nextInt(9)));
			//chromosome.add(4, Math.abs(2 + rng.nextInt(9)));
			chromosome.add(4, 10);
			chromosome.add(5, Math.abs(0.0001 + rng.nextInt(100) - Math.abs(1/rng.nextInt())));
			//chromosome.add(6, Math.abs(2 + rng.nextInt((Integer) chromosome.get(4) - 2)));
			chromosome.add(6, 5);
			chromosome.add(7, Math.abs(rng.nextInt()));
			chromosome.add(8, -1 + rng.nextInt(101));
			chromosome.add(9, Math.abs(0.9 + rng.nextInt(100) - Math.abs(1/rng.nextInt())));
			pop.addChromosome(new BaggingChromosome(chromosome, wekaTrainingInstances, wekaTestingInstances));
			System.out.println("Added BaggingChromosome to population with percent=" + chromosome.get(0) + ", seed=" + chromosome.get(1) + ", executionSlots=" + chromosome.get(2) + ", iterations=" + chromosome.get(3) + ", instances=" + chromosome.get(4) + ", minVariance=" + chromosome.get(5) + ", folds=" + chromosome.get(6) + ", REPTreeSeed=" + chromosome.get(7) + ", and initialCount=" + chromosome.get(8) + ".");
		}
		return pop;
	}

	/**
	 * Get the fittest <code>Bagging</code> classifier after the population has finished evolving.
	 * @return the fittest <code>Bagging</code> classifier.
	 */
	public Bagging getFittestClassifier() {
		BaggingChromosome fittest = (BaggingChromosome) bestFinal;
		return (Bagging) fittest.getWekaClassifier();
	}

}
