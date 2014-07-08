package com.heapify;

public class Heapify {

	static int[] someArray = { 4, 1, 3, 2, 16, 9, 10, 14, 8, 7 };

	public static void max_Heapify(int[] someArray, int i) {

		int biggest;
		int leftChild = 2 * i + 1;
		int rightChild = 2 * i + 2;
		if ((leftChild < someArray.length)
				&& (someArray[leftChild] > someArray[i])) {
			biggest = leftChild;
		} else {
			biggest = i;
		}

		if ((rightChild < someArray.length)
				&& (someArray[rightChild] > someArray[i])) {
			biggest = rightChild;
		}

		if (biggest != i) {
			swap(i, biggest);
			max_Heapify(someArray,biggest);
		}
	}

	private static void swap(int i, int biggest) {

		int temp = someArray[i];
		someArray[i] = someArray[biggest];
		someArray[biggest] = temp;

	}

	public static void main(String[] args) {

		for (int k = someArray.length - 1; k >= 0; k--) {
			max_Heapify(someArray, k);
		}

		for (int j = 0; j <= someArray.length - 1; j++) {
			System.out.println(someArray[j]);
		}
	}

}
