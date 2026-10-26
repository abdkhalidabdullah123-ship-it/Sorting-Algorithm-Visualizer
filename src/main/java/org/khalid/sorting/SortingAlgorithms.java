
package org.khalid.sorting;

public class SortingAlgorithms {

    private int[] arr;
    private final java.util.List<SortingStep> steps = new java.util.ArrayList<>();

    // Constructor
    public SortingAlgorithms(int[] arr) {

         if (arr == null) {
        throw new IllegalArgumentException("Array cannot be null");
    }

      this.arr = arr.clone();
}

  
    // -------- Bubble Sort --------
    public void bubbleSort() {

        steps.clear();

        for (int i = 0; i < arr.length - 1; i++) {

            boolean swapped = false;

            for (int j = 0; j < arr.length - i - 1; j++) {

                steps.add(new SortingStep("COMPARE", j, j + 1, 0));

                if (arr[j] > arr[j + 1]) {

                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;

                    steps.add(new SortingStep("SWAP", j, j + 1, 0));

                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }

        steps.add(new SortingStep("DONE", -1, -1, 0));
    }



 
    // -------- Selection Sort --------
    public void selectionSort() {

        steps.clear();

        for (int i = 0; i < arr.length - 1; i++) {

            int minIndex = i;

            for (int j = i + 1; j < arr.length; j++) {

                // Record comparison
                steps.add(new SortingStep("COMPARE", minIndex, j, 0));

                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {

                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;

                // Record swap
                steps.add(new SortingStep("SWAP", i, minIndex, 0));
            }
        }

        // Sorting completed
        steps.add(new SortingStep("DONE", -1, -1, 0));
    }

   
    //--------Insertion Sort---------//
    public void insertionSort(){

        steps.clear();

        int n = arr.length;

        for (int i = 1; i < n; i++){
            int prev = i-1;
            int currentValue = arr[i];

            while (prev >= 0){

                // Record comparison
                steps.add(new SortingStep("COMPARE", prev, prev+1, 0));

                if (currentValue >= arr[prev]){
                    break;
                }

                arr[prev+1] = arr[prev];

                // Record shift/write
                steps.add(new SortingStep("WRITE", prev+1, -1, arr[prev+1]));

                prev--;
            }

            arr[prev+1] = currentValue;

            // Record insertion of current value
            steps.add(new SortingStep("WRITE", prev+1, -1, currentValue));
        }

        steps.add(new SortingStep("DONE", -1, -1, 0));
    }


    //---------------Merge Sort---------//

   
    // -------- Merge  Sort Main Method to call --------
   public void mergeSort() {
    steps.clear();

    mergeSort(0, arr.length - 1);

    steps.add(new SortingStep("DONE", -1, -1, 0));
}

    private void mergeSort(int l , int r){
        int s = l;
        int e = r;

        if (s >= e){
            //invalid Array ---- = for Single element Array
            return;
        }

        int mid = s + (e - s) / 2;

        // Sort the left Array
        mergeSort(s, mid);

        // Sort the right Array
        mergeSort(mid+1, e);

        // Merge Both Halves
        merge(s, e, mid);
    }

  
private void merge(int s , int e , int mid){
    int leftArrLen = mid - s + 1;
    int rightArrLen = e - mid;
    int leftArr[] = new int[leftArrLen];
    int rightArr[] = new int[rightArrLen];

    // Copy left and right half of the Array to this new left and right Array
    // Left Array
    int k = s;
    for (int i = 0; i < leftArrLen; i++){
        leftArr[i] = arr[k];
        k++;
    }

    // Right Array
    k = mid+1;
    for (int i = 0; i < rightArrLen; i++){
        rightArr[i] = arr[k];
        k++;
    }

    // Now compare elements of leftArr and RightArr
    int i = 0; // pointer on Left Array
    int j = 0; // pointer on right Array
    int m = s; // pointer on original Array

    while (i < leftArrLen && j < rightArrLen){

        // Record comparison
        steps.add(new SortingStep("COMPARE", s+i, mid+1+j, 0));

        if (leftArr[i] <= rightArr[j]){
            arr[m] = leftArr[i];

            // Record write
            steps.add(new SortingStep("WRITE", m, -1, arr[m]));

            i++;
            m++;
        }
        else{
            arr[m] = rightArr[j];

            // Record write
            steps.add(new SortingStep("WRITE", m, -1, arr[m]));

            j++;
            m++;
        }
    }

    // If the elements in left or right array are remaining then just copy them to original array

    while (i < leftArrLen){
        arr[m] = leftArr[i];

        // Record write
        steps.add(new SortingStep("WRITE", m, -1, arr[m]));

        i++;
        m++;
    }

    while (j < rightArrLen){
        arr[m] = rightArr[j];

        // Record write
        steps.add(new SortingStep("WRITE", m, -1, arr[m]));

        j++;
        m++;
    }
}

    
    //-------------Quick Sort-------//

    // Main method to start Quick Sort
    public void quickSort() {
    steps.clear();

    quickSort(0, arr.length - 1);

    steps.add(new SortingStep("DONE", -1, -1, 0));
}
    //Method Overloading Different Parameters
    private void quickSort(int s , int e){
        // s is 0 index
        // e is arr.length - 1;

        // Base Case
        if (s >= e){
            return;
        }

        int pivotIndex = partition(s , e);

        // Give left part to recursion to sort
        quickSort(s , pivotIndex-1);

        // Give right part to recursion to sort
        quickSort(pivotIndex+1 , e);

    }

    
private int partition(int s , int e){
    int pivotElement = arr[s];
    int count = 0;

    // Record pivot selection
    steps.add(new SortingStep("PIVOT", s, -1, 0));

    for (int i = s+1; i <= e; i++){

        steps.add(new SortingStep("COMPARE", s, i, 0));

        if (arr[i] <= pivotElement){
            count++;
        }
    }

    // Place Pivot Element at its correct Position
    int correctPosition = s + count;

    // Swap pivot Element  with element that is present at its correct position
    int temp = arr[correctPosition];
    arr[correctPosition] = arr[s];
    arr[s] = temp;

    if (correctPosition != s) {
        steps.add(new SortingStep("SWAP", s, correctPosition, 0));
    }

    // Ensure that the left of pivot element have smaller element and right of pivot element have greater element that pivot element

    int i = s;
    int j = e;

    while(i < correctPosition && j > correctPosition){

        while (i < correctPosition){

            steps.add(new SortingStep("COMPARE", i, correctPosition, 0));

            if (arr[i] <= pivotElement) {
                i++;
            } else {
                break;
            }
        }

        while(j > correctPosition){

            steps.add(new SortingStep("COMPARE", j, correctPosition, 0));

            if (arr[j] > pivotElement) {
                j--;
            } else {
                break;
            }
        }

        if (i < correctPosition && j > correctPosition){
            int temp1 = arr[i];
            arr[i] = arr[j];
            arr[j] = temp1;

            steps.add(new SortingStep("SWAP", i, j, 0));

            i++;
            j--;
        }
    }

    return correctPosition;
}



    // -------- Return Sorted Array --------
    public int[] getArray() {
        return arr.clone();
    }
   // -------- Return Recorded Animation Steps --------
     public java.util.List<SortingStep> getSteps() {
     return new java.util.ArrayList<>(steps);
 }
}
