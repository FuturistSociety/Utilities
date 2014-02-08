/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.MutationPolicy;

import weka.classifiers.meta.ClassificationViaRegression;
import weka.core.Instances;

public class RegressionEvolver extends WekaEvolver {

	/**
	 * Default constructor to create a <code>RegressionEvolver<code>.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param m the Mutation Policy to apply.
	 */
	public RegressionEvolver(Instances trainSet, Instances testSet, MutationPolicy m) {
		super(trainSet, testSet, m);
	}

	/**
	 * Constructor to create a <code>RegressionEvolver<code> with the given parameters.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param popSize the size of the initial population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @param numGen the number of generations to run before the evolution is considered finished.
	 * @param crossoverRate the probability of the <code>CrossoverPolicy</code> being applied to parent chromosomes.
	 * @param m the Mutation Policy to apply.
	 * @param mutationRate the probability of the <code>MutationPolicy</code> being applied to offspring chromosomes.
	 */
	public RegressionEvolver(Instances trainSet, Instances testSet, int popSize, int eliteRate, int numGen, double crossoverRate, MutationPolicy m, double mutationRate) {
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
			chromosome.add(0, rng.nextBoolean());
			chromosome.add(1, Math.abs(1 + rng.nextInt(99)));
			pop.addChromosome(new RegressionChromosome(chromosome, wekaTrainingInstances, wekaTestingInstances));
			System.out.println("Added RegressionChromosome to population with the unsmoothed option set to " + chromosome.get(0) + " and the number of instances set to " + chromosome.get(1) + ".");
		}
		return pop;
	}

	/**
	 * Get the fittest <code>ClassificationViaRegression</code> after the population has finished evolving.
	 * @return the fittest <code>ClassificationViaRegression</code>.
	 */
	public ClassificationViaRegression getFittestClassifier() {
		RegressionChromosome fittest = (RegressionChromosome) bestFinal;
		return (ClassificationViaRegression) fittest.getWekaClassifier();
	}

}