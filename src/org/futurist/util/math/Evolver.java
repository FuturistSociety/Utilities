package org.futurist.util.math;

import java.util.Random;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.BinaryMutation;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import org.apache.commons.math3.genetics.FixedElapsedTime;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.MutationPolicy;
import org.apache.commons.math3.genetics.NPointCrossover;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.RandomKey;
import org.apache.commons.math3.genetics.RandomKeyMutation;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;

public class Evolver {
	
	private static final double defaultCrossoverRate = 0.5;
	private static final double defaultDirectCopyRate = 0.25;
	private static final double defaultSelectionArityPercent = 0.5;
	private static final int defaultGenerationCount = 100;
	
	private CrossoverPolicy crossover;
	private MutationPolicy mutation;
	private StoppingCondition stopCondition;
	private GeneticAlgorithm ga;
	private ElitisticListPopulation population;
	
	public Evolver(CrossoverPolicy c, int a, ElitisticListPopulation p, MutationPolicy m, StoppingCondition s) {
		crossover = c;
		mutation = m;
		stopCondition = s;
		ga = new GeneticAlgorithm(crossover, 0.2, mutation, 0.2, new TournamentSelection(a));
		population = p;
	}
	
	public Evolver(CrossoverPolicy c, int a, Double[][] data, MutationPolicy m, StoppingCondition s) {
		crossover = c;
		mutation = m;
		stopCondition = s;
		ga = new GeneticAlgorithm(c, 0.2, mutation, 0.2, new TournamentSelection(a));
		
		population = new ElitisticListPopulation(10* data.length, data.length * defaultDirectCopyRate);
		population.addChromosome(new RandomKey<Double>(data));
	}
	
	public Evolver(Double[][] data) {
		// crossover [number of genes or parameters in each data observation * defaultCrossoverRate] 
		crossover = new NPointCrossover<Double>(new Double(Math.round(data[0].length * defaultCrossoverRate)).intValue());
		mutation = new RandomKeyMutation();
		stopCondition = new FixedGenerationCount(defaultGenerationCount);
		
		// draw [number of genes or parameters in each data observation * defaultSelectionArityPercent] chromosomes into the next tournament round
		ga = new GeneticAlgorithm(crossover, 0.2, mutation, 0.2, new TournamentSelection(new Double(Math.round(data[0].length * defaultSelectionArityPercent)).intValue()));
		
		population = new ElitisticListPopulation(10* data.length, data.length * defaultDirectCopyRate);
		population.addChromosome(new RandomKey<Double>(data));
	}
	
	/**
	 * @return the genetic algorithm.
	 */
	public GeneticAlgorithm getGeneticAlgorithm() {
		return ga;
	}

	/**
	 * @return the stopping condition.
	 */
	public StoppingCondition getStoppingCondition() {
		return stopCondition;
	}
	
	/**
	 * @return the initial population.
	 */
	public Population getInitialPopulation() {
		return population;
	}
	
	public static void main(String[] args) {		        
	
		Random xRNG = new Random();		
		Double[][] x = new Double[100][100];
		for(int i = 0; i < 100; i++) {
			for(int j = 0; j < 100; j++) {
				x[i][j] = xRNG.nextDouble();
			}
		}
		
		Evolver e = new Evolver(x);
		
		// run the algorithm
		Population initialPopulation = e.getInitialPopulation();
		Population finalPopulation = e.getGeneticAlgorithm().evolve(initialPopulation, e.getStoppingCondition());
		        
		// best chromosome from the final population
		Chromosome bestFinal = finalPopulation.getFittestChromosome();
		System.out.println(bestFinal.toString());

	}
}
