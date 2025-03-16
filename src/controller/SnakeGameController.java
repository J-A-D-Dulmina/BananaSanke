package controller;

import interfaces.IGameController;
import interfaces.ISnakeGameController;
import model.SnakeGameLogic;
import model.HighScoreNotifier;
import view.SnakePanel;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.util.List;

/**
 * Controls snake game mechanics, user input, and game flow.
 */
public class SnakeGameController implements IGameController, ISnakeGameController, ActionListener {
    private final SnakePanel snakePanel;
    private final SnakeGameLogic gameLogic;
    private final GameState gameState;
    private Timer gameTimer;
    private int direction;
    private HighScoreNotifier highScoreNotifier;
    
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    public SnakeGameController(SnakePanel snakePanel, SnakeGameLogic gameLogic) {
        this.snakePanel = snakePanel;
        this.gameLogic = gameLogic;
        this.gameState = new GameState();
        this.direction = RIGHT;
        this.gameTimer = new Timer(100, this); // Timer triggers every 100ms
        this.highScoreNotifier = HighScoreNotifier.getInstance();
    }

    /**
     * Starts or restarts the game
     */
    @Override
    public void startGame() {
        gameTimer.stop(); 
        gameState.reset();
        gameLogic.reset();
        direction = RIGHT;
        gameState.setPaused(false);
        gameLogic.setRunning(true);
        gameLogic.updateGame();
        gameTimer.start();
        snakePanel.repaint();
    }

    /**
     * Stops the game timer
     */
    @Override
    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameLogic.setRunning(false);
        gameState.setPaused(true);
    }

    /**
     * Toggles the pause state of the game
     */
    @Override
    public void pauseGame() {
        if (gameState.isGameOver()) return;
        
        boolean newPauseState = !gameState.isPaused();
        gameState.setPaused(newPauseState);
        gameLogic.setRunning(!newPauseState);
        
        if (newPauseState) {
            gameTimer.stop();
        } else {
            gameTimer.start();
        }
    }

    /**
     * Handles keyboard input for snake direction
     */
    @Override
    public void handleKeyPress(KeyEvent e) {
        if (gameState.isGameOver() || gameState.isPaused()) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != RIGHT) {
                    direction = LEFT;
                    gameLogic.changeDirection("LEFT");
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != LEFT) {
                    direction = RIGHT;
                    gameLogic.changeDirection("RIGHT");
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != DOWN) {
                    direction = UP;
                    gameLogic.changeDirection("UP");
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != UP) {
                    direction = DOWN;
                    gameLogic.changeDirection("DOWN");
                }
                break;
        }
    }

    /**
     * Timer event handler for game updates
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameState.isPaused() && !gameState.isGameOver()) {
            gameLogic.updateGame();
            
            // Check for game over conditions
            if (checkCollision()) {
                gameOver();
            }
            
            snakePanel.repaint();
        }
    }

    /**
     * Resets the game to initial state
     */
    @Override
    public void resetGame() {
        System.out.println("Game resetting...");
        stopGame();
        gameLogic.reset();
        gameState.reset();
        direction = RIGHT;
        
        // Don't set running to true here, let the SnakePanel handle it
        // gameLogic.setRunning(true);

        SwingUtilities.invokeLater(() -> {
            snakePanel.repaint();
            // Don't call startGame() here, let the SnakePanel handle it
            // startGame();
        });
    }

    private boolean checkCollision() {
        List<Point> snake = gameLogic.getSnakePositions();
        if (snake.isEmpty()) return false;

        Point head = snake.get(0);
        
        // Check wall collision
        if (head.x < 0 || head.x >= gameLogic.getPanelWidth() ||
            head.y < 0 || head.y >= gameLogic.getPanelHeight()) {
            return true;
        }

        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                return true;
            }
        }

        return false;
    }

    private void gameOver() {
        stopGame();
        gameState.setGameOver(true);
        gameLogic.setRunning(false);
        
        // Get the final score
        final int finalScore = gameState.getScore();
        
        // Check if high score on a background thread
        if (finalScore > 0) {
            SwingUtilities.invokeLater(() -> {
                saveHighScore(finalScore);
            });
        }
    }

    // Getters for game state
    @Override
    public boolean isGameOver() {
        return gameState.isGameOver();
    }

    @Override
    public boolean isPaused() {
        return gameState.isPaused();
    }

    @Override
    public int getScore() {
        return gameState.getScore();
    }

    /**
     * Checks if the given score is a high score
     */
    @Override
    public boolean isHighScore(int score) {
        return score > 0 && (score > highScoreNotifier.getCurrentHighScore() || 
                highScoreNotifier.getCurrentHighScore() == 0);
    }

    /**
     * Saves a high score and notifies listeners
     */
    @Override
    public boolean saveHighScore(int score) {
        if (isHighScore(score)) {
            return highScoreNotifier.checkAndNotifyHighScore(score);
        }
        return false;
    }
    
    /**
     * Toggles game pause state
     */
    @Override
    public void togglePause() {
        if (gameState.isPaused()) {
            // Resume game
            gameState.setPaused(false);
            gameLogic.setRunning(true);
            gameTimer.start();
            snakePanel.hidePauseOverlay();
        } else {
            // Pause game
            gameState.setPaused(true);
            gameLogic.setRunning(false);
            gameTimer.stop();
            snakePanel.showPauseOverlay();
        }
    }
}
