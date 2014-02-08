package org.futurist.util.sort;

public class HeapSort  
{  
	public static <T extends Comparable<? super T>> T[] sort(T[] table)  
	{  
		T[] output = buildHeap(table);  
		output = shrinkHeap(table);
		return output;
	}  

	private static <T extends Comparable<? super T>> T[] buildHeap(T[] table)  
	{  
		T[] output = table;
		for (int child = 1; child < output.length; child++)  
		{  
			int parent = (child - 1) / 2;  
			while (parent >= 0 && output[parent].compareTo(output[child]) < 0)  
			{  
				swap(output, parent, child);  
				child = parent;  
				parent = (child - 1) / 2;  
			}  
		}
		return output;
	}  

	private static <T extends Comparable<? super T>> T[] shrinkHeap(T[] table)  
	{  
		T[] output = table;
		for (int n = output.length-1; n >= 0; n--)  
		{  
			swap(output, 0, n);  
			int parent = 0;  
			while (true)  
			{  
				int leftChild = 2 * parent + 1;  
				if (leftChild >= n)  
					break; // no more children  
				int rightChild = leftChild + 1;  
				int maxChild = leftChild;  
				if (rightChild < n && output[leftChild].compareTo(output[rightChild]) < 0)  
					maxChild = rightChild;  
				if (output[parent].compareTo(output[maxChild]) < 0)  
				{  
					swap(output, parent, maxChild);  
					parent = maxChild;  
				}  
				else  
					break; // exit loop  
			}  
		}  
		return output;
	}  

	private static void swap(Object[] table, int i, int j)  
	{  
		Object temp = table[i];  
		table[i] = table[j];  
		table[j] = temp;  
	}  
}  