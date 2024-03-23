package org.snake.snakegamejavafx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SnakeGame extends Application {

    private static final int SCREEN_WIDTH = 400;
    private static final int SCREEN_HEIGHT = 400;
    private static final int UNIT_SIZE = 10;
    private static final int GAME_UNITS = SCREEN_WIDTH / UNIT_SIZE;
    private final int[] snakeX = new int[GAME_UNITS];
    private final int[] snakeY = new int[GAME_UNITS];
    private int snakesLength = 6;
    private int appleX = 0;
    private int appleY = 0;
    private char direction = 'R';
    private boolean gameOver = false;
    private Canvas canvas;
    private GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        startGame();

        Group root = new Group(canvas);
        Scene scene = new Scene(root);

        // Handle key presses for direction change
        scene.setOnKeyPressed(event -> {
            changeDirection(event.getCode());
        });

        // Start the game loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    move();
                    checkAppleCollision();
                    draw();
                }
            }
        }.start();

        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame() {
        snakeX[0] = 0;
        snakeY[0] = 0;
        snakesLength = 6;
        direction = 'R';
        createApple();
        System.out.println("saba");
    }

    private void createApple() {
        appleX = (int) (Math.random() * GAME_UNITS) * UNIT_SIZE;
        appleY = (int) (Math.random() * GAME_UNITS) * UNIT_SIZE;
    }

    private void changeDirection(KeyCode code) {
        if (code == KeyCode.RIGHT && direction != 'L') {
            direction = 'R';
        } else if (code == KeyCode.LEFT && direction != 'R') {
            direction = 'L';
        } else if (code == KeyCode.UP && direction != 'D') {
            direction = 'U';
        } else if (code == KeyCode.DOWN && direction != 'U') {
            direction = 'D';
        }
    }

    private void move() {
        for (int i = snakesLength - 1; i >= 0; i--) {
            snakeX[i + 1] = snakeX[i];
            snakeY[i + 1] = snakeY[i];
        }

        switch (direction) {
            case 'R':
                snakeX[0] = snakeX[0] + UNIT_SIZE;
                break;
            case 'L':
                snakeX[0] = snakeX[0] - UNIT_SIZE;
                break;
            case 'U':
                snakeY[0] = snakeY[0] - UNIT_SIZE;
                break;
            case 'D':
                snakeY[0] = snakeY[0] + UNIT_SIZE;
                break;
        }

        // Check for collisions with walls and itself
        gameOver = snakeX[0] < 0 || snakeX[0] >= SCREEN_WIDTH || snakeY[0] < 0 || snakeY[0] >= SCREEN_HEIGHT || checkCollision();
    }

    private boolean checkCollision() {
        for (int i = 1; i < snakesLength; i++) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                return true;
            }
        }
        return false;
    }

    private void checkAppleCollision() {
        if (snakeX[0] == appleX && snakeY[0] == appleY) {
            snakesLength++;
            createApple();
        }
    }

    private void draw() {
        gc.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        // Draw apple
        gc.setFill(Color.RED);
        gc.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        // Draw snake
        gc.setFill(Color.GREEN);
        for (int i = 0; i < snakesLength; i++) {
            gc.fillRect(snakeX[i], snakeY[i], UNIT_SIZE, UNIT_SIZE);
        }

        // If game is over, display game over text
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("Game Over!", SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}