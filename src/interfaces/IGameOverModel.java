package interfaces;

/**
 * Interface for the GameOverModel that manages game result data
 */
public interface IGameOverModel {
    
    /**
     * Sets the game results
     * 
     * @param finalScore the final score of the game
     * @param highScore the current high score
     * @param playerName the name of the player
     */
    void setGameResults(int finalScore, int highScore, String playerName);
    
    /**
     * Gets the final score of the game
     * 
     * @return the final score
     */
    int getFinalScore();
    
    /**
     * Sets the final score
     * 
     * @param finalScore the final score to set
     */
    void setFinalScore(int finalScore);
    
    /**
     * Gets the high score
     * 
     * @return the high score
     */
    int getHighScore();
    
    /**
     * Sets the high score
     * 
     * @param highScore the high score to set
     */
    void setHighScore(int highScore);
    
    /**
     * Gets the player name
     * 
     * @return the player name
     */
    String getPlayerName();
    
    /**
     * Sets the player name
     * 
     * @param playerName the player name to set
     */
    void setPlayerName(String playerName);
    
    /**
     * Checks if the final score is a new high score
     * 
     * @return true if the final score is higher than the high score
     */
    boolean isHighScore();
    
    /**
     * Sets whether the final score is a high score
     * 
     * @param highScore true if the final score is a high score
     */
    void setHighScore(boolean highScore);
    
    /**
     * Checks if the game has been saved
     * 
     * @return true if the game has been saved
     */
    boolean isGameSaved();
    
    /**
     * Sets whether the game has been saved
     * 
     * @param gameSaved true if the game has been saved
     */
    void setGameSaved(boolean gameSaved);
    
    /**
     * Resets the model to its initial state
     */
    void reset();
    
    /**
     * Returns whether a high score message should be displayed
     * 
     * @return true if high score message should be displayed
     */
    boolean shouldShowHighScoreMessage();
    
    /**
     * Sets whether to show high score message
     * 
     * @param show true to show the message
     */
    void setShouldShowHighScoreMessage(boolean show);
} 