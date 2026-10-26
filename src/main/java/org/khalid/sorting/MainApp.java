
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

    // ---------- Colors ----------
    private static final String BACKGROUND = "#0B1120";
    private static final String PANEL = "#172338";
    private static final String TEXT = "#F8FAFC";
    private static final String MUTED = "#94A3B8";
    private static final String BLUE = "#60A5FA";

    // ---------- Array Data ----------
    private final Random random = new Random();

    private int[] originalArray;
    private int[] displayedArray;

    // ---------- GUI Components ----------
    private final HBox barsContainer = new HBox(4);

    private StackPane chartArea;

    private Label statusLabel;
    private Label sizeValueLabel;
    private Label speedValueLabel;
    private Label elementsValueLabel;

    private ComboBox<String> algorithmBox;

    private Slider sizeSlider;
    private Slider speedSlider;

    @Override
    public void start(Stage primaryStage) {

        // Step 1: Create main layout
        BorderPane root = new BorderPane();

        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Step 2: Build the application sections
        root.setTop(createHeader());
        root.setLeft(createControls());
        root.setCenter(createVisualization());
        root.setRight(createStatistics());
        root.setBottom(createStatusBar());

        // Step 3: Set spacing between sections
        BorderPane.setMargin(root.getTop(),
                new Insets(0, 0, 16, 0));

        BorderPane.setMargin(root.getCenter(),
                new Insets(0, 16, 0, 16));

        BorderPane.setMargin(root.getBottom(),
                new Insets(16, 0, 0, 0));

        // Step 4: Create Scene
        Scene scene = new Scene(root, 1280, 760);

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
    }

    // ==========================================
    // HEADER
    // ==========================================

    private HBox createHeader() {

        Label title = new Label("Sorting Visualizer");
        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + TEXT + ";"
        );

        Label subtitle = new Label(
                "Interactive Algorithm Learning Platform"
        );

        subtitle.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 12px;"
        );

        VBox heading = new VBox(5, title, subtitle);

        Label version = new Label("JavaFX 25 • Desktop");
        version.setStyle(
                "-fx-background-color: #243552;" +
                "-fx-text-fill: #93C5FD;" +
                "-fx-padding: 9 14;" +
                "-fx-background-radius: 8;"
        );

        HBox header = new HBox(20, heading, version);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(heading, Priority.ALWAYS);

        return header;
    }

    // ==========================================
    // LEFT PANEL - CONTROLS
    // ==========================================

    private VBox createControls() {

        VBox controls = new VBox(14);
        controls.setPrefWidth(230);
        controls.setMinWidth(220);
        controls.setStyle(panelStyle());

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

        algorithmBox.setOnAction(event -> {
            statusLabel.setText(
                    "Selected: " + algorithmBox.getValue()
            );
        });

        // Array Size
        Label sizeLabel = smallLabel("ARRAY SIZE");

        sizeValueLabel = smallLabel("24 Elements");

        sizeSlider = new Slider(10, 40, 24);

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

        speedSlider = new Slider(0.25, 3.0, 1.0);

        speedSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> {
                    speedValueLabel.setText(
                            String.format("%.2fx",
                                    newValue.doubleValue())
                    );
                }
        );

        // Buttons
        Button generateButton = createButton(
                "Generate Array", "#2563EB"
        );

        Button startButton = createButton(
                "Start Sorting", "#16A34A"
        );

        Button pauseButton = createButton(
                "Pause", "#334155"
        );

        Button resumeButton = createButton(
                "Resume", "#334155"
        );

        Button resetButton = createButton(
                "Reset Array", "#475569"
        );

        // Animation controls will be enabled later
        pauseButton.setDisable(true);
        resumeButton.setDisable(true);

        // Button Events
        generateButton.setOnAction(event -> {
            generateArray();
        });

        startButton.setOnAction(event -> {
            statusLabel.setText(
                    algorithmBox.getValue() +
                    " selected. Animation integration pending."
            );
        });

        resetButton.setOnAction(event -> {
            resetArray();
        });

        HBox playbackButtons = new HBox(
                8, pauseButton, resumeButton
        );

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
        visualization.setStyle(panelStyle());
        visualization.setMinWidth(0);

        Label heading = headingLabel(
                "Array Visualization"
        );

        Label description = smallLabel(
                "Each bar represents an array element"
        );

        // Container for array bars
        barsContainer.setAlignment(Pos.BOTTOM_CENTER);
        barsContainer.setPrefHeight(280);
        barsContainer.setMaxHeight(280);

        // Chart area
        chartArea = new StackPane(barsContainer);

        chartArea.setMinHeight(340);
        chartArea.setStyle(
                "-fx-background-color: #0F172A;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 20;"
        );

        VBox.setVgrow(chartArea, Priority.ALWAYS);

        // Color legend
        HBox legend = new HBox(14);

        legend.getChildren().addAll(
                createLegend("#60A5FA", "Normal"),
                createLegend("#FBBF24", "Compare"),
                createLegend("#F87171", "Swap"),
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

        VBox statistics = new VBox(16);

        statistics.setPrefWidth(190);
        statistics.setMinWidth(180);
        statistics.setStyle(panelStyle());

        Label heading = headingLabel("Statistics");

        Label comparisons = smallLabel("Comparisons");
        Label comparisonsValue = valueLabel("0");

        Label swaps = smallLabel("Swaps");
        Label swapsValue = valueLabel("0");

        Label writes = smallLabel("Writes");
        Label writesValue = valueLabel("0");

        Label elements = smallLabel("Elements");
        elementsValueLabel = valueLabel("24");

        statistics.getChildren().addAll(
                heading,
                comparisons,
                comparisonsValue,
                swaps,
                swapsValue,
                writes,
                writesValue,
                elements,
                elementsValueLabel
        );

        return statistics;
    }

    // ==========================================
    // BOTTOM - STATUS BAR
    // ==========================================

    private HBox createStatusBar() {

        statusLabel = new Label("Status: Ready");

        statusLabel.setStyle(
                "-fx-text-fill: #22C55E;" +
                "-fx-font-size: 13px;"
        );

        Label framework = smallLabel("JavaFX Desktop");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBar = new HBox(
                10, statusLabel, spacer, framework
        );

        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle(panelStyle());

        return statusBar;
    }

    // ==========================================
    // GENERATE RANDOM ARRAY
    // ==========================================

    private void generateArray() {

        int size = (int) Math.round(sizeSlider.getValue());

        originalArray = new int[size];

        for (int i = 0; i < size; i++) {
            originalArray[i] =
                    random.nextInt(10, 101);
        }

        displayedArray = originalArray.clone();

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

        if (displayedArray == null) {
            return;
        }

        barsContainer.getChildren().clear();

        double availableWidth =
                chartArea.getWidth() > 100
                ? chartArea.getWidth() - 48
                : 600;

        double gap = 4;

        double barWidth = Math.max(
                5,
                Math.min(
                        22,
                        (availableWidth -
                        gap * (displayedArray.length - 1))
                        / displayedArray.length
                )
        );

        for (int i = 0; i < displayedArray.length; i++) {

            int value = displayedArray[i];

            Rectangle bar = new Rectangle(
                    barWidth,
                    value * 2.6
            );

            bar.setFill(Color.web(BLUE));
            bar.setArcWidth(4);
            bar.setArcHeight(4);

            // Show index and value on mouse hover
            Tooltip tooltip = new Tooltip(
                    "Index: " + i +
                    "\nValue: " + value
            );

            Tooltip.install(bar, tooltip);

            barsContainer.getChildren().add(bar);
        }
    }

    // ==========================================
    // RESET ARRAY
    // ==========================================

    private void resetArray() {

        if (originalArray == null) {
            return;
        }

        displayedArray = originalArray.clone();

        renderBars();

        statusLabel.setText(
                "Status: Array Reset"
        );
    }

    // ==========================================
    // REUSABLE UI HELPER METHODS
    // ==========================================

    private String panelStyle() {
        return "-fx-background-color: " + PANEL + ";" +
               "-fx-background-radius: 14;" +
               "-fx-padding: 18;";
    }

    private Label headingLabel(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    private Label smallLabel(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 12px;"
        );

        return label;
    }

    private Label valueLabel(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    private Button createButton(
            String text, String color) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);

        button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 11;"
        );

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
    // MAIN METHOD
    // ==========================================

    public static void main(String[] args) {
        launch(args);
    }
}
