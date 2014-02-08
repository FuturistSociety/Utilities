/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.MutationPolicy;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;

import weka.classifiers.Classifier;
import weka.core.Instances;

public abstract class WekaEvolver extends Thread {

	public static final int DEFAULT_POPULATION_SIZE = 5;
	public static final double DEFAULT_ELITE_RATE = 0.25;
	public static final int DEFAULT_CROSSOVER_RATE = 1;
	public static final double DEFAULT_MUTATION_RATE = 0.1;
	public static final int DEFAULT_NUM_GENERATIONS = 10;
	public static final int DEFAULT_TIMEOUT_SECONDS = 60;

	protected Instances wekaTrainingInstances;
	protected Instances wekaTestingInstances;
	protected ElitisticListPopulation initialPop;
	protected StoppingCondition stopCond;
	protected GeneticAlgorithm ga;
	protected Population finalPop;
	protected WekaChromosome bestInitial;
	protected WekaChromosome bestFinal;
	protected MutationPolicy mutationPolicy;

	/**
	 * Default constructor to create a <code>WekaEvolver<code>.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param m the Mutation Policy to apply.
	 */
	public WekaEvolver(Instances trainSet, Instances testSet, MutationPolicy m) {
		wekaTrainingInstances = trainSet;
		wekaTestingInstances = testSet;
		mutationPolicy = m;

		// initialize a random population
		initialPop = getRandomPopulation(DEFAULT_POPULATION_SIZE, DEFAULT_ELITE_RATE);

		// set the stopping condition
		stopCond = new FixedGenerationCount(DEFAULT_NUM_GENERATIONS);

		// initialize a new genetic algorithm
		ga = new GeneticAlgorithm(new OnePointCrossover<Integer>(), DEFAULT_CROSSOVER_RATE, mutationPolicy, DEFAULT_MUTATION_RATE, new TournamentSelection(initialPop.getPopulationSize() / 2));

		run();
	}

	/**
	 * Constructor to create a <code>WekaEvolver<code> with the given parameters.
	 * @param trainSet the Weka training dataset.
	 * @param trainSet the Weka testing dataset.
	 * @param popSize the size of the initial population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @param numGen the number of generations to run before the evolution is considered finished.
	 * @param crossoverRate the probability of the <code>CrossoverPolicy</code> being applied to parent chromosomes.
	 * @param m the Mutation Policy to apply.
	 * @param mutationRate the probability of the <code>MutationPolicy</code> being applied to offspring chromosomes.
	 */
	public WekaEvolver(Instances trainSet, Instances testSet, int popSize, int eliteRate, int numGen, double crossoverRate, MutationPolicy m, double mutationRate) {
		wekaTrainingInstances = trainSet;
		wekaTestingInstances = testSet;
		mutationPolicy = m;

		// initialize a random population
		initialPop = getRandomPopulation(popSize, eliteRate);

		// set the stopping condition
		stopCond = new FixedGenerationCount(numGen);

		// initialize a new genetic algorithm
		ga = new GeneticAlgorithm(new OnePointCrossover<Integer>(), crossoverRate, mutationPolicy, mutationRate, new TournamentSelection(initialPop.getPopulationSize() / 2));

		run();
	}

	/**
	 * Generate a random <code>ElitisticListPopulation</code> of <code>/WekaChromosomes</code> of the given size with the given elitism rate.
	 * @param size the size of the population.
	 * @param eliteRate the percentage of most fit individuals to survive into the next generation.
	 * @return the random random <code>ElitisticListPopulation</code> of <code>WekaChromosomes</code>.
	 */
	public abstract ElitisticListPopulation getRandomPopulation(int size, double eliteRate);
	
	/**
	 * Run the <code>GeneticAlgorithm</code>.
	 */
	public void run() {

		long startTime = System.nanoTime();

		// save the most fit Chromosome from the initial random population for later comparison
		bestInitial = (WekaChromosome) initialPop.getFittestChromosome();

		// run the GeneticAlgorithm
		finalPop = ga.evolve(initialPop, stopCond);

		// best Chromosome from the final population
		bestFinal = (WekaChromosome) finalPop.getFittestChromosome();

		Double initialFitness = bestInitial.getFitness();
		Double finalFitness = bestFinal.getFitness();
		System.out.println("The most fit chromosome in the final population has a fitness of " + finalFitness + " compared to " + initialFitness + " in the initial random population's, a " + 100 * (finalFitness-initialFitness) / initialFitness + "% improvement.");
		System.out.println("The genetic algorithm finished running in " + (System.nanoTime()-startTime) / 1000000000.0 + " seconds.");
	}

	/**
	 * Get the fittest <code>WekaChromosome</code> after the population has finished evolving.
	 * @return the fittest <code>WekaChromosome</code>.
	 */
	public WekaChromosome getBestFinalChromosome() {
		return bestFinal;
	}

	/**
	 * Get the fittest <code>Classifier</code> after the population has finished evolving.
	 * @return the fittest <code>Classifier</code>.
	 */
	public abstract Classifier getFittestClassifier();

}
