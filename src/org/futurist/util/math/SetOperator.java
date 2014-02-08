/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util.math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SetOperator {

	/**
	 * Returns a set containing all elements of A and B
	 * @param setA
	 * @param setB
	 * @return the union of the two given sets.
	 */
	public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>(setA);
		tmp.addAll(setB);
		return tmp;
	}

	/**
	 * Returns a set containing the elements A and B have in common.
	 * @param setA
	 * @param setB
	 * @return the intersection of the two given sets.
	 */
	public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>();
		for (T x : setA)
			if (setB.contains(x))
				tmp.add(x);
		return tmp;
	}

	/**
	 * Returns a set containing the elements A minus any elements that were also found in B.
	 * @param setA
	 * @param setB
	 * @return the difference of the two given sets.
	 */
	public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>(setA);
		tmp.removeAll(setB);
		return tmp;
	}

	/**
	 * Returns a set combining the elements that were unique to either A or B.
	 * @param setA
	 * @param setB
	 * @return the difference of the two given sets.
	 */
	public static <T> Set<T> symDifference(Set<T> setA, Set<T> setB) {
		Set<T> tmpA;
		Set<T> tmpB;

		tmpA = union(setA, setB);
		tmpB = intersection(setA, setB);
		return difference(tmpA, tmpB);
	}

	/**
	 * Determines if A is a subset of B
	 * @param setA
	 * @param setB
	 * @return true if B contains all elements of A, or false otherwise
	 */
	public static <T> boolean isSubset(Set<T> setA, Set<T> setB) {
		return setB.containsAll(setA);
	}

	/**
	 * Determines if A is a superset of B
	 * @param setA
	 * @param setB
	 * @return true if A contains all elements of B, or false otherwise
	 */	
	public static <T> boolean isSuperset(Set<T> setA, Set<T> setB) {
		return setA.containsAll(setB);
	}

	/**
	 * Generates the superset (set of all possible sets) of the given set.
	 * @param set
	 * @return the superset.
	 */
	public static <T> Set<Set<T>> superset(Set<T> set) {
		List<T> setAsList = new ArrayList<T>(set);
		Set<Set<T>> superSet = new HashSet<Set<T>>();
		int maxNumber = (int) Math.pow(2, set.size());
		for (int number = 0; number < maxNumber; number++) {
			char[] bins = Integer.toBinaryString(number).toCharArray();
			Set<T> subset = new HashSet<T>();
			for (int i = bins.length - 1; i >= 0; i--) {
				if ((bins[i] == '1')) {
					subset.add(setAsList.get(bins.length - 1 - i));
				}
			}
			superSet.add(subset);
		}
		return superSet;
	}

}
