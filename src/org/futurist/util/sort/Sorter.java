package org.futurist.util.sort;

import java.util.Comparator;

//import java.util.TimSort;

public class Sorter {

	/**
	 * Bubble Sort
	 * @param input
	 * @return
	 * Best: O(n)
	 * Average: O(n^2)
	 * Worst: O(n^2)
	 */
	public static <T extends Comparable<? super T>> T[] bubbleSort(T[] input) {
		boolean swapped;
		do{
			swapped = false;
			for(int i=0; i<input.length-1; i++){
				if(input[i].compareTo(input[i+1]) > 0){
					T tmp = input[i];
					input[i] = input[i+1];
					input[i+1] = tmp;
					swapped = true;
				}
			}
		}while(swapped);
		return input;
	}

	/**
	 * Insertion Sort
	 * @param input
	 * @return
	 * Best: O(n)
	 * Average: O(n^2)
	 * Worst: O(n^2)
	 */
	public static <T extends Comparable<? super T>> T[] insertionSort(T[] input) {
		for(int i=1; i<input.length; i++){
			T value = input[i];
			int j = i-1;
			while(j >= 0 && input[j].compareTo(value)>0){
				input[j+1] = input[j];
				j--;
			}
			input[j+1] = value;
		}
		return input;
	}

	/**
	 * Heap Sort
	 * @param input
	 * @return
	 * Best: O(n*logn)
	 * Average: O(n*logn)
	 * Worst: O(n*logn)
	 */
	public static <T extends Comparable<? super T>> T[] heapSort(T[] input) {
		return HeapSort.sort(input);
	}

	/**
	 * In-Place Stable Merge Sort
	 * @param input
	 * @return
	 * Best: O(n*logn)
	 * Average: O(n*logn)
	 * Worst: O(n*logn)
	 */
	public static <T extends Comparable<? super T>> Object[] mergeSort(T[] input, Comparator<Object> comp) {
		return InPlaceStableMergeSort.sort(input, comp);
	}

	/**
	 * In-Place Stable Quick Sort
	 * @param input
	 * @return
	 * Best: O(n*logn)
	 * Average: O(n*logn)
	 * Worst: O(n^2)
	 */
	public static <T extends Comparable<? super T>> Object[] quickSort(T[] input, Comparator<Object> comp) {
		return InPlaceStableQuicksort.sort(input, comp);
	}

	/**
	 * Google's Tim Sort
	 * @param input
	 * @return
	 * Best: O(n)
	 * Average: O(n*logn)
	 * Worst: O(n*logn)
	 */
	
	public static <T extends Comparable<? super T>> T[] timSort(T[] input, Comparator<Object> comp) {
		if(input instanceof Number[]) {
			return TimSort.sort(input, comp);
		} else {
			return null;
		}

	}
}
