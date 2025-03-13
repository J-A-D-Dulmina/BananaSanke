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

        initializeSnake();
        food = new Food(panelWidth, panelHeight, tileSize);
        
        SwingUtilities.invokeLater(() -> {
            if (APISection.getInstance() != null) {
                Game currentGame = APISection.getInstance().getCurrentGame();
                if (currentGame != null) {
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

        if (newHead.x < 0) newHead.x = panelWidth - tileSize;
        if (newHead.x >= panelWidth) newHead.x = 0;
        if (newHead.y < 0) newHead.y = panelHeight - tileSize;
        if (newHead.y >= panelHeight) newHead.y = 0;

        if (snake.contains(newHead)) {
            running = false;
            SoundManager.getInstance().stopRunningSound();
            showGameOver();
            return;
        }

        boolean ateFirstFood = newHead.equals(food.getFirstFoodPosition());
        boolean ateSecondFood = newHead.equals(food.getSecondFoodPosition());

        if (ateFirstFood || ateSecondFood) {
            int correctAnswer = APISection.getInstance().getCorrectAnswer();
            boolean correctAnswerEaten = (ateFirstFood && food.getFirstFoodNumber() == correctAnswer) ||
                                       (ateSecondFood && food.getSecondFoodNumber() == correctAnswer);
            
            if (correctAnswerEaten) {
                SoundManager.getInstance().playEatSound();
                APISection.getInstance().loadNextQuestion();
                Game newGame = APISection.getInstance().getCurrentGame();
                if (newGame != null) {
                    food.setCurrentGame(newGame);
                }
                score++;
                if (APISection.getInstance() != null) {
                    APISection.getInstance().updateScore(score);
                }
                
                BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                if (parent != null) {
                    parent.getTimerPanel().reset();
                    parent.getTimerPanel().start();
                }
            } else {
                SoundManager.getInstance().playWrongFoodSound();
                if (APISection.getInstance() != null) {
                    BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                    boolean stillAlive = APISection.getInstance().reduceHealth();
                    
                    if (!stillAlive) {
                        running = false;
                        SoundManager.getInstance().stopRunningSound();
                        showGameOver();
                        return;
                    }
                }
            }
            
            food.spawnFood(snake);
        } else {
            snake.remove(snake.size() - 1);
        }

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
        SoundManager.getInstance().stopRunningSound();
        direction = "RIGHT";  
        score = 0;
        waitingForAnswer = false;
        running = true;

        initializeSnake();

        if (APISection.getInstance() != null) {
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
            if (parent != null) {
                parent.getHealthPanel().resetHealth();
                parent.getTimerPanel().reset();
                parent.getTimerPanel().start();
            }
            
            APISection.getInstance().updateScore(0);
            APISection.getInstance().loadNextQuestion();
            Game newGame = APISection.getInstance().getCurrentGame();
            if (newGame != null) {
                food.setCurrentGame(newGame);
            }
        }

        food.spawnFood(snake);

        if (!SoundManager.getInstance().isMuted()) {
            SoundManager.getInstance().startRunningSound();
        }

        SwingUtilities.invokeLater(() -> {
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
            if (parent != null) {
                parent.repaint();
            }
        });
    }

    public boolean isWaitingForAnswer() {
        return waitingForAnswer;
    }

    public void handleTimeUp() {
        if (running) {
            running = false;
            SoundManager.getInstance().stopRunningSound();
            showGameOver();
        }
    }

    private void showGameOver() {
        try {
            SoundManager.getInstance().stopRunningSound();
            String response = APIClient.getInstance().updateHighScore(score);
            
            SwingUtilities.invokeLater(() -> {
                BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                if (parent != null) {
                    parent.getTimerPanel().stop();
                    parent.showGameOver(score);
                }
            });
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                if (parent != null) {
                    parent.getTimerPanel().stop();
                    parent.showGameOver(score);
                }
            });
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

    public void pauseGame() {
        if (running) {
            running = false;
            SoundManager.getInstance().stopRunningSound();
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
            if (parent != null) {
                parent.getTimerPanel().stop();
            }
        }
    }

    public void resumeGame() {
        if (!running) {
            running = true;
            if (!SoundManager.getInstance().isMuted()) {
                SoundManager.getInstance().startRunningSound();
            }
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
            if (parent != null) {
                parent.getTimerPanel().start();
            }
        }
    }

    public boolean isPaused() {
        return !running;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public Point getStartingPosition() {
        return new Point(STARTING_POSITION);
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Food getFood() {
        return food;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setWaitingForAnswer(boolean waiting) {
        this.waitingForAnswer = waiting;
    }

    public void setDirection(String direction) {
        if (!isOppositeDirection(direction)) {
            this.direction = direction;
        }
    }
}