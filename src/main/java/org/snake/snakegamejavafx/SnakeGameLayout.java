package org.snake.snakegamejavafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;


public class SnakeGameLayout extends Application {

    private static Stage primaryStage;  // Declare primaryStage as a static variable
    private static SnakeGame.Difficulty selectedDifficulty = SnakeGame.Difficulty.MEDIUM; // Default difficulty

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;  // Initialize the static variable

        // Load the FXML layout
        Parent root = FXMLLoader.load(getClass().getResource("snake-game-layout.fxml"));

        // Set the FXML layout as the root of the scene
        Scene mainMenu = new Scene(root);

        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(mainMenu);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    @FXML
    public void onGameStart(ActionEvent actionEvent) {
        // Start the SnakeGame
//        SnakeGame snakeGame = new SnakeGame();
        SnakeGame snakeGame = new SnakeGame(selectedDifficulty); // Pass selected difficulty

        try {
            snakeGame.start(primaryStage); // Use the static variable
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setEasyDifficulty(ActionEvent actionEvent) {
        selectedDifficulty = SnakeGame.Difficulty.EASY;
    }
    @FXML
    public void setMediumDifficulty(ActionEvent actionEvent) {
        selectedDifficulty = SnakeGame.Difficulty.MEDIUM;
    }
    @FXML
    public void setHardDifficulty(ActionEvent actionEvent) {
        selectedDifficulty = SnakeGame.Difficulty.HARD;
    }

public static void returnToMainMenu() {
    try {
        Parent root = FXMLLoader.load(SnakeGameLayout.class.getResource("snake-game-layout.fxml"));
        Scene mainMenu = new Scene(root);
        primaryStage.setScene(mainMenu);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
        });
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
