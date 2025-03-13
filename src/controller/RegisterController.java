package controller;

import javax.swing.SwingUtilities;
import org.json.JSONException;
import org.json.JSONObject;

import model.Register;
import view.LoginUI;
import view.RegisterUI;

public class RegisterController {
    private final RegisterUI registerUI;

    public RegisterController(RegisterUI registerUI) {
        this.registerUI = registerUI;
    }

    public void handleRegister(String username, String email, String password) {
        // Validate input
        if (username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            registerUI.showMessage("All fields are required.", false);
            return;
        }

        String response = Register.registerUser(username, email, password);
        System.out.println("Server Response: " + response); // Debug log

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message");

            if (status.equals("success")) {
                registerUI.showMessage("Registration Successful! Please login.", true);
                registerUI.dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            } else {
                // Show the specific error message from the server
                registerUI.showMessage(message, false);
            }
        } catch (JSONException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            registerUI.showMessage("An error occurred during registration. Please try again.", false);
        }
    }
}
