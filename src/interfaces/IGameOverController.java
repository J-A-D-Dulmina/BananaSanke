package interfaces;

/**
 * Interface for the GameOverController that manages game over functionality
 */
public interface IGameOverController {
    
    /**
     * Sets the game results
     * 
     * @param finalScore the final score of the game
     * @param highScore the current high score
     * @param playerName the name of the player
     */
    void setGameResults(int finalScore, int highScore, String playerName);
    
    /**
     * Saves the game result if it is a high score
     */
    void saveGameResult();
    
    /**
     * Resets the game
     */
    void resetGame();
    
    /**
     * Gets the game over model
     * 
     * @return the game over model
     */
    IGameOverModel getModel();
    
    /**
     * Disposes of resources used by the controller
     */
    void dispose();
    
    /**
     * Updates the high score display
     */
    void updateHighScore();
    
    /**
     * Saves the score to the server
     */
    void saveScore();
    
    /**
     * Restarts the game
     */
    void restartGame();
    
    /**
     * Returns to the main menu
     */
    void returnToMainMenu();
    
    /**
     * Shows a high score message in the view
     * 
     * @param score The high score to display
     */
    void showHighScoreMessage(int score);
    
    /**
     * Checks if a score is a high score and updates the display accordingly
     * 
     * @param score The score to check
     * @return true if it's a high score
     */
    boolean checkAndDisplayHighScore(int score);
} 