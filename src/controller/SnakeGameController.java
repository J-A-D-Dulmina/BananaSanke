package controller;

import model.SnakeGameLogic;
import view.SnakePanel;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

/**
 * Controls snake game mechanics, user input, and game flow.
 */
public class SnakeGameController implements ActionListener {
    private final SnakePanel snakePanel;
    private final SnakeGameLogic gameLogic;
    private final GameState gameState;
    private Timer gameTimer;
    private int direction;
    
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
    }

    /**
     * Starts or restarts the game
     */
    public void startGame() {
        gameTimer.stop(); // Stop any existing timer
        gameState.reset();
        direction = RIGHT;
        gameState.setPaused(false);
        gameLogic.updateGame();
        gameTimer.start();
        snakePanel.repaint();
    }

    /**
     * Stops the game timer
     */
    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    /**
     * Toggles the pause state of the game
     */
    public void pauseGame() {
        if (gameState.isGameOver()) return;
        
        gameState.setPaused(!gameState.isPaused());
        if (gameState.isPaused()) {
            gameTimer.stop();
        } else {
            gameTimer.start();
        }
    }

    /**
     * Handles keyboard input for snake direction
     */
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
    public void resetGame() {
        System.out.println("Game resetting...");
        stopGame();
        gameLogic.reset();
        gameState.reset();
        direction = RIGHT;

        SwingUtilities.invokeLater(() -> {
            snakePanel.repaint();
            startGame();
        });
        System.out.println("Game restarted");
    }

    private boolean checkCollision() {
        // Implement collision detection logic here
        // This should check wall collisions and self-collisions
        return false; // Placeholder return
    }

    private void gameOver() {
        stopGame();
        gameState.setGameOver(true);
    }

    // Getters for game state
    public boolean isGameOver() {
        return gameState.isGameOver();
    }

    public boolean isPaused() {
        return gameState.isPaused();
    }

    public int getScore() {
        return gameState.getScore();
    }
}
