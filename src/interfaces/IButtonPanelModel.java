package interfaces;

import view.SnakePanel;

/**
 * Interface defining the operations for the button panel model.
 */
public interface IButtonPanelModel {
    /**
     * Updates the username in the model.
     * @param newUsername The new username to set.
     */
    void updateUsername(String newUsername);
    
    /**
     * Gets the current username.
     * @return The current username.
     */
    String getUsername();
    
    /**
     * Gets the game logic instance.
     * @return The game logic instance.
     */
    IGameLogic getGameLogic();
    
    /**
     * Gets the snake panel instance.
     * @return The snake panel instance.
     */
    SnakePanel getSnakePanel();
    
    /**
     * Starts the game.
     */
    void startGame();
    
    /**
     * Pauses the game.
     */
    void pauseGame();
    
    /**
     * Resumes the game.
     */
    void resumeGame();
    
    /**
     * Resets the game to initial state.
     */
    void resetGame();
    
    /**
     * Stops the game.
     */
    void stopGame();
    
    /**
     * Checks if the game has started.
     * @return True if the game has started, false otherwise.
     */
    boolean isGameStarted();
    
    /**
     * Checks if the game is paused.
     * @return True if the game is paused, false otherwise.
     */
    boolean isGamePaused();
} 