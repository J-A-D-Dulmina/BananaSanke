package interfaces;

import java.awt.event.KeyEvent;

/**
 * Interface for the Snake Game Controller
 */
public interface ISnakeGameController {
    
    /**
     * Starts the game
     */
    void startGame();
    
    /**
     * Resets the game
     */
    void resetGame();
    
    /**
     * Handles key press events
     * 
     * @param e The key event
     */
    void handleKeyPress(KeyEvent e);
    
    /**
     * Toggles the game pause state
     */
    void togglePause();
    
    /**
     * Checks if a score is a high score
     * 
     * @param score The score to check
     * @return true if it's a high score
     */
    boolean isHighScore(int score);
    
    /**
     * Handles saving a new high score
     * 
     * @param score The score to save
     * @return true if high score was saved successfully
     */
    boolean saveHighScore(int score);
} 