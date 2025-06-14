package controller;

import view.ResetPasswordUI;
import view.LoginUI;
import model.ResetPasswordModel;
import org.json.JSONObject;
import org.json.JSONException;
import javax.swing.SwingUtilities;
import factory.ComponentFactory;
import interfaces.IAPIClient;
import interfaces.IResetPasswordController;
import interfaces.IResetPasswordModel;

public class ResetPasswordController implements IResetPasswordController {
    private ResetPasswordUI view;
    private IResetPasswordModel model;
    private final IAPIClient apiClient;

    public ResetPasswordController(ResetPasswordUI view, String username, String email) {
        this.view = view;
        // Create new model since view doesn't have getModel method
        this.model = new ResetPasswordModel(username, email);
        this.apiClient = ComponentFactory.getAPIClient();
    }

    @Override
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
            String response = apiClient.verifyResetToken(
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

    @Override
    public void resendToken() {
        try {
            if (!model.isResendEnabled()) {
                updateMessage("Please wait before requesting another token.", false);
                return;
            }

            String response = apiClient.requestPasswordReset(
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

    @Override
    public void clearResetToken() {
        try {
            String response = apiClient.clearResetToken(model.getUsername());
            JSONObject jsonResponse = new JSONObject(response);
            
            if (!jsonResponse.getString("status").equals("success")) {
                // Silently fail for cleanup operations
            }
        } catch (Exception e) {
            // Silently fail for cleanup operations
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
    }

    private void updateMessage(String message, boolean success) {
        view.updateMessageLabel(message, success);
    }
} 