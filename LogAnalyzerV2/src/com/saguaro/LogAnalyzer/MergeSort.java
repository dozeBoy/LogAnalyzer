package com.saguaro.LogAnalyzer;

public class MergeSort {

	static void merge(Comparable[] a, Comparable[] aux, int low, int mid,
			int high) {
		// possible assert?
		// possible assert ?
		for (int k = low; k <= high; k++) {
			aux[k] = a[k]; // copy original array in an auxiliary array
		}

		int i = low;
		int j = mid + 1;

		for (int k = low; k < high; k++) {
			if (i > mid) // if left half is exhausted move to right half
				a[k] = aux[j++];
			else if (j > high)
				a[k] = aux[i++];// if right half is exhausted move to left half
			else if (less(aux[i], aux[j]))
				a[k] = aux[j++]; // compare the pointers from the two halfs
			else
				// and copy back in the original array
				a[k] = aux[i++]; // the smallest of the two
		}
		// possible assert ?
	}

	private static boolean less(Comparable v, Comparable w) {

		return v.compareTo(w) < 0;
	}

	private static boolean isSorted(Comparable[] a, int low, int high) {
		// TODO Auto-generated method stub
		return false;
	}

	// recursive sorting method
	public static void sort(Comparable[] a, Comparable[] aux, int low, int high) {
		if (high <= low) return;
		int mid = low + (high - low) / 2; // compute middle of the array
		sort(a, aux, low, mid); // sort first half
		sort(a, aux, mid + 1, high); // sort second half
		merge(a, aux, low, mid, high); // merge 
	}

	public static void sort(Comparable[] a) {
		Comparable[] aux = new Comparable[a.length]; // create auxiliary array
		sort(a, aux, 0, low, a.length - 1); // and use it

	}
}
