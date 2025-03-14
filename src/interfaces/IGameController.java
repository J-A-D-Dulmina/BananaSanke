package interfaces;

import java.awt.event.KeyEvent;

/**
 * Interface defining the game controller operations.
 */
public interface IGameController {
    /**
     * Starts or restarts the game.
     */
    void startGame();

    /**
     * Stops the game.
     */
    void stopGame();

    /**
     * Toggles the pause state of the game.
     */
    void pauseGame();

    /**
     * Handles keyboard input for snake direction.
     * @param e The key event to handle.
     */
    void handleKeyPress(KeyEvent e);

    /**
     * Resets the game to initial state.
     */
    void resetGame();

    /**
     * Checks if the game is currently paused.
     * @return True if game is paused, false otherwise.
     */
    boolean isPaused();

    /**
     * Checks if the game is over.
     * @return True if game is over, false otherwise.
     */
    boolean isGameOver();

    /**
     * Gets the current score.
     * @return The current score.
     */
    int getScore();
} 