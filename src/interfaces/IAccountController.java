package interfaces;

/**
 * Interface defining the operations for the account controller.
 */
public interface IAccountController {
    /**
     * Initializes the view with data from the model.
     */
    void initializeView();
    
    /**
     * Handles username update request.
     * @param newUsername The new username to set.
     */
    void handleUsernameUpdate(String newUsername);
    
    /**
     * Handles password update request.
     * @param oldPassword The current password.
     * @param newPassword The new password.
     * @param confirmPassword The confirmation of the new password.
     */
    void handlePasswordUpdate(String oldPassword, String newPassword, String confirmPassword);
    
    /**
     * Handles logout request.
     */
    void handleLogout();
} 