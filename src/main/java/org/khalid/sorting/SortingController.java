package org.khalid.sorting;

import java.util.Arrays;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Controls the playback of events recorded by SortingAlgorithms.
 * This class owns the Timeline and the displayed array state.
 * It delegates drawing to its Listener, so it does not manipulate JavaFX bars.
 */
public class SortingController {

    private static final double BASE_STEP_MILLIS = 120;

    public enum State { IDLE, RUNNING, PAUSED }

    /** MainApp implements this interface to update its GUI after each event. */
    public interface Listener {
        void onProgress(int[] visibleArray, SortingStep step,
                        SortingStats stats, String message);

        void onStateChanged(State state);
    }

    private final Listener listener;
    private final SortingStats stats = new SortingStats();

    private Timeline timeline;
    private State state = State.IDLE;

    private List<SortingStep> steps = List.of();
    private int nextStepIndex = 0;
    private int[] visibleArray;
    private int[] expectedSortedArray;
    private SortingStep highlightedStep;

    public SortingController(Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        this.listener = listener;
    }

    public State getState() {
        return state;
    }

    /** Start a fresh replay from the original unsorted values. */
    public void start(int[] originalArray, String algorithm, double speed) {
        if (originalArray == null || state != State.IDLE) {
            return;
        }

        // The sorter records events on its own cloned array.
        SortingAlgorithms sorter = new SortingAlgorithms(originalArray);

        switch (algorithm) {
            case "Bubble Sort" -> sorter.bubbleSort();
            case "Selection Sort" -> sorter.selectionSort();
            case "Insertion Sort" -> sorter.insertionSort();
            case "Merge Sort" -> sorter.mergeSort();
            case "Quick Sort" -> sorter.quickSort();
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }

        steps = sorter.getSteps();
        expectedSortedArray = sorter.getArray();
        visibleArray = originalArray.clone();
        nextStepIndex = 0;
        highlightedStep = null;
        stats.reset();

        if (steps.isEmpty()) {
            notifyProgress("Status: No sorting steps recorded");
            return;
        }

        // Process ONE SortingStep on every KeyFrame.
        timeline = new Timeline(new KeyFrame(
                Duration.millis(BASE_STEP_MILLIS), event -> playNextStep()
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        setSpeed(speed);

        changeState(State.RUNNING);
        notifyProgress("Status: Sorting started - " + algorithm);
        timeline.play();
    }

    /** Replay precisely one COMPARE, SWAP, WRITE, PIVOT, or DONE event. */
    private void playNextStep() {
        if (nextStepIndex >= steps.size()) {
            finish(false);
            return;
        }

        SortingStep step = steps.get(nextStepIndex++);
        highlightedStep = step;
        stats.record(step);

        switch (step.type) {
            case "COMPARE" -> notifyProgress(
                    "Status: Comparing indices " + step.first + " and " + step.second);

            case "SWAP" -> {
                int temp = visibleArray[step.first];
                visibleArray[step.first] = visibleArray[step.second];
                visibleArray[step.second] = temp;
                notifyProgress("Status: Swapping indices " + step.first + " and " + step.second);
            }

            case "WRITE" -> {
                visibleArray[step.first] = step.value;
                notifyProgress("Status: Writing " + step.value + " at index " + step.first);
            }

            case "PIVOT" -> notifyProgress("Status: Pivot selected at index " + step.first);

            case "DONE" -> finish(true);

            default -> throw new IllegalArgumentException("Unknown step type: " + step.type);
        }
    }

    public void pause() {
        if (state == State.RUNNING && timeline != null) {
            timeline.pause();
            changeState(State.PAUSED);
            notifyProgress("Status: Paused at step " + nextStepIndex);
        }
    }

    public void resume() {
        if (state == State.PAUSED && timeline != null) {
            timeline.play();
            changeState(State.RUNNING);
            notifyProgress("Status: Sorting resumed");
        }
    }

    /** Changes the speed without restarting the current animation. */
    public void setSpeed(double speed) {
        if (speed <= 0 || Double.isNaN(speed) || Double.isInfinite(speed)) {
            throw new IllegalArgumentException("Speed must be a positive finite number");
        }
        if (timeline != null) {
            timeline.setRate(speed);
        }
    }

    /** Cancel playback and zero counts for Generate / Reset Array. */
    public void reset() {
        stopTimeline();
        steps = List.of();
        nextStepIndex = 0;
        expectedSortedArray = null;
        visibleArray = null;
        highlightedStep = null;
        stats.reset();
        changeState(State.IDLE);
        notifyProgress("Status: Ready");
    }

    /** Stops the animation when the application closes. */
    public void stop() {
        stopTimeline();
    }

    private void finish(boolean receivedDone) {
        stopTimeline();
        changeState(State.IDLE);

        boolean matches = expectedSortedArray != null
                && Arrays.equals(visibleArray, expectedSortedArray);

        if (receivedDone && matches) {
            // Keeping the DONE event makes all bars green in MainApp.
            notifyProgress("Status: Sorting completed successfully!");
        } else {
            // Never show a false successful/green sorted state.
            highlightedStep = null;
            notifyProgress("Status: Replay incomplete or array mismatch");
        }
    }

    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    private void changeState(State newState) {
        state = newState;
        listener.onStateChanged(state);
    }

    private void notifyProgress(String message) {
        listener.onProgress(
                visibleArray == null ? null : visibleArray.clone(),
                highlightedStep, stats, message
        );
    }
}
