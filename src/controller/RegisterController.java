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
        if (username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            registerUI.showMessage("All fields are required.", false);
            return;
        }

        String response = Register.registerUser(username, email, password);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message");

            if (status.equals("success")) {
                registerUI.showMessage("Registration Successful! Please login.", true);
                registerUI.dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            } else {
                registerUI.showMessage(message, false);
            }
        } catch (Exception e) {
            registerUI.showMessage("An error occurred during registration. Please try again.", false);
        }
    }
}
