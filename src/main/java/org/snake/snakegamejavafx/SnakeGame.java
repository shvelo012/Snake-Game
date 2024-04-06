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

    private static final int SCREEN_WIDTH = 700;
    private static final int SCREEN_HEIGHT = 700;
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

    private int score = 0;

    private GraphicsContext gc;


    public enum Difficulty {
        EASY(100_000_000), MEDIUM(50_000_000), HARD(35_000_000);

        private final long delay;

        Difficulty(long delay) {
            this.delay = delay;
        }

        public long getDelay() {
            return delay;
        }
    }


    private Difficulty difficulty = Difficulty.MEDIUM; // Default difficulty


    public Canvas getCanvas() {
        return canvas;
    }


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
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= difficulty.getDelay()) {
                    lastUpdate = now;
                    if (!gameOver) {
                        move();
                        checkAppleCollision();
                        draw();
                    }
                }
            }
        }.start();


        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame() {
        for (int i = 0; i < snakesLength; i++) {
            snakeX[i] = 0;
            snakeY[i] = 0;
        }
        snakesLength = 6;
        direction = 'R';
        createApple();
        gameOver = false;
        score = 0;
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
        } else if (code == KeyCode.R && gameOver ) {
            startGame();
        }
    }

    private void move() {
        for (int i = snakesLength - 1; i >= 0; i--) {
            snakeX[i + 1] = snakeX[i];
            snakeY[i + 1] = snakeY[i];
        }

        switch (direction) {
            case 'R':
                snakeX[0] = (snakeX[0] + UNIT_SIZE) % SCREEN_WIDTH;
                break;
            case 'L':
                snakeX[0] = (snakeX[0] - UNIT_SIZE + SCREEN_WIDTH) % SCREEN_WIDTH;
                break;
            case 'U':
                snakeY[0] = (snakeY[0] - UNIT_SIZE + SCREEN_HEIGHT) % SCREEN_HEIGHT;
                break;
            case 'D':
                snakeY[0] = (snakeY[0] + UNIT_SIZE) % SCREEN_HEIGHT;
                break;

        }
        gameOver = checkCollision();
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
            score += 10;
            createApple();
        }
    }

    private void draw() {
        gc.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        // Draw apple
        gc.setFill(Color.RED);
        gc.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        // Draw score
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, 10, 20);

        // Draw snake
        gc.setFill(Color.GREEN);
        for (int i = 0; i < snakesLength; i++) {
            gc.fillRect((int) Math.round(snakeX[i]), (int) Math.round(snakeY[i]), UNIT_SIZE, UNIT_SIZE);
        }

        // If game is over, display game over text
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("Game Over! Final Score: " + score, SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2);
        }
    }

}