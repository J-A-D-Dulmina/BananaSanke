package model;

import org.json.JSONObject;
import api.APIClient;
import java.util.List;
import controller.LeaderboardController;
import controller.LeaderboardController.LeaderboardEntry;
import interfaces.ISessionManager;
import interfaces.IAccountModel;

public class AccountModel implements IAccountModel {
    private String username;
    private String email;
    private int bestScore;
    private final ISessionManager sessionManager;

    public AccountModel() {
        this.sessionManager = SessionManagerImpl.getInstance();
        this.username = sessionManager.getUsername();
        this.email = sessionManager.getEmail();
        this.bestScore = 0;
        fetchBestScore();
    }

    @Override
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

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public int getBestScore() {
        return bestScore;
    }

    @Override
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
            sessionManager.setUsername(newUsername);
        }
        return jsonResponse;
    }

    @Override
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

    @Override
    public void logout() {
        APIClient.logoutUser();
    }
} 