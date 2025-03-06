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
        String response = APIClient.loginUser(email, password);
        System.out.println("Server Response: " + response); // Debugging API response

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message");

            if (status.equals("success")) {
                // Store session details
                String authToken = jsonResponse.optString("auth_token");
                String username = jsonResponse.optString("username"); // Get username from API response
                
                SessionManager.setAuthToken(authToken);
                SessionManager.setUsername(username); // Store username in session
                
                loginUI.showMessage("Login Successful!", true);
                loginUI.dispose(); // Close login window
                
                // Open the game main interface
                openGameInterface();
            } else {
                loginUI.showMessage(message, false); // Show error message
            }
        } catch (JSONException e) {
            loginUI.showMessage("An error occurred. Please try again.", false);
        }
    }


    private void openGameInterface() {
        SwingUtilities.invokeLater(() -> {
            GameMainInterface gameMain = new GameMainInterface();
            gameMain.setVisible(true);
        });
    }
}
