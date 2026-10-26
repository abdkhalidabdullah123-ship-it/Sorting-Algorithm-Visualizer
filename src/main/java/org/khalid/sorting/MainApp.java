package org.khalid.sorting;

import java.util.Random;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MainApp extends Application {

    // Bar colors remain in Java because they depend on sorting events.
    private static final String UNSORTED_RED = "#EF4444";

    // ---------- Array Data ----------
    private final Random random = new Random();

    private int[] originalArray;
    private int[] displayedArray;
    // ---------- Controller and currently displayed event ----------
    private SortingController controller;
    private SortingStep highlightedStep;

    // ---------- Live Statistics labels ----------
    private Label comparisonsValueLabel;
    private Label swapsValueLabel;
    private Label writesValueLabel;
    private Label pivotsValueLabel;
    private Label stepsValueLabel;

    // ---------- GUI Components ----------
    private final HBox barsContainer = new HBox(2);

    private StackPane chartArea;

    private Label statusLabel;
    private Label sizeValueLabel;
    private Label speedValueLabel;
    private Label elementsValueLabel;

    private ComboBox<String> algorithmBox;

    private Slider sizeSlider;
    private Slider speedSlider;

    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;

    @Override
    public void start(Stage primaryStage) {

        // Step 1: Create main layout
        BorderPane root = new BorderPane();

        root.getStyleClass().add("app-root");

        // Step 2: Build the application sections
        root.setTop(createHeader());
        root.setLeft(createControls());
        root.setCenter(createVisualization());
        root.setRight(createStatistics());
        root.setBottom(createStatusBar());

        // Connect the controller to the graphical interface.
        // Controller manages the Timeline; MainApp only draws its events.
        controller = new SortingController(new SortingController.Listener() {
            @Override
            public void onProgress(int[] visibleArray, SortingStep step,
                                   SortingStats stats, String message) {
                if (visibleArray != null) {
                    displayedArray = visibleArray;
                }
                highlightedStep = step;
                updateStatistics(stats);
                if ("Status: Sorting completed successfully!".equals(message)) {
                statusLabel.setText("✓ Sorting Completed Successfully");
                 }
                    else {
                    statusLabel.setText(message);
                    }
                renderBars();
            }

            @Override
            public void onStateChanged(SortingController.State state) {
                updatePlaybackButtons(state);
            }
        });

        // Step 3: Set spacing between sections
        BorderPane.setMargin(root.getTop(),
                new Insets(0, 0, 16, 0));

        BorderPane.setMargin(root.getCenter(),
                new Insets(0, 16, 0, 16));

        BorderPane.setMargin(root.getBottom(),
                new Insets(16, 0, 0, 0));

        // Step 4: Create Scene
        Scene scene = new Scene(root, 1280, 760);
        // Stylesheet replaces the original inline JavaFX styling.
        scene.getStylesheets().add(
                MainApp.class.getResource("/style.css").toExternalForm()
        );

        // Step 5: Configure Stage
        primaryStage.setTitle("Sorting Algorithm Visualizer");
        primaryStage.setScene(scene);

        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.setResizable(true);

        // Step 6: Generate initial array
        generateArray();

        // Update bar widths when the window is resized
        chartArea.widthProperty().addListener(
                (obs, oldWidth, newWidth) -> renderBars()
        );

        // Step 7: Display window
        primaryStage.show();

        // Initial button states
        updatePlaybackButtons(controller.getState());
    }

    // ==========================================
    // HEADER
    // ==========================================

    
private StackPane createHeader() {

    // Main Title
    Label title = new Label("Sorting Visualizer");
    title.getStyleClass().add("app-title");

    // Subtitle
    Label subtitle = new Label(
            "Interactive Algorithm Learning Platform"
    );
    subtitle.getStyleClass().add("app-subtitle");

    // Center title and subtitle
    VBox heading = new VBox(5, title, subtitle);
    heading.setAlignment(Pos.CENTER);

    // Initials on the right
    Label version = new Label("KA");
    version.getStyleClass().add("version-badge");

    // Header layout
    StackPane header = new StackPane();

    header.getChildren().addAll(heading, version);

    StackPane.setAlignment(heading, Pos.CENTER);
    StackPane.setAlignment(version, Pos.TOP_RIGHT);

    return header;
}


    // ==========================================
    // LEFT PANEL - CONTROLS
    // ==========================================

    private VBox createControls() {

        VBox controls = new VBox(14);
        controls.setPrefWidth(230);
        controls.setMinWidth(220);
        controls.getStyleClass().add("panel");
        controls.getStyleClass().add("dark-panel");

     // Center align all controls panel content
        controls.setAlignment(Pos.TOP_CENTER);

        Label heading = headingLabel("Controls");

        // Algorithm Selection
        Label algorithmLabel = smallLabel("ALGORITHM");

        algorithmBox = new ComboBox<>();

        algorithmBox.getItems().addAll(
                "Bubble Sort",
                "Selection Sort",
                "Insertion Sort",
                "Merge Sort",
                "Quick Sort"
        );

        algorithmBox.setValue("Bubble Sort");
        algorithmBox.setMaxWidth(Double.MAX_VALUE);
        algorithmBox.getStyleClass().add("algorithm-dropdown");

        algorithmBox.setOnAction(event -> {
            statusLabel.setText(
                    "Selected: " + algorithmBox.getValue()
            );
        });

        // Array Size
        Label sizeLabel = smallLabel("ARRAY SIZE");

        sizeValueLabel = smallLabel("24 Elements");

        sizeSlider = new Slider(10, 100, 24);
        sizeSlider.getStyleClass().add("modern-slider");

        sizeSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> {
                    sizeValueLabel.setText(
                            newValue.intValue() + " Elements"
                    );
                }
        );

        // Speed Control
        Label speedLabel = smallLabel("ANIMATION SPEED");

        speedValueLabel = smallLabel("1.00x");

        speedSlider = new Slider(0.25, 20.0, 1.0);
        speedSlider.getStyleClass().add("modern-slider");

        speedSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> {
                    speedValueLabel.setText(
                            String.format("%.2fx",
                                    newValue.doubleValue())
                    );

                    // Changing speed affects a running or paused Timeline.
                    if (controller != null) {
                        controller.setSpeed(newValue.doubleValue());
                    }
                }
        );

        // Buttons
        Button generateButton = createButton(
                "Generate Array", "generate-button"
        );

        startButton = createButton(
                "Start Sorting", "start-button"
        );

        pauseButton = createButton(
                "Pause", "pause-button"
        );

        resumeButton = createButton(
                "Resume", "resume-button"
        );

        Button resetButton = createButton(
                "Reset Array", "reset-button"
        );

        // Pause and Resume become available during playback.
        pauseButton.setDisable(true);
        resumeButton.setDisable(true);

        // Button Events
        generateButton.setOnAction(event -> {
            generateArray();
        });

        startButton.setOnAction(event -> {
            controller.start(originalArray, algorithmBox.getValue(), speedSlider.getValue());
        });

        pauseButton.setOnAction(event -> {
            controller.pause();
        });

        resumeButton.setOnAction(event -> {
            controller.resume();
        });

        resetButton.setOnAction(event -> {
            resetArray();
        });

        HBox playbackButtons = new HBox(
                8, pauseButton, resumeButton
        );
        HBox.setHgrow(pauseButton, Priority.ALWAYS);
        HBox.setHgrow(resumeButton, Priority.ALWAYS);

        // Add everything to the left panel
        controls.getChildren().addAll(
                heading,
                algorithmLabel,
                algorithmBox,
                sizeLabel,
                sizeValueLabel,
                sizeSlider,
                generateButton,
                startButton,
                playbackButtons,
                resetButton,
                speedLabel,
                speedValueLabel,
                speedSlider
        );

        return controls;
    }

    // ==========================================
    // CENTER - ARRAY VISUALIZATION
    // ==========================================

    private VBox createVisualization() {

        VBox visualization = new VBox(18);
        visualization.getStyleClass().add("panel");
        visualization.setMinWidth(0);

      Label heading = headingLabel(
        "Visualization"
        );

        Label description = smallLabel(
                "Each bar represents an array element"
        );

        // Center both texts
        heading.setMaxWidth(Double.MAX_VALUE);
        heading.setAlignment(Pos.CENTER);

        description.setMaxWidth(Double.MAX_VALUE);
        description.setAlignment(Pos.CENTER);

        // Container for array bars
        barsContainer.setAlignment(Pos.BOTTOM_CENTER);
        barsContainer.setPrefHeight(280);
        barsContainer.setMaxHeight(280);

        // Chart area
      
            chartArea = new StackPane(barsContainer);

            // Allow horizontal resizing
            chartArea.setMinWidth(0);
            barsContainer.setMinWidth(0);

            chartArea.setMinHeight(340);
            chartArea.getStyleClass().add("chart-area");

        VBox.setVgrow(chartArea, Priority.ALWAYS);

        // Color legend
        HBox legend = new HBox(14);
        // Center position
        legend.setAlignment(Pos.CENTER);

        legend.getChildren().addAll(
                createLegend("#EF4444", "Unsorted"),
                createLegend("#FBBF24", "Compare"),
                createLegend("#F8FAFC", "Swap"),
                createLegend("#38BDF8", "Write"),
                createLegend("#C084FC", "Pivot"),
                createLegend("#22C55E", "Sorted")
        );

        visualization.getChildren().addAll(
                heading,
                description,
                chartArea,
                legend
        );

        return visualization;
    }

    // ==========================================
    // RIGHT PANEL - STATISTICS
    // ==========================================

    private VBox createStatistics() {

        VBox statistics = new VBox(8);

        statistics.setPrefWidth(190);
        statistics.setMinWidth(180);
        statistics.getStyleClass().add("panel");
        statistics.getStyleClass().add("dark-panel");
        statistics.setAlignment(Pos.TOP_CENTER);

        Label heading = headingLabel("Statistics");

        Label comparisons = smallLabel("Comparisons");
        comparisonsValueLabel = valueLabel("0");

        Label swaps = smallLabel("Swaps");
        swapsValueLabel = valueLabel("0");

        Label writes = smallLabel("Writes");
        writesValueLabel = valueLabel("0");

        Label pivots = smallLabel("Pivots");
        pivotsValueLabel = valueLabel("0");

        Label steps = smallLabel("Operations");
        stepsValueLabel = valueLabel("0");

        Label elements = smallLabel("Elements");
        elementsValueLabel = valueLabel("24");

        statistics.getChildren().addAll(
        heading,
        createStatCard(comparisons, comparisonsValueLabel),
        createStatCard(swaps, swapsValueLabel),
        createStatCard(writes, writesValueLabel),
        createStatCard(pivots, pivotsValueLabel),
        createStatCard(steps, stepsValueLabel),
        createStatCard(elements, elementsValueLabel)
       );

        return statistics;
    }

    // ==========================================
    // BOTTOM - STATUS BAR
    // ==========================================

    private HBox createStatusBar() {

        statusLabel = new Label("Status: Ready");

        statusLabel.getStyleClass().add("status-label");
        // Update styling whenever the status message changes
statusLabel.textProperty().addListener((obs, oldText, newText) -> {

    if ("✓ Sorting Completed Successfully".equals(newText)) {

        if (!statusLabel.getStyleClass().contains("status-completed")) {
            statusLabel.getStyleClass().add("status-completed");
        }

    } else {
        statusLabel.getStyleClass().remove("status-completed");
    }
      });

        Label framework = smallLabel("JavaFX Desktop");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBar = new HBox(
                10, statusLabel, spacer, framework
        );

        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.getStyleClass().add("panel");

        return statusBar;
    }

    // ==========================================
    // GENERATE RANDOM ARRAY
    // ==========================================

    private void generateArray() {

        // Cancel any old animation before replacing the array.
        controller.reset();

        int size = (int) Math.round(sizeSlider.getValue());

        originalArray = new int[size];

        for (int i = 0; i < size; i++) {
            originalArray[i] =
                    random.nextInt(10, 101);
        }

        displayedArray = originalArray.clone();
        highlightedStep = null;

        elementsValueLabel.setText(
                String.valueOf(size)
        );

        renderBars();

        statusLabel.setText(
                "Status: New Array Generated"
        );
    }

    // ==========================================
    // DISPLAY ARRAY AS BARS
    // ==========================================

    
private void renderBars() {

    if (displayedArray == null || chartArea == null) {
        return;
    }

    // ---------- STEP 1: CLEAR OLD BARS ----------
    barsContainer.getChildren().clear();

    // ---------- STEP 2: ARRAY SIZE ----------
    int count = displayedArray.length;

    if (count == 0) {
        return;
    }

    // ---------- STEP 3: AVAILABLE CHART WIDTH ----------
    double availableWidth;

    if (chartArea.getWidth() > 0) {

        availableWidth = Math.max(
                1.0,
                chartArea.getWidth()
                - chartArea.getInsets().getLeft()
                - chartArea.getInsets().getRight()
                - 8
        );

    } else {
        availableWidth = 600;
    }

    // ---------- STEP 4: DYNAMIC BAR SPACING ----------
    double gap = Math.min(
            2.0,
            availableWidth / (count * 5.0)
    );

    barsContainer.setSpacing(gap);

    // ---------- STEP 5: DYNAMIC BAR WIDTH ----------
    double barWidth = Math.min(
            22.0,
            (availableWidth - gap * (count - 1)) / count
    );

    // ---------- STEP 6: CONTAINER WIDTH ----------
    double totalBarsWidth =
            (barWidth * count) + (gap * (count - 1));

    barsContainer.setMinWidth(0);
    barsContainer.setPrefWidth(totalBarsWidth);
    barsContainer.setMaxWidth(totalBarsWidth);

    // ---------- STEP 7: CREATE BARS ----------
    for (int i = 0; i < displayedArray.length; i++) {

        int value = displayedArray[i];

        Rectangle bar = new Rectangle(
                barWidth,
                value * 2.6
        );

        // Default color: Red for unsorted elements
        Color barColor = Color.web(UNSORTED_RED);

        // ---------- STEP 8: SORTING HIGHLIGHTS ----------
        if (highlightedStep != null) {

            String type = highlightedStep.type;

            boolean first = (i == highlightedStep.first);
            boolean second = (i == highlightedStep.second);

            if ("DONE".equals(type)) {

                barColor = Color.web("#22C55E");

            } else if ("COMPARE".equals(type)
                    && (first || second)) {

                barColor = Color.web("#FBBF24");

            } else if ("SWAP".equals(type)
                    && (first || second)) {

                barColor = Color.web("#F8FAFC");

            } else if ("WRITE".equals(type) && first) {

                barColor = Color.web("#38BDF8");

            } else if ("PIVOT".equals(type) && first) {

                barColor = Color.web("#C084FC");
            }
        }

        // ---------- STEP 9: APPLY BAR STYLE ----------
        // Apply bar colors
        bar.setFill(barColor);

        // Rounded corners
        bar.setArcWidth(4);
        bar.setArcHeight(4);

// Apply CSS hover styling
bar.getStyleClass().add("sorting-bar");

        // ---------- STEP 10: TOOLTIP ----------
        Tooltip tooltip = new Tooltip(
                "Index: " + i +
                "\nValue: " + value
        );

        Tooltip.install(bar, tooltip);

        // ---------- STEP 11: ADD BAR ----------
        barsContainer.getChildren().add(bar);
    }
}

    // ==========================================
    // CONTROLLER STATE -> BUTTON AVAILABILITY
    // ==========================================

    private void updatePlaybackButtons(SortingController.State state) {
        if (startButton == null || pauseButton == null || resumeButton == null) {
            return;
        }
        startButton.setDisable(state != SortingController.State.IDLE);
        pauseButton.setDisable(state != SortingController.State.RUNNING);
        resumeButton.setDisable(state != SortingController.State.PAUSED);

        // Avoid changing inputs in the middle of an animation.
        algorithmBox.setDisable(state != SortingController.State.IDLE);
        sizeSlider.setDisable(state != SortingController.State.IDLE);
    }

    // ==========================================
    // LIVE STATISTICS -> LABELS
    // ==========================================

    private void updateStatistics(SortingStats stats) {
        if (comparisonsValueLabel == null) {
            return;
        }
        comparisonsValueLabel.setText(String.valueOf(stats.getComparisons()));
        swapsValueLabel.setText(String.valueOf(stats.getSwaps()));
        writesValueLabel.setText(String.valueOf(stats.getWrites()));
        pivotsValueLabel.setText(String.valueOf(stats.getPivots()));
        stepsValueLabel.setText(String.valueOf(stats.getSteps()));
    }

    // ==========================================
    // RESET ARRAY
    // ==========================================

    private void resetArray() {
        if (originalArray == null) {
            return;
        }
        controller.reset(); // Cancels Timeline and resets SortingStats.
        displayedArray = originalArray.clone();
        highlightedStep = null;
        renderBars();
        statusLabel.setText("Status: Array Reset");
    }

    // ==========================================
    // REUSABLE UI HELPER METHODS
    // ==========================================

    private Label headingLabel(String text) {

        Label label = new Label(text);

        label.getStyleClass().add("section-title");

        return label;
    }

    private Label smallLabel(String text) {

        Label label = new Label(text);

        label.getStyleClass().add("small-label");

        return label;
    }

    private Label valueLabel(String text) {

        Label label = new Label(text);

        label.getStyleClass().add("stat-value");

        return label;
    }
    
private VBox createStatCard(Label label, Label value) {

    // Create a card containing label and value
    VBox card = new VBox(2, label, value);

    // Center content inside the card
    card.setAlignment(Pos.CENTER);

    // Allow card to occupy available width
    card.setMaxWidth(Double.MAX_VALUE);

    // Apply CSS styling
    card.getStyleClass().add("stat-card");

    return card;
}


    private Button createButton(
            String text, String buttonStyleClass) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);

        button.getStyleClass().addAll("action-button", buttonStyleClass);

        return button;
    }

    private HBox createLegend(
            String color, String text) {

        Rectangle colorBox = new Rectangle(
                10, 10, Color.web(color)
        );

        Label label = smallLabel(text);

        HBox item = new HBox(
                5, colorBox, label
        );

        item.setAlignment(Pos.CENTER_LEFT);

        return item;
    }

    // ==========================================
    // APPLICATION SHUTDOWN
    // ==========================================

    @Override
    public void stop() {
        // Ensure no Timeline keeps running after the window is closed.
        if (controller != null) {
            controller.stop();
        }
    }

    // ==========================================
    // MAIN METHOD
    // ==========================================

    public static void main(String[] args) {
        launch(args);
    }
}
