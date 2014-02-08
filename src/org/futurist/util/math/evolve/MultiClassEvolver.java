/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.MutationPolicy;

import weka.classifiers.meta.MultiClassClassifier;
import weka.core.Instances;

public class MultiClassEvolver extends WekaEvolver {

	/**
	 * Default constructor to create a <code>MultiClassEvolver<code>.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param m the Mutation Policy to apply.
	 */
	public MultiClassEvolver(Instances trainSet, Instances testSet, MutationPolicy m) {
		super(trainSet, testSet, m);
	}

	/**
	 * Constructor to create a <code>MultiClassEvolver<code> with the given parameters.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param popSize the size of the initial population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @param numGen the number of generations to run before the evolution is considered finished.
	 * @param crossoverRate the probability of the <code>CrossoverPolicy</code> being applied to parent chromosomes.
	 * @param m the Mutation Policy to apply.
	 * @param mutationRate the probability of the <code>MutationPolicy</code> being applied to offspring chromosomes.
	 */
	public MultiClassEvolver(Instances trainSet, Instances testSet, int popSize, int eliteRate, int numGen, double crossoverRate, MutationPolicy m, double mutationRate) {
		super(trainSet, testSet, popSize, eliteRate, numGen, crossoverRate, m, mutationRate);
	}

	/**
	 * Generate a random <code>ElitisticListPopulation</code> of <code>RegressionChromosomes</code> of the given size with the given elitism rate.
	 * @param size the size of the population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @return the random random <code>ElitisticListPopulation</code> of <code>RegressionChromosomes</code>.
	 */
	public ElitisticListPopulation getRandomPopulation(int size, double eliteRate) {
		ElitisticListPopulation pop = new ElitisticListPopulation(size, eliteRate);
		Random rng = new Random();
		for(int i = 0; i < size; i++) {
			ArrayList<Object> chromosome = new ArrayList<Object>();
			chromosome.add(0, Math.abs(rng.nextInt(4)));
			chromosome.add(1, Math.abs(1.0 + rng.nextInt(99) - Math.abs((1/rng.nextInt()))));
			chromosome.add(2, Math.abs(rng.nextInt()));
			chromosome.add(3, Math.abs(rng.nextInt(3)));
			chromosome.add(4, Math.abs(rng.nextInt(100) - Math.abs((1/rng.nextInt()))));
			chromosome.add(5, Math.abs(rng.nextInt(100) - Math.abs((1/rng.nextInt()))));
			chromosome.add(6, Math.abs(1 + rng.nextInt(2500)));
			pop.addChromosome(new RegressionChromosome(chromosome, wekaTrainingInstances, wekaTestingInstances));
			System.out.println("Added MultiClassChromosome to population with method=" + chromosome.get(0) + ", randomWidth=" + chromosome.get(1) + ", seed=" + chromosome.get(2) + ", lossFunction=" + chromosome.get(3) + ", learnRate=" + chromosome.get(4) + ", lambda=" + chromosome.get(5) + ", and epochs=" + chromosome.get(6) + ".");
		}
		return pop;
	}

	/**
	 * Get the fittest <code>MultiClassClassifier</code> after the population has finished evolving.
	 * @return the fittest <code>MultiClassClassifier</code>.
	 */
	public MultiClassClassifier getFittestClassifier() {
		MultiClassChromosome fittest = (MultiClassChromosome) bestFinal;
		return (MultiClassClassifier) fittest.getWekaClassifier();
	}

}