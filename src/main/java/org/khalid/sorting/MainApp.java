
package org.khalid.sorting;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("Sorting Algorithm Visualizer");

        StackPane root = new StackPane();
        root.getChildren().add(title);

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Sorting Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
