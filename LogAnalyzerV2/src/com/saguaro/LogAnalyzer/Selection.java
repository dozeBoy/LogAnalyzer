package com.saguaro.LogAnalyzer;

public class Selection {

	public static void sort(Comparable[] a) {
		int N = a.length;// length of array

		for (int i = 0; i < N; i++) { // loop initial array
			int min = i;
			for (int j = i + 1; j < N; j++) { // second inner loop that finds
												// the min
				if (less(a[j], a[min]))
					min = j;
				exch(a, i, min);// swap i with min
			}
		}
	}

	public static boolean less(Comparable v, Comparable w) {
		return v.compareTo(w) < 0;

	}

	public static void exch(Comparable[] a, int i, int j) {
		Comparable swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}
}
