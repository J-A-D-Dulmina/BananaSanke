package model;

import org.json.JSONObject;
import java.util.List;
import controller.LeaderboardController;
import controller.LeaderboardController.LeaderboardEntry;
import interfaces.ISessionManager;
import interfaces.IAccountModel;
import interfaces.IUserService;

public class AccountModel implements IAccountModel {
    private String username;
    private String email;
    private int bestScore;
    private final IUserService userService;

    public AccountModel(IUserService userService) {
        this.userService = userService;
        this.username = userService.getUsername();
        this.email = userService.getEmail();
        this.bestScore = 0;
        fetchBestScore();
    }

    @Override
    public JSONObject fetchBestScore() {
        try {
            JSONObject jsonResponse = userService.fetchBestScore();
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

        JSONObject jsonResponse = userService.updateUsername(newUsername);
        if (jsonResponse.getString("status").equals("success")) {
            this.username = newUsername;
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

        return userService.updatePassword(oldPassword, newPassword);
    }

    @Override
    public void logout() {
        userService.logout();
    }
} 