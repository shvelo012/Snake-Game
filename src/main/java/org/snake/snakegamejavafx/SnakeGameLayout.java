package org.snake.snakegamejavafx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SnakeGameLayout extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML layout
        Parent root = FXMLLoader.load(getClass().getResource("snake-game-layout.fxml"));

        // Set the FXML layout as the root of the scene
        Scene scene = new Scene(root);

        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void onGameStart(ActionEvent actionEvent) {
        // Start the SnakeGame
        SnakeGame snakeGame = new SnakeGame();
        Stage gameStage = new Stage();
        try {
            snakeGame.start(gameStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
