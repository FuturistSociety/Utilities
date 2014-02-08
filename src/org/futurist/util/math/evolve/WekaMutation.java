/**
 * @author Steven L. Moxley
 * @version 0.1
 */
package org.futurist.util.math.evolve;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.math3.genetics.MutationPolicy;

public abstract class WekaMutation extends Thread implements MutationPolicy {

	protected SecureRandom rng;

	/**
	 * Default constructor that initializes the <code>SecureRandom</code> random number generator used to select genes for mutations and their mutated values.
	 */
	public WekaMutation() {
		try {
			rng = SecureRandom.getInstance("SHA1PRNG");
			run();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run the random number generator to seed it.
	 */
	public void run() {
		final byte[] temp = new byte[512];
		rng.nextBytes(temp);
	}
}