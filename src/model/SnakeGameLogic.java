package model;

import interfaces.IGameLogic;
import interfaces.ISoundManager;
import interfaces.ISessionManager;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import view.APISection;
import view.BananaPanel;
import view.GameMainInterface;
import javax.swing.SwingUtilities;

public class SnakeGameLogic implements IGameLogic {
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
    private final ISoundManager soundManager;
    private final ISessionManager sessionManager;

    public SnakeGameLogic(int panelWidth, int panelHeight, int tileSize) {
        this.tileSize = tileSize;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.snake = new ArrayList<>();
        this.direction = "RIGHT";
        this.running = true;
        this.score = 0;
        this.waitingForAnswer = false;
        this.soundManager = SoundManager.getInstance();
        this.sessionManager = SessionManagerImpl.getInstance();

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

    @Override
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
            soundManager.stopRunningSound();
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
                soundManager.playEatSound();
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
                soundManager.playWrongFoodSound();
                if (APISection.getInstance() != null) {
                    boolean stillAlive = APISection.getInstance().reduceHealth();
                    
                    if (!stillAlive) {
                        running = false;
                        soundManager.stopRunningSound();
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

    @Override
    public int getScore() {
        return score;
    }

    @Override
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

    @Override
    public List<Point> getSnakePositions() {
        return new ArrayList<>(snake);
    }
    
    @Override
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

    @Override
    public String getDirection() {
        return direction;
    }
    
    @Override
    public void reset() {
        // Stop sound effects
        soundManager.stopRunningSound();
        
        // Reset basic game state
        direction = "RIGHT";  
        score = 0;
        waitingForAnswer = false;
        running = false; // Set to false initially to ensure proper start state

        // Reset snake position
        initializeSnake();

        // Handle API section reset while maintaining session
        if (APISection.getInstance() != null) {
            try {
                BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                if (parent != null) {
                    // Reset health and timer
                    parent.getHealthPanel().resetHealth();
                    parent.getTimerPanel().reset();
                    parent.getTimerPanel().stop(); // Ensure timer is stopped
                }
                
                // Reset API state while maintaining session
                APISection.getInstance().updateScore(0);
                
                // Load new question only if we have valid session
                if (sessionManager.getAuthToken() != null && !sessionManager.getAuthToken().isEmpty()) {
                    APISection.getInstance().loadNextQuestion();
                    Game newGame = APISection.getInstance().getCurrentGame();
                    if (newGame != null) {
                        food.setCurrentGame(newGame);
                        food.spawnFood(snake);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error resetting API state: " + e.getMessage());
            }
        }

        // Stop all sounds and ensure they're in initial state
        soundManager.stopAll();

        // Refresh UI
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
            soundManager.stopRunningSound();
            showGameOver();
        }
    }

    private void showGameOver() {
        try {
            soundManager.stopRunningSound();
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

    @Override
    public void setRunning(boolean running) {
        if (this.running && !running) {
            // If we're stopping the game, stop the running sound
            soundManager.stopRunningSound();
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
                        soundManager.startRunningSound();
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
            soundManager.stopRunningSound();
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
            if (parent != null) {
                parent.getTimerPanel().stop();
            }
        }
    }

    public void resumeGame() {
        if (!running) {
            running = true;
            if (!soundManager.isMuted()) {
                soundManager.startRunningSound();
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

    @Override
    public int getTileSize() {
        return tileSize;
    }

    @Override
    public int getPanelWidth() {
        return panelWidth;
    }

    @Override
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