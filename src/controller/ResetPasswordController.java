package controller;

import view.ResetPasswordUI;
import view.LoginUI;
import model.ResetPasswordModel;
import api.APIClient;
import org.json.JSONObject;
import org.json.JSONException;
import javax.swing.SwingUtilities;

public class ResetPasswordController {
    private ResetPasswordUI view;
    private ResetPasswordModel model;

    public ResetPasswordController(ResetPasswordUI view, String username, String email) {
        this.view = view;
        // Create new model since view doesn't have getModel method
        this.model = new ResetPasswordModel(username, email);
    }

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        try {
            // Update model with new values
            model.setResetToken(token);
            model.setNewPassword(newPassword);
            model.setConfirmPassword(confirmPassword);

            // Validate using model
            if (!model.validateToken()) {
                updateMessage("Please enter the reset token from your email!", false);
                return;
            }

            if (!model.validatePasswords()) {
                if (newPassword.isEmpty()) {
                    updateMessage("Please enter a new password!", false);
                } else if (!newPassword.equals(confirmPassword)) {
                    updateMessage("Passwords do not match!", false);
                } else {
                    updateMessage("Password must be at least 6 characters long!", false);
                }
                return;
            }

            // Make API call
            String response = APIClient.verifyResetToken(
                model.getUsername(), 
                model.getResetToken(), 
                model.getNewPassword()
            );
            
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                updateMessage("Password reset successful!", true);
                navigateToLogin();
            } else {
                String errorMessage = jsonResponse.optString("message", "Unknown error occurred");
                updateMessage(errorMessage, false);
            }
        } catch (JSONException e) {
            handleError(e, "Invalid response from server");
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void resendToken() {
        try {
            if (!model.isResendEnabled()) {
                updateMessage("Please wait before requesting another token.", false);
                return;
            }

            // Clear any existing token first
            clearResetToken();

            String response = APIClient.requestPasswordReset(
                model.getUsername(), 
                model.getEmail()
            );
            
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                updateMessage("New reset token sent to your email!", true);
                model.resetCooldown();
                view.resetCooldown();
            } else {
                String errorMessage = jsonResponse.optString("message", "Failed to send reset token");
                updateMessage(errorMessage, false);
            }
        } catch (JSONException e) {
            handleError(e, "Invalid response from server");
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void clearResetToken() {
        try {
            String response = APIClient.clearResetToken(model.getUsername());
            JSONObject jsonResponse = new JSONObject(response);
            
            if (!jsonResponse.getString("status").equals("success")) {
                System.err.println("Warning: Failed to clear reset token: " + 
                    jsonResponse.optString("message", "Unknown error"));
            }
        } catch (Exception e) {
            // Log error but don't show to user since this is cleanup
            System.err.println("Error clearing reset token: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        view.dispose();
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }

    private void handleError(Exception e) {
        handleError(e, null);
    }

    private void handleError(Exception e, String defaultMessage) {
        String errorMessage = defaultMessage != null ? defaultMessage : 
            "An unexpected error occurred. Please try again.";
            
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            errorMessage = "Error: " + e.getMessage();
        }
        
        updateMessage(errorMessage, false);
        System.err.println("Error in ResetPasswordController: " + e.getMessage());
        e.printStackTrace();
    }

    private void updateMessage(String message, boolean success) {
        // Update the message label in the view
        view.updateMessageLabel(message, success);
    }
} 