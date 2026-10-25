
package org.khalid.sorting;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortingAlgorithmsTest {

    private void testAlgorithm(int[] input, String name) {

        int[] original = input.clone();

        SortingAlgorithms sorting = new SortingAlgorithms(input);

        switch (name) {
            case "Bubble Sort" -> sorting.bubbleSort();
            case "Selection Sort" -> sorting.selectionSort();
            case "Insertion Sort" -> sorting.insertionSort();
            case "Merge Sort" -> sorting.mergeSort();
            case "Quick Sort" -> sorting.quickSort();
            default -> throw new IllegalArgumentException(name);
        }

        int[] expected = original.clone();
        Arrays.sort(expected);

        // Verify actual sorting result
        assertArrayEquals(expected, sorting.getArray(), name);

        // Replay events on the original array
        int[] replay = original.clone();

        List<SortingStep> steps = sorting.getSteps();

        for (SortingStep step : steps) {

            switch (step.type) {
                case "SWAP" -> {
                    int temp = replay[step.first];
                    replay[step.first] = replay[step.second];
                    replay[step.second] = temp;
                }

                case "WRITE" ->
                    replay[step.first] = step.value;

                case "COMPARE", "PIVOT", "DONE" -> {
                    // No array modification
                }

                default -> fail("Unknown event: " + step.type);
            }
        }

        // Verify animation replay matches sorted result
        assertArrayEquals(expected, replay,
                name + " animation replay failed");

        // Check sorting completion event
        assertFalse(steps.isEmpty());

        assertEquals("DONE", steps.getLast().type);
    }

    @Test
    void testAllSortingAlgorithms() {

        String[] algorithms = {
            "Bubble Sort",
            "Selection Sort",
            "Insertion Sort",
            "Merge Sort",
            "Quick Sort"
        };

        int[][] testCases = {
            {},
            {5},
            {2, 1},
            {5, 2, 4, 1, 3},
            {3, 3, 1, 2, 1},
            {1, 2, 3, 4, 5},
            {5, 4, 3, 2, 1},
            {-5, 10, 0, -2, 8},
            {7, 7, 7, 7}
        };

        for (String name : algorithms) {
            for (int[] arr : testCases) {
                testAlgorithm(arr, name);
            }
        }
    }
}
