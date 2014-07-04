package com.saguaro.LogAnalyzer;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Heap<T extends Comparable<T>> {

	private ArrayList<T> items;// use arrayList so items can be added without
								// limit

	public Heap() {
		items = new ArrayList<T>();// set up the arrayList to default initial
									// capacity
	}

	// make the method private so it can be called only from the insert process
	private void siftUp() {
		int k = items.size() - 1;   // set k to the index of the last element of
									// the ArrayList
									// because is the one that is going to be
									// sifted up

		while (k > 0) { // loop until the sift up item reaches the top of the
						// heap
			int p = (k - 1) / 2;    // compute parent,p, item
									// we know this because k has children at
									// indexes 2*k+1 (left child) and 2*k+2 (right child)
									// so to find the index of the child we consider p parent and 2*p+1 being the left child
									// we set the index of the child to k so p = (k - 1)/2

			T item = items.get(k);
			T parent = items.get(p);
			if (item.compareTo(parent) > 0) { //compare that the k item is greater that the p item
											  //if so, swap
				items.set(k, parent);
				items.set(p, item);

				
				k = p; //move k up one level
			} else {
				break;
			}
		}
	}

	public void insert(T item) {
		items.add(item); // add new item to the end of the ArrayList
		siftUp();
	}

	// declare the method as private so it can be called only from the delete
	// process
	private void siftDown() {
		int k = 0;
		int l = 2 * k + 1;
		while (l < items.size()) { //loop as long as the left child index is inside the ArrayList 
								   //were the heap elements are stored
			
			int max = l, r = l + 1; 		//set max to l index and r to l+1 because l is already set 2*k+1
			if (r < items.size()) { 		//loop to check if there is a right child
				if (items.get(r).compareTo(items.get(l)) > 0) { //compare right and left children
					max++;					//if right child is greater increment max
											//max was previously set to l
				}
			}
			if (items.get(k).compareTo(items.get(max)) < 0) {//compare k item to max item
															 //if k is less then swap	
					
				//swap it
				T temp = items.get(k);
				items.set(k, items.get(max));
				items.set(max, temp);
				k = max; //set k to max
				l = 2 * k + 1; //
			} else {
				break;
			}
		}
	}

	public T delete() throws NoSuchElementException {
		if (items.size() == 0) { // throw exception if the heap is empty
			throw new NoSuchElementException();
		}
		if (items.size() == 1) { // if the heap has only 1 item
			return items.remove(0); // that item is removed and returned
		}

		// if neither of the above conditions is true then there are at least 2
		// items in the heap and the deletion process starts
		T hold = items.get(0); // first item from the top of the heap is saved
		items.set(0, items.remove(items.size() - 1));// the last item is removed
														// and written in the
														// first position
		siftDown(); // call siftdown
		return hold;
	}

	// this method returns the size of the heap
	public int size() {
		return items.size();
	}

	// check if the heap is empty or not
	public boolean isEmpty() {
		return items.isEmpty();

	}

	// method for returning a String representations of the items in the heap
	public String toString() {
		return items.toString();
	}
}
