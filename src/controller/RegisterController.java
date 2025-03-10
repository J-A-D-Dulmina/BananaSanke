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
        String response = Register.registerUser(username, email, password);
        
        // Debugging - Print the response from the server
        System.out.println("Server Response: " + response); 

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message"); // Get the actual error message

            if (status.equals("success")) {
                registerUI.showMessage("Registration Successful!", true);
                registerUI.dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            } else {
                // Show the exact error message from the API
                registerUI.showMessage(message, false);
            }
        } catch (JSONException e) {
            registerUI.showMessage(response, false);
        }
    }

}
