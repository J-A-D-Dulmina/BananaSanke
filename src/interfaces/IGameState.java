package interfaces;

/**
 * Interface for managing game state including score, pause state, and game over state.
 */
public interface IGameState {
    /**
     * Resets the game state to initial values.
     */
    void reset();

    /**
     * Increments the game score.
     */
    void incrementScore();

    /**
     * Gets the current score.
     * @return The current score.
     */
    int getScore();

    /**
     * Checks if the game is paused.
     * @return True if game is paused, false otherwise.
     */
    boolean isPaused();

    /**
     * Sets the pause state of the game.
     * @param paused The new pause state.
     */
    void setPaused(boolean paused);

    /**
     * Checks if the game is over.
     * @return True if game is over, false otherwise.
     */
    boolean isGameOver();

    /**
     * Sets the game over state.
     * @param gameOver The new game over state.
     */
    void setGameOver(boolean gameOver);
} 