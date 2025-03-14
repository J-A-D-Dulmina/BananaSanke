package interfaces;

/**
 * Interface defining the operations for the button panel controller.
 */
public interface IButtonPanelController {
    /**
     * Gets the button panel model.
     * @return The button panel model.
     */
    IButtonPanelModel getModel();
    
    /**
     * Handles the play/pause button click.
     */
    void handlePlayPause();
    
    /**
     * Handles the reset button click.
     */
    void handleReset();
    
    /**
     * Handles the logout button click.
     */
    void handleLogout();
    
    /**
     * Handles the settings button click.
     */
    void handleSettings();
    
    /**
     * Handles the account button click.
     */
    void handleAccount();
    
    /**
     * Handles the leaderboard button click.
     */
    void handleLeaderboard();
    
    /**
     * Handles clicks on the pause overlay.
     */
    void handlePauseOverlayClick();
    
    /**
     * Updates the username display.
     * @param newUsername The new username to display.
     */
    void updateUsername(String newUsername);
} 