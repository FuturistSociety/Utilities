/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.MutationPolicy;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class MultilayerPerceptronEvolver extends WekaEvolver {

	/**
	 * Default constructor to create a <code>MultilayerPerceptronEvolver<code>.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param m the Mutation Policy to apply.
	 */
	public MultilayerPerceptronEvolver(Instances trainSet, Instances testSet, MutationPolicy m) {
		super(trainSet, testSet, m);
	}

	/**
	 * Constructor to create a <code>MultilayerPerceptronEvolver<code> with the given parameters.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param popSize the size of the initial population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @param numGen the number of generations to run before the evolution is considered finished.
	 * @param crossoverRate the probability of the <code>CrossoverPolicy</code> being applied to parent chromosomes.
	 * @param m the Mutation Policy to apply.
	 * @param mutationRate the probability of the <code>MutationPolicy</code> being applied to offspring chromosomes.
	 */
	public MultilayerPerceptronEvolver(Instances trainSet, Instances testSet, int popSize, int eliteRate, int numGen, double crossoverRate, MutationPolicy m, double mutationRate) {
		super(trainSet, testSet, popSize, eliteRate, numGen, crossoverRate, m, mutationRate);
	}

	/**
	 * Generate a random <code>ElitisticListPopulation</code> of <code>MultilayerPerceptronChromosomes</code> of the given size with the given elitism rate.
	 * @param size the size of the population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @return the random random <code>ElitisticListPopulation</code> of <code>MultilayerPerceptronChromosomes</code>.
	 */
	public ElitisticListPopulation getRandomPopulation(int size, double eliteRate) {
		ElitisticListPopulation pop = new ElitisticListPopulation(size, eliteRate);
		Random rng = new Random();
		for(int i = 0; i < size; i++) {
			ArrayList<Object> chromosome = new ArrayList<Object>();
			chromosome.add(0, Math.abs(0.9 + rng.nextInt(100) - Math.abs((1/rng.nextInt()))));
			chromosome.add(1, Math.abs(0.9 + rng.nextInt(100) - Math.abs((1/rng.nextInt()))));
			chromosome.add(2, Math.abs(1 + rng.nextInt(99)));
			chromosome.add(3, Math.abs(1 + rng.nextInt(999)));
			chromosome.add(4, Math.abs(rng.nextInt()));
			chromosome.add(5, Math.abs(1 + rng.nextInt(99)));
			Integer idx = rng.nextInt(4);
			String h = "a";
			switch(idx) {
				case 0: h = "a";  // (attributes + classes) / 2
				case 1: h = "i";  // attributes
				case 2: h = "o";  // classes
				case 3: h = "t";  // attributes + classes
			}
			chromosome.add(6, h);
			chromosome.add(7, rng.nextBoolean());
			chromosome.add(8, rng.nextBoolean());
			pop.addChromosome(new MultilayerPerceptronChromosome(chromosome, wekaTrainingInstances, wekaTestingInstances));
			System.out.println("Added MultilayerPerceptronChromosome to population with learnRate=" + chromosome.get(0) + ", momentum=" + chromosome.get(1) + ", secTrainTime=" + chromosome.get(2) + ", testSetSize=" + chromosome.get(3) + ", seed=" + chromosome.get(4) + ", validationThreshold=" + chromosome.get(5) + ", hiddenLayers=" + chromosome.get(6) + ", reset=" + chromosome.get(7) + ", and decay=" + chromosome.get(8) + ".");
			}
		return pop;
	}

	/**
	 * Get the fittest <code>MultilayerPerceptron</code> after the population has finished evolving.
	 * @return the fittest <code>MultilayerPerceptron</code>.
	 */
	public MultilayerPerceptron getFittestClassifier() {
		MultilayerPerceptronChromosome fittest = (MultilayerPerceptronChromosome) bestFinal;
		return fittest.getWekaClassifier();
	}

}
