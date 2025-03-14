package controller;

import model.AccountModel;
import view.AccountPanel;
import view.GameMainInterface;
import view.LoginUI;
import org.json.JSONObject;
import javax.swing.SwingUtilities;
import interfaces.ISoundManager;
import interfaces.ISessionManager;
import interfaces.IAccountController;
import interfaces.IAccountModel;
import interfaces.IUserService;

/**
 * Controller for the Account Panel.
 */
public class AccountController implements IAccountController {
    private final IAccountModel model;
    private final AccountPanel view;
    private final GameMainInterface mainFrame;
    private final ISoundManager soundManager;
    private final ISessionManager sessionManager;

    public AccountController(
            IAccountModel model, 
            AccountPanel view, 
            GameMainInterface mainFrame,
            ISoundManager soundManager,
            ISessionManager sessionManager) {
        this.model = model;
        this.view = view;
        this.mainFrame = mainFrame;
        this.soundManager = soundManager;
        this.sessionManager = sessionManager;
    }

    /**
     * Initializes the view with data from the model.
     */
    @Override
    public void initializeView() {
        view.setUsername(model.getUsername());
        view.setBestScore(model.getBestScore());
    }

    /**
     * Handles username update request.
     * @param newUsername The new username to set.
     */
    @Override
    public void handleUsernameUpdate(String newUsername) {
        try {
            JSONObject response = model.updateUsername(newUsername);
            if (response.getString("status").equals("success")) {
                view.setUsername(newUsername);
                view.showSuccessMessage("Username updated successfully!");
                
                // Update the username display in the main interface
                mainFrame.updateUsernameDisplay(newUsername);
            } else {
                view.showErrorMessage(response.optString("message", "Failed to update username"));
            }
        } catch (Exception e) {
            view.showErrorMessage("Error: " + e.getMessage());
        }
    }

    /**
     * Handles password update request.
     * @param oldPassword The current password.
     * @param newPassword The new password.
     * @param confirmPassword The confirmation of the new password.
     */
    @Override
    public void handlePasswordUpdate(String oldPassword, String newPassword, String confirmPassword) {
        try {
            // Validate inputs
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                view.showErrorMessage("All password fields are required");
                return;
            }
            
            if (newPassword.length() < 6) {
                view.showErrorMessage("New password must be at least 6 characters long");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                view.showErrorMessage("New passwords do not match");
                return;
            }
            
            // Update password
            JSONObject response = model.updatePassword(oldPassword, newPassword);
            if (response.getString("status").equals("success")) {
                view.clearPasswordFields();
                view.showSuccessMessage("Password updated successfully!");
            } else {
                view.showErrorMessage(response.optString("message", "Failed to update password"));
            }
        } catch (Exception e) {
            view.showErrorMessage("Error: " + e.getMessage());
        }
    }

    /**
     * Handles logout request.
     */
    @Override
    public void handleLogout() {
        try {
            // Stop all sounds
            soundManager.stopAll();
            
            // Logout from server
            model.logout();
            
            // Clear session
            sessionManager.logout();
            
            // Close main frame and show login screen
            SwingUtilities.invokeLater(() -> {
                try {
                    mainFrame.dispose();
                    new LoginUI().setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error showing login window: " + e.getMessage());
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            view.showErrorMessage("Error during logout: " + e.getMessage());
        }
    }
} 