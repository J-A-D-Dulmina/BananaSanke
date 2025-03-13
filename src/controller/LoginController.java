package controller;

import javax.swing.SwingUtilities;
import org.json.JSONException;
import org.json.JSONObject;
import api.APIClient;
import view.GameMainInterface;
import view.LoginUI;
import model.SessionManager;

public class LoginController {
    private final LoginUI loginUI;

    public LoginController(LoginUI loginUI) {
        this.loginUI = loginUI;
    }

    public void handleLogin(String email, String password) {
        try {
            String response = APIClient.loginUser(email, password);
            System.out.println("Server Response: " + response);

            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");

            if (status.equals("success")) {
                // Extract all necessary data from response
                String authToken = jsonResponse.getString("auth_token");
                String username = jsonResponse.getString("username");
                JSONObject userObj = jsonResponse.getJSONObject("user");
                String userEmail = userObj.getString("email");
                
                // Update session with all user data
                SessionManager.setAuthToken(authToken);
                SessionManager.setUsername(username);
                SessionManager.setEmail(userEmail);
                
                System.out.println("Login successful - Username: " + username);
                
                // Show success message
                loginUI.showMessage("Login Successful!", true);
                loginUI.dispose(); // Close login window
                
                // Open the game main interface
                openGameInterface();
            } else {
                // Show error message from server
                String message = jsonResponse.optString("message", "Login failed. Please try again.");
                System.err.println("Login failed: " + message);
                loginUI.showMessage(message, false);
            }
        } catch (JSONException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            e.printStackTrace();
            loginUI.showMessage("An error occurred. Please try again.", false);
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            loginUI.showMessage("An unexpected error occurred. Please try again.", false);
        }
    }

    private void openGameInterface() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create and show the game interface
                GameMainInterface gameMain = new GameMainInterface();
                gameMain.setVisible(true);
                
                System.out.println("Game interface opened successfully");
            } catch (Exception e) {
                System.err.println("Error opening game interface: " + e.getMessage());
                e.printStackTrace();
                loginUI.showMessage("Error starting game. Please try logging in again.", false);
            }
        });
    }
}
