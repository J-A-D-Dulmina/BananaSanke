package controller;

import model.AccountModel;
import view.AccountPanel;
import view.LoginUI;
import view.GameMainInterface;
import javax.swing.Timer;
import org.json.JSONObject;
import api.APIClient;

public class AccountController {
    private final AccountModel model;
    private final AccountPanel view;
    private final GameMainInterface mainFrame;

    public AccountController(AccountModel model, AccountPanel view, GameMainInterface mainFrame) {
        this.model = model;
        this.view = view;
        this.mainFrame = mainFrame;
    }

    public void initializeView() {
        if (model != null && view != null) {
            view.setUsername(model.getUsername());
            view.setEmail(model.getEmail());
            view.setBestScore(model.getBestScore());
        }
    }

    public void handleUsernameUpdate(String newUsername) {
        try {
            JSONObject response = model.updateUsername(newUsername);
            if (response.getString("status").equals("success")) {
                view.setUsername(newUsername);
                view.showSuccessMessage("Username updated successfully!");
                mainFrame.setTitle("Banana Snake - " + newUsername);
                mainFrame.updateUsernameDisplay(newUsername);
            } else {
                view.showErrorMessage(response.getString("message"));
            }
        } catch (Exception e) {
            view.showErrorMessage("Error updating username: " + e.getMessage());
        }
    }

    public void handlePasswordUpdate(String oldPassword, String newPassword, String confirmPassword) {
        try {
            // Client-side validation
            if (oldPassword.isEmpty()) {
                view.showErrorMessage("Old password is required");
                return;
            }
            if (newPassword.isEmpty()) {
                view.showErrorMessage("New password is required");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                view.showErrorMessage("New passwords do not match");
                return;
            }
            if (newPassword.length() < 6) {
                view.showErrorMessage("New password must be at least 6 characters long");
                return;
            }

            JSONObject response = model.updatePassword(oldPassword, newPassword);
            if (response.getString("status").equals("success")) {
                view.clearPasswordFields();
                view.showSuccessMessage("Password updated successfully!");
            } else {
                String errorMessage = response.optString("message", "Failed to update password");
                view.showErrorMessage(errorMessage);
            }
        } catch (IllegalArgumentException e) {
            view.showErrorMessage(e.getMessage());
        } catch (Exception e) {
            view.showErrorMessage("Error updating password: " + e.getMessage());
        }
    }

    public void handleLogout() {
        try {
            // Stop any ongoing game
            if (mainFrame.getSnakePanel().isGameStarted()) {
                mainFrame.getSnakePanel().getGameController().stopGame();
            }
            
            // Call API to logout
            String response = APIClient.logoutUser();
            
            // Parse response
            JSONObject jsonResponse = new JSONObject(response);
            
            // Close the account panel
            view.dispose();
            
            // Close main frame and show login UI
            mainFrame.dispose();
            new LoginUI().setVisible(true);
        } catch (Exception e) {
            view.showErrorMessage("Error during logout: " + e.getMessage());
        }
    }
} 