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

    Stage window;

    private static final int SCREEN_WIDTH = 700;
    private static final int SCREEN_HEIGHT = 700;
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE); // Adjust the maximum possible units
    private final int[] snakeX = new int[GAME_UNITS];
    private final int[] snakeY = new int[GAME_UNITS];
    private int snakesLength = 6;
    private int appleX = 0;
    private int appleY = 0;
    private int specialAppleX = -1;
    private int specialAppleY = -1;
    private char direction = 'R';
    private boolean gameOver = false;
    private Canvas canvas;

    private int score = 0;

    private GraphicsContext gc;

    private int appleCounter = 0;

    public enum Difficulty {
        EASY(100_000_000), MEDIUM(50_000_000), HARD(30_000_000);

        private final long delay;

        Difficulty(long delay) {
            this.delay = delay;
        }

        public long getDelay() {
            return delay;
        }
    }

    private Difficulty difficulty;

    public SnakeGame() {
        this.difficulty = Difficulty.MEDIUM;
    }

    public SnakeGame(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println(difficulty);
        window = primaryStage;
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

        window.setTitle("Snake Game");
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest(event -> {
            SnakeGameLayout.returnToMainMenu();
            event.consume(); // Prevent the default close action
        });

        draw(); // Draw the initial state of the game
    }

    private void startGame() {
        // Generate random positions for the snake's head within bounds
        int headX = (int) (Math.random() * (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        int headY = (int) (Math.random() * (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

        // Initialize the snake segments to follow the head
        for (int i = 0; i < snakesLength; i++) {
            snakeX[i] = headX - i * UNIT_SIZE;
            snakeY[i] = headY;
        }

        snakesLength = 6;
        direction = 'R';
        appleCounter = 0; // Reset apple counter
        createApple();
        gameOver = false;
        score = 0;
    }

    private void createApple() {
        if (appleCounter > 0 && appleCounter % 5 == 0) {
            // Create a special apple every fifth apple
            specialAppleX = (int) (Math.random() * (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            specialAppleY = (int) (Math.random() * (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } else {
            // Create a regular apple
            appleX = (int) (Math.random() * (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = (int) (Math.random() * (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            specialAppleX = -1; // No special apple
            specialAppleY = -1; // No special apple
        }
        appleCounter++;
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
        } else if (code == KeyCode.R && gameOver) {
            startGame();
        }
    }

    private void move() {
        for (int i = snakesLength - 1; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
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
        if (specialAppleX != -1 && specialAppleY != -1) {
            // Adjust collision detection to account for the 3x3 size
            for (int x = specialAppleX; x < specialAppleX + UNIT_SIZE * 3; x += UNIT_SIZE) {
                for (int y = specialAppleY; y < specialAppleY + UNIT_SIZE * 3; y += UNIT_SIZE) {
                    if (snakeX[0] == x && snakeY[0] == y) {
                        snakesLength += 5;
                        score += 50;
                        createApple();
                        specialAppleX = -1;
                        specialAppleY = -1;
                        return;
                    }
                }
            }
        }
    }

    private void draw() {
        gc.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (specialAppleX == -1 && specialAppleY == -1) {
            // Draw apple
            gc.setFill(Color.RED);
            gc.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        }

        if (specialAppleX != -1 && specialAppleY != -1) {
            gc.setFill(Color.BLUE);
            gc.fillOval(specialAppleX, specialAppleY, UNIT_SIZE * 3, UNIT_SIZE * 3);
        }

        // Draw score
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, 10, 20);

        // Draw snake
        gc.setFill(Color.GREEN);
        for (int i = 0; i < snakesLength; i++) {
            gc.fillRect(snakeX[i], snakeY[i], UNIT_SIZE, UNIT_SIZE);
        }

        // If game is over, display game over text
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("Game Over! Final Score: " + score, SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2);
            gc.fillText("Press 'R' for restart the game", SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2 + 20);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
