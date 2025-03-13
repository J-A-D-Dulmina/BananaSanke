package model;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import view.APISection;
import view.BananaPanel;
import view.GameMainInterface;
import view.GameOverPanel;
import javax.swing.SwingUtilities;
import api.APIClient;

public class SnakeGameLogic {
    private final int tileSize;
    private final int panelWidth;
    private final int panelHeight;
    private final List<Point> snake;
    private Food food;
    private String direction;
    private boolean running;
    private int score;
    private boolean waitingForAnswer;
    private static final Point STARTING_POSITION = new Point(240, 240); // Center position

    public SnakeGameLogic(int panelWidth, int panelHeight, int tileSize) {
        this.tileSize = tileSize;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.snake = new ArrayList<>();
        this.direction = "RIGHT";
        this.running = true;
        this.score = 0;
        this.waitingForAnswer = false;

        // Initialize snake at the center with 3 segments
        initializeSnake();

        food = new Food(panelWidth, panelHeight, tileSize);
        
        // Initialize API and food with a small delay to ensure proper setup
        SwingUtilities.invokeLater(() -> {
            if (APISection.getInstance() != null) {
                Game currentGame = APISection.getInstance().getCurrentGame();
                if (currentGame != null) {
                    System.out.println("Initial API Response - URL: " + currentGame.getLocation());
                    System.out.println("Initial API Response - Answer: " + currentGame.getSolution());
                    food.setCurrentGame(currentGame);
                    food.spawnFood(snake);
                }
            }
        });
    }

    private void initializeSnake() {
        snake.clear();
        // Start at the center position
        snake.add(new Point(STARTING_POSITION.x, STARTING_POSITION.y));
        snake.add(new Point(STARTING_POSITION.x - tileSize, STARTING_POSITION.y));
        snake.add(new Point(STARTING_POSITION.x - (2 * tileSize), STARTING_POSITION.y));
    }

    public void updateGame() {
        if (!running) return;

        Point head = snake.get(0);
        Point newHead = new Point(head);

        // Move the snake in the current direction
        switch (direction) {
            case "UP":
                newHead.y -= tileSize;
                break;
            case "DOWN":
                newHead.y += tileSize;
                break;
            case "LEFT":
                newHead.x -= tileSize;
                break;
            case "RIGHT":
                newHead.x += tileSize;
                break;
        }

        // Wrap-around screen logic
        if (newHead.x < 0) newHead.x = panelWidth - tileSize;
        if (newHead.x >= panelWidth) newHead.x = 0;
        if (newHead.y < 0) newHead.y = panelHeight - tileSize;
        if (newHead.y >= panelHeight) newHead.y = 0;

        // Check for self collision
        if (snake.contains(newHead)) {
            System.out.println("Snake hit itself! Game Over!");
            running = false;
            SoundManager.getInstance().stopRunningSound(); // Stop running sound immediately
            showGameOver();
            return;
        }

        // Check if the snake eats either of the two food items
        boolean ateFirstFood = newHead.equals(food.getFirstFoodPosition());
        boolean ateSecondFood = newHead.equals(food.getSecondFoodPosition());

        if (ateFirstFood || ateSecondFood) {
            System.out.println("Snake ate food! Checking if it's the correct answer...");
            // Get the current API answer
            int correctAnswer = APISection.getInstance().getCorrectAnswer();
            System.out.println("Correct answer from API: " + correctAnswer);
            System.out.println("First food number: " + food.getFirstFoodNumber());
            System.out.println("Second food number: " + food.getSecondFoodNumber());
            
            // Check if we ate the food with the correct answer
            boolean correctAnswerEaten = (ateFirstFood && food.getFirstFoodNumber() == correctAnswer) ||
                                       (ateSecondFood && food.getSecondFoodNumber() == correctAnswer);
            
            if (correctAnswerEaten) {
                // Play eat sound for correct answer
                SoundManager.getInstance().playEatSound();
                
                System.out.println("Ate food with correct answer!");
                // Load next question from APISection
                APISection.getInstance().loadNextQuestion();
                Game newGame = APISection.getInstance().getCurrentGame();
                if (newGame != null) {
                    System.out.println("API Response: " + newGame.getLocation() + ", Answer: " + newGame.getSolution());
                }
                food.setCurrentGame(newGame);
                score++;
                System.out.println("Score increased! Current score: " + score);
                // Update score in UI
                if (APISection.getInstance() != null) {
                    APISection.getInstance().updateScore(score);
                }
                
                // Reset timer for next question
                BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                if (parent != null) {
                    parent.getTimerPanel().reset();
                    parent.getTimerPanel().start();
                }
            } else {
                // Play wrong food sound
                SoundManager.getInstance().playWrongFoodSound();
                
                System.out.println("Ate food with wrong answer!");
                // Reduce health when eating wrong answer
                if (APISection.getInstance() != null) {
                    BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                    boolean stillAlive = APISection.getInstance().reduceHealth();
                    
                    if (!stillAlive) {
                        running = false;
                        System.out.println("Game Over - No more attempts remaining!");
                        SoundManager.getInstance().stopRunningSound(); // Stop running sound immediately
                        showGameOver();
                        return;
                    } else if (parent != null && parent.getHealthPanel().isLastAttempt()) {
                        System.out.println("WARNING: This is your final attempt! Next wrong answer will end the game!");
                    }
                }
            }
            
            // Spawn new food regardless of whether the answer was correct
            food.spawnFood(snake);
        } else {
            // Remove tail only if food wasn't eaten
            snake.remove(snake.size() - 1);
        }

        // Add the new head position
        snake.add(0, newHead);
    }

    public int getScore() {
        return score;
    }

    public boolean isRunning() {
        return running;
    }

    public List<Point> getFoodPositions() {
        List<Point> foodPositions = new ArrayList<>();
        foodPositions.add(food.getFirstFoodPosition());
        foodPositions.add(food.getSecondFoodPosition());
        return foodPositions;
    }

    public int getFirstFoodNumber() {
        return food.getFirstFoodNumber();
    }

    public int getSecondFoodNumber() {
        return food.getSecondFoodNumber();
    }

    public List<Point> getSnakePositions() {
        return new ArrayList<>(snake);
    }
    
    public void changeDirection(String newDirection) {
        if (!isOppositeDirection(newDirection)) {
            direction = newDirection;
        }
    }

    private boolean isOppositeDirection(String newDirection) {
        return (direction.equals("LEFT") && newDirection.equals("RIGHT")) ||
               (direction.equals("RIGHT") && newDirection.equals("LEFT")) ||
               (direction.equals("UP") && newDirection.equals("DOWN")) ||
               (direction.equals("DOWN") && newDirection.equals("UP"));
    }

    public String getDirection() {
        return direction;
    }
    
    public void reset() {
        System.out.println("Resetting game to starting state...");

        // Stop any running sounds
        SoundManager.getInstance().stopRunningSound();

        // Reset all game variables
        direction = "RIGHT";  
        score = 0;
        waitingForAnswer = false;
        running = true;  // Set game to running state

        // Reset snake to center starting position
        initializeSnake();

        // Reset health and score in UI
        if (APISection.getInstance() != null) {
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
            if (parent != null) {
                // Reset health
                parent.getHealthPanel().resetHealth();
                // Reset and start timer
                parent.getTimerPanel().reset();
                parent.getTimerPanel().start();
            }
            
            // Reset score display
            APISection.getInstance().updateScore(0);
            
            // Load new question
            APISection.getInstance().loadNextQuestion();
            Game newGame = APISection.getInstance().getCurrentGame();
            if (newGame != null) {
                System.out.println("New question loaded - Answer: " + newGame.getSolution());
                food.setCurrentGame(newGame);
            }
        }

        // Reset food positions
        food.spawnFood(snake);

        // Start running sound
        if (!SoundManager.getInstance().isMuted()) {
            SoundManager.getInstance().startRunningSound();
        }

        // Notify any listeners that game state has been reset
        SwingUtilities.invokeLater(() -> {
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
            if (parent != null) {
                parent.repaint();
            }
        });

        System.out.println("Game reset complete - Score: 0, Direction: RIGHT, Health: Full");
    }

    public boolean isWaitingForAnswer() {
        return waitingForAnswer;
    }

    private void showGameOver() {
        try {
            System.out.println("Game Over! Final score: " + score);
            
            // Stop running sound
            SoundManager.getInstance().stopRunningSound();
            
            // Send final score to server
            String response = APIClient.getInstance().updateHighScore(score);
            System.out.println("Server response for score update: " + response);
            
            // Show game over panel
            SwingUtilities.invokeLater(() -> {
                try {
                    BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                    if (parent != null) {
                        parent.getTimerPanel().stop();
                        parent.showGameOver(score);
                    } else {
                        System.err.println("Error: Could not find BananaPanel");
                        throw new RuntimeException("Could not find BananaPanel");
                    }
                } catch (Exception e) {
                    System.err.println("Error showing game over screen: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error in showGameOver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setRunning(boolean running) {
        if (this.running && !running) {
            // If we're stopping the game, stop the running sound
            SoundManager.getInstance().stopRunningSound();
        }
        this.running = running;
        // Update background music state and running sound
        if (APISection.getInstance() != null) {
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
            if (parent != null) {
                Component comp = parent;
                while (comp != null && !(comp instanceof GameMainInterface)) {
                    comp = comp.getParent();
                }
                if (comp instanceof GameMainInterface) {
                    ((GameMainInterface) comp).handleGameStateChange(running);
                    // Handle running sound
                    if (running) {
                        SoundManager.getInstance().startRunningSound();
                        // Start the timer
                        parent.getTimerPanel().start();
                    } else {
                        // Stop the timer
                        parent.getTimerPanel().stop();
                    }
                }
            }
        }
    }

    public void handleTimeUp() {
        if (running) {
            running = false;
            System.out.println("Time's up! Game Over!");
            SoundManager.getInstance().stopRunningSound();
            showGameOver();
        }
    }
}
