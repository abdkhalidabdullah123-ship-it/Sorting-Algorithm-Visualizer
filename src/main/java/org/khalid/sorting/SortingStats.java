package org.khalid.sorting;

/**
 * Holds the statistics of one sorting animation.
 * Counters advance as the UI replays recorded SortingStep events.
 * DONE marks completion and is not counted as an operation.
 */
public class SortingStats {

    private int comparisons;
    private int swaps;
    private int writes;
    private int pivots;
    private int steps;

    /** Clears all counters for a new run or a reset. */
    public void reset() {
        comparisons = 0;
        swaps = 0;
        writes = 0;
        pivots = 0;
        steps = 0;
    }

    /** Records one replayed sorting event, not a precomputed event. */
    public void record(SortingStep step) {
        if (step == null) {
            throw new IllegalArgumentException("Sorting step cannot be null");
        }

        switch (step.type) {
            case "COMPARE" -> {
                comparisons++;
                steps++;
            }
            case "SWAP" -> {
                swaps++;
                steps++;
            }
            case "WRITE" -> {
                writes++;
                steps++;
            }
            case "PIVOT" -> {
                pivots++;
                steps++;
            }
            case "DONE" -> {
                // Completion marker: not an actual array operation.
            }
            default -> throw new IllegalArgumentException(
                    "Unknown sorting step: " + step.type
            );
        }
    }

    public int getComparisons() {
        return comparisons;
    }

    public int getSwaps() {
        return swaps;
    }

    public int getWrites() {
        return writes;
    }

    public int getPivots() {
        return pivots;
    }

    public int getSteps() {
        return steps;
    }
}
