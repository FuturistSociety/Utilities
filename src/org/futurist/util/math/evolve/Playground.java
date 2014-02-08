/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.io.File;
import java.util.Random;

import org.futurist.util.math.DataLoader;

public class Playground {

	public static void main(String[] args) {
		/*
		DataLoader xLoader = new DataLoader(new File("xData.csv"), ",");
		DataLoader yLoader = new DataLoader(new File("yData.csv"), ",");
		MetaClassifier trainer = new MetaClassifier(yLoader.getData().get(0), xLoader.getData(), xLoader.getHeaders(), 0.8);
		*/

		int numObservations = 1000;
		int numParameters = 25;
		
		String[] labels = new String[numParameters];
		for(int i = 0; i < numParameters; i++) {
			labels[i] = "Paramater #" + i;
		}
		
		Random xRNG = new Random();		
		Double[][] x = new Double[numObservations][numParameters];
		for(int i = 0; i < numObservations; i++) {
			for(int j = 0; j < numParameters; j++) {
				x[i][j] = xRNG.nextDouble();
			}
		}
		
		Random yRNG = new Random();
		Integer[] y = new Integer[numObservations];
		for(int i = 0; i < numObservations; i++) {
			y[i] = yRNG.nextInt();
		}
		
		EvolvingMetaClassifier classifier = new EvolvingMetaClassifier(y, x, labels, 0.8);
		classifier.run(2);
		System.out.println(classifier.getParameterScoreReport());
		System.out.println(classifier.getAlgorithmScoreReport());
		
		//classifier.classify(data);
	}
}