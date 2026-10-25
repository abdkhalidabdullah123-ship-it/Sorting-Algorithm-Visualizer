
package org.khalid.sorting;

// A single action to display on the screen.
public class SortingStep {

    public final String type;
    public final int first;
    public final int second;
    public final int value;

    public SortingStep(String type, int first, int second, int value) {
        this.type = type;
        this.first = first;
        this.second = second;
        this.value = value;
    }
}
