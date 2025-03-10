package controller;


import view.SnakePanel;
import java.awt.event.KeyEvent;

/**
 * Controls snake game mechanics and user input.
 */
public class SnakeGameController {
    private final SnakePanel snakePanel;
    private final GameState gameState;
    private int direction;
    private boolean isRunning;

    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    public SnakeGameController(SnakePanel snakePanel) {
        this.snakePanel = snakePanel;
        this.gameState = new GameState();
        this.direction = RIGHT;
        this.isRunning = false;
    }

    public void startGame() {
        isRunning = true;
        gameState.reset();
        direction = RIGHT;
        snakePanel.repaint();
    }

    public void pauseGame() {
        isRunning = !isRunning;
        gameState.setPaused(!gameState.isPaused());
    }

    public void handleKeyPress(KeyEvent e) {
        if (!isRunning) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != RIGHT) direction = LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != LEFT) direction = RIGHT;
                break;
            case KeyEvent.VK_UP:
                if (direction != DOWN) direction = UP;
                break;
            case KeyEvent.VK_DOWN:
                if (direction != UP) direction = DOWN;
                break;
        }
    }

    public void update() {
        if (!isRunning || gameState.isPaused() || gameState.isGameOver()) {
            return;
        }

        // Update snake position based on direction
        updateSnakePosition();
        
        // Check collisions
        if (checkCollision()) {
            gameOver();
        }

        snakePanel.repaint();
    }

    private void updateSnakePosition() {
        // Implementation of snake movement logic
    }

    private boolean checkCollision() {
        // Implementation of collision detection
        return false;
    }

    private void gameOver() {
        isRunning = false;
        gameState.setGameOver(true);
    }

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
