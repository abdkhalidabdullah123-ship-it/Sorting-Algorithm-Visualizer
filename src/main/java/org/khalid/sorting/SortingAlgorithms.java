
package org.khalid.sorting;

public class SortingAlgorithms {

    private int[] arr;

    // Constructor
    public SortingAlgorithms(int[] arr) {
        this.arr = arr.clone();
    }

    // -------- Bubble Sort --------
    public void bubbleSort() {

        for (int i = 0; i < arr.length - 1; i++) {

            for (int j = 0; j < arr.length - i - 1; j++) {

                if (arr[j] > arr[j + 1]) {

                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;

                }
            }
        }
    }

    // -------- Selection Sort --------
    public void selectionSort() {

        for (int i = 0; i < arr.length - 1; i++) {

            int minIndex = i;

            for (int j = i + 1; j < arr.length; j++) {

                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }

            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
    //--------Insertion Sort---------//
  public void insertionSort(){
        int n = arr.length;
        for (int i = 1; i < n; i++){
            int prev = i-1;
            int CurrentValue = arr[i];

            while (prev >= 0 && CurrentValue < arr[prev] ){
                arr[prev+1] = arr[prev];
                prev--;
            }
            arr[prev+1] = CurrentValue;
        }
    }

    //---------------Merge Sort---------//

   
    // -------- Merge Sort --------
    public void mergeSort() {
        mergeSort(0, arr.length - 1);
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
            if (leftArr[i] <= rightArr[j]){
                arr[m] = leftArr[i];
                i++;
                m++;
            }
            else{
                arr[m] = rightArr[j];
                j++;
                m++;
            }
        }

        // If the elements in left or right array are remaining then just copy them to original array

        while (i < leftArrLen){
            arr[m] = leftArr[i];
            i++;
            m++;
        }

        while (j < rightArrLen){
            arr[m] = rightArr[j];
            j++;
            m++;
        }
    }

    public void mergeSort(int l , int r){
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
    //-------------Quick Sort-------//

    // Main method to start Quick Sort
    public void quickSort() {
        quickSort(0, arr.length - 1);
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

        for (int i = s+1; i <= e; i++){
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

        // Ensure that the left of pivot element have smaller element and right of pivot element have greater element that pivot element

        int i = s;
        int j = e;

        while(i < correctPosition && j > correctPosition){

            while (arr[i] <= pivotElement){
                i++;
            }

            while(arr[j] > pivotElement){
                j--;
            }

            if (i < correctPosition && j > correctPosition){
                int temp1 = arr[i];
                arr[i] = arr[j];
                arr[j] = temp1;
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
}
