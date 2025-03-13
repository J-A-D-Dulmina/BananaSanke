package model;

import org.json.JSONObject;
import api.APIClient;
import java.util.List;
import controller.LeaderboardController;
import controller.LeaderboardController.LeaderboardEntry;

public class AccountModel {
    private String username;
    private String email;
    private int bestScore;

    public AccountModel() {
        this.username = SessionManager.getUsername();
        this.email = SessionManager.getEmail();
        this.bestScore = 0;
        fetchBestScore();
    }

    public JSONObject fetchBestScore() {
        try {
            String response = APIClient.getBestScore();
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getString("status").equals("success")) {
                this.bestScore = jsonResponse.getInt("best_score");
            }
            return jsonResponse;
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch best score");
            return errorResponse;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getBestScore() {
        return bestScore;
    }

    public JSONObject updateUsername(String newUsername) throws Exception {
        if (newUsername.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        String response = APIClient.sendAuthenticatedPostRequest(
            APIClient.BASE_URL + "?action=update_username",
            "new_username=" + newUsername
        );

        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.getString("status").equals("success")) {
            this.username = newUsername;
            SessionManager.setUsername(newUsername);
        }
        return jsonResponse;
    }

    public JSONObject updatePassword(String oldPassword, String newPassword) throws Exception {
        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Password fields cannot be empty");
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }

        String response = APIClient.updatePassword(oldPassword, newPassword);
        return new JSONObject(response);
    }

    public void logout() {
        APIClient.logoutUser();
    }
} 