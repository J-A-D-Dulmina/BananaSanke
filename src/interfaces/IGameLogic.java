package interfaces;

import java.awt.Point;
import java.util.List;

/**
 * Interface defining the core game logic operations for the snake game.
 */
public interface IGameLogic {
    /**
     * Updates the game state for one frame.
     */
    void updateGame();

    /**
     * Changes the direction of the snake.
     * @param newDirection The new direction to move in.
     */
    void changeDirection(String newDirection);

    /**
     * Gets the current positions of all snake segments.
     * @return List of points representing snake positions.
     */
    List<Point> getSnakePositions();

    /**
     * Gets the current direction of the snake.
     * @return The current direction.
     */
    String getDirection();

    /**
     * Resets the game to initial state.
     */
    void reset();

    /**
     * Gets the width of the game panel.
     * @return The panel width.
     */
    int getPanelWidth();

    /**
     * Gets the height of the game panel.
     * @return The panel height.
     */
    int getPanelHeight();

    /**
     * Gets the size of each tile in the game grid.
     * @return The tile size.
     */
    int getTileSize();

    /**
     * Checks if the game is currently running.
     * @return True if game is running, false otherwise.
     */
    boolean isRunning();

    /**
     * Sets the running state of the game.
     * @param running The new running state.
     */
    void setRunning(boolean running);

    /**
     * Gets the current score.
     * @return The current score.
     */
    int getScore();
} 