/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.MutationPolicy;

import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

public class BayesNetEvolver extends WekaEvolver {
	
	/**
	 * Default constructor to create a <code>BayesNetEvolver<code>.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param m the Mutation Policy to apply.
	 */
	public BayesNetEvolver(Instances trainSet, Instances testSet, MutationPolicy m) {
		super(trainSet, testSet, m);
	}

	/**
	 * Constructor to create a <code>BayesNetEvolver<code> with the given parameters.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param popSize the size of the initial population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @param numGen the number of generations to run before the evolution is considered finished.
	 * @param crossoverRate the probability of the <code>CrossoverPolicy</code> being applied to parent chromosomes.
	 * @param m the Mutation Policy to apply.
	 * @param mutationRate the probability of the <code>MutationPolicy</code> being applied to offspring chromosomes.
	 */
	public BayesNetEvolver(Instances trainSet, Instances testSet, int popSize, int eliteRate, int numGen, double crossoverRate, MutationPolicy m, double mutationRate) {
		super(trainSet, testSet, popSize, eliteRate, numGen, crossoverRate, m, mutationRate);
	}

	/**
	 * Generate a random <code>ElitisticListPopulation</code> of <code>BayesNetChromosomes</code> of the given size with the given elitism rate.
	 * @param size the size of the population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @return the random random <code>ElitisticListPopulation</code> of <code>BayesNetChromosomes</code>.
	 */
	public ElitisticListPopulation getRandomPopulation(int size, double eliteRate) {
		ElitisticListPopulation pop = new ElitisticListPopulation(size, eliteRate);
		Random rng = new Random();
		for(int i = 0; i < size; i++) {
			ArrayList<Object> chromosome = new ArrayList<Object>();
			chromosome.add(0, Math.abs(1 + rng.nextInt(25)));
			chromosome.add(1, Math.abs(1 + rng.nextInt(25)));
			chromosome.add(2, Math.abs(1 + rng.nextInt(25)));
			Integer idx = rng.nextInt(5);
			String s = "BAYES";
			switch(idx) {
				case 0: s = "BAYES";
				case 1: s = "BDeu";
				case 2: s = "MDL";
				case 3: s = "ENTROPY";
				case 4: s = "AIC";
			}
			chromosome.add(3, s);
			chromosome.add(4, Math.abs(rng.nextInt(6) - Math.abs((1/rng.nextInt()))));
			pop.addChromosome(new BaggingChromosome(chromosome, wekaTrainingInstances, wekaTestingInstances));
			System.out.println("Added BayesNetChromosome to population with parents=" + chromosome.get(0) + ", goodOps=" + chromosome.get(1) + ", lookAhead=" + chromosome.get(2) + ", scoreTyep=" + chromosome.get(3) + ", and alpha=" + chromosome.get(4) + ".");
			}
		return pop;
	}

	/**
	 * Get the fittest <code>BayesNet</code> classifier after the population has finished evolving.
	 * @return the fittest <code>BayesNet</code> classifier.
	 */
	public BayesNet getFittestClassifier() {
		BayesNetChromosome fittest = (BayesNetChromosome) bestFinal;
		return (BayesNet) fittest.getWekaClassifier();
	}

}
