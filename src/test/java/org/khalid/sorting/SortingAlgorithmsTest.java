
package org.khalid.sorting;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SortingAlgorithmsTest {

    // Different input arrays for testing
    private final int[][] testCases = {
        {5, 2, 8, 1, 3},       // Random
        {1, 2, 3, 4, 5},       // Already sorted
        {9, 7, 5, 3, 1},       // Reverse sorted
        {4, 4, 2, 2, 1},       // Duplicates
        {10},                  // Single element
        {},                    // Empty array
        {-5, 3, -1, 0, 2}     // Negative numbers
    };

    // Run selected algorithm
    private void runSort(
            SortingAlgorithms sorter,
            String algorithm) {

        switch (algorithm) {
            case "Bubble" -> sorter.bubbleSort();
            case "Selection" -> sorter.selectionSort();
            case "Insertion" -> sorter.insertionSort();
            case "Merge" -> sorter.mergeSort();
            case "Quick" -> sorter.quickSort();
            default -> fail("Unknown algorithm");
        }
    }

    // Common test method for all algorithms
    private void verifySorting(String algorithm) {

        for (int[] input : testCases) {

            int[] expected = input.clone();
            Arrays.sort(expected);

            SortingAlgorithms sorter =
                    new SortingAlgorithms(input);

            runSort(sorter, algorithm);

            assertArrayEquals(
                    expected,
                    sorter.getArray(),
                    algorithm + " failed for " +
                    Arrays.toString(input)
            );
        }
    }

    @Test
    void testBubbleSort() {
        verifySorting("Bubble");
    }

    @Test
    void testSelectionSort() {
        verifySorting("Selection");
    }

    @Test
    void testInsertionSort() {
        verifySorting("Insertion");
    }

    @Test
    void testMergeSort() {
        verifySorting("Merge");
    }

    @Test
    void testQuickSort() {
        verifySorting("Quick");
    }

    @Test
    void testOriginalArrayIsNotModified() {

        int[] input = {5, 3, 1};
        int[] original = input.clone();

        SortingAlgorithms sorter =
                new SortingAlgorithms(input);

        sorter.bubbleSort();

        assertArrayEquals(original, input);
    }

    @Test
    void testSortingCompletesWithDoneEvent() {

        String[] algorithms = {
            "Bubble", "Selection", "Insertion",
            "Merge", "Quick"
        };

        for (String algorithm : algorithms) {

            SortingAlgorithms sorter =
                    new SortingAlgorithms(
                            new int[]{5, 2, 8, 1}
                    );

            runSort(sorter, algorithm);

            List<SortingStep> steps = sorter.getSteps();

            assertFalse(steps.isEmpty());

            assertEquals(
                    "DONE",
                    steps.get(steps.size() - 1).type
            );
        }
    }

    @Test
    void testEventsReproduceSortedArray() {

        String[] algorithms = {
            "Bubble", "Selection", "Insertion",
            "Merge", "Quick"
        };

        for (String algorithm : algorithms) {

            int[] input = {5, 2, 4, 1, 3, 2};

            SortingAlgorithms sorter =
                    new SortingAlgorithms(input);

            runSort(sorter, algorithm);

            int[] replay = input.clone();

            for (SortingStep step : sorter.getSteps()) {

                switch (step.type) {

                    case "SWAP" -> {
                        int temp = replay[step.first];
                        replay[step.first] =
                                replay[step.second];
                        replay[step.second] = temp;
                    }

                    case "WRITE" ->
                        replay[step.first] = step.value;
                }
            }

            assertArrayEquals(
                    sorter.getArray(),
                    replay,
                    algorithm + " event replay mismatch"
            );
        }
    }
}
