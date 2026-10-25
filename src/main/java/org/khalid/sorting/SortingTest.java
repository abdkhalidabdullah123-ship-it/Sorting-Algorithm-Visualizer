
package org.khalid.sorting;

import java.util.Arrays;

public class SortingTest {

    public static void main(String[] args) {

        int[] original = {5, 2, 4, 1, 3};

        System.out.println("Original Array:");
        System.out.println(Arrays.toString(original));

        // Bubble Sort
        SortingAlgorithms bubble = new SortingAlgorithms(original);
        bubble.bubbleSort();

        System.out.println("Bubble Sort:");
        System.out.println(Arrays.toString(bubble.getArray()));

        // Selection Sort
        SortingAlgorithms selection = new SortingAlgorithms(original);
        selection.selectionSort();
        //Insertion Sort
        SortingAlgorithms insertion = new SortingAlgorithms(original);
        insertion.insertionSort();;

        System.out.println("Selection Sort:");
        System.out.println(Arrays.toString(selection.getArray()));
        System.out.println("Insertion Sort");
        System.out.println(Arrays.toString(insertion.getArray()));

        // Merge Sort
         SortingAlgorithms merge = new SortingAlgorithms(original);
         merge.mergeSort();
        // Quick Sort
        SortingAlgorithms Quick = new SortingAlgorithms(original);
        Quick.quickSort();

        System.out.println("Merge Sort");
        System.out.println(Arrays.toString(merge.getArray()));
        System.out.println("Quick Sort");
        System.out.println(Arrays.toString(Quick.getArray()));
    }
}
