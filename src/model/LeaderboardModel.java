package model;

import api.APIClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import controller.LeaderboardController.LeaderboardEntry;
import java.util.Collections;

public class LeaderboardModel {
    private volatile List<LeaderboardEntry> entries;
    private volatile int userRank;
    private volatile int userScore;
    private volatile int totalPlayers;
    private volatile String lastError;

    public LeaderboardModel() {
        this.entries = Collections.synchronizedList(new ArrayList<>());
        this.userRank = 0;
        this.userScore = 0;
        this.totalPlayers = 0;
        this.lastError = "";
    }

    public synchronized List<LeaderboardEntry> fetchLeaderboard() throws Exception {
        try {
            String response = APIClient.getLeaderboard();
            if (response == null || response.trim().isEmpty()) {
                throw new Exception("Empty response from server");
            }

            JSONObject jsonResponse = new JSONObject(response);
            
            if (!"success".equals(jsonResponse.optString("status"))) {
                String errorMessage = jsonResponse.optString("message", "Unknown error occurred");
                this.lastError = errorMessage;
                throw new Exception("Failed to fetch leaderboard: " + errorMessage);
            }

            // Clear existing entries in a thread-safe way
            synchronized (entries) {
                entries.clear();
                
                // Parse scores array
                JSONArray scores = jsonResponse.getJSONArray("scores");
                for (int i = 0; i < scores.length(); i++) {
                    JSONObject score = scores.getJSONObject(i);
                    LeaderboardEntry entry = new LeaderboardEntry(
                        score.optInt("id", 0),
                        score.optString("username", "Unknown"),
                        score.optInt("id", 0),
                        score.optInt("score", 0),
                        score.optString("created_at", "")
                    );
                    entries.add(entry);
                }

                // Update user data
                this.userRank = jsonResponse.optInt("user_rank", 0);
                this.userScore = jsonResponse.optInt("user_score", 0);
                this.totalPlayers = jsonResponse.optInt("total_players", 0);
                this.lastError = "";

                // Return a copy of the list to prevent external modification
                return new ArrayList<>(entries);
            }
        } catch (Exception e) {
            this.lastError = "Failed to fetch leaderboard: " + e.getMessage();
            System.err.println("Error fetching leaderboard: " + e.getMessage());
            e.printStackTrace();
            throw new Exception(this.lastError);
        }
    }

    public synchronized int getUserRank() {
        return userRank;
    }

    public synchronized int getUserScore() {
        return userScore;
    }

    public synchronized int getTotalPlayers() {
        return totalPlayers;
    }

    public synchronized String getLastError() {
        return lastError;
    }

    public synchronized List<LeaderboardEntry> getEntries() {
        synchronized (entries) {
            return new ArrayList<>(entries);
        }
    }

    public synchronized int getHighestScore() {
        synchronized (entries) {
            if (entries.isEmpty()) {
                return 0;
            }
            return entries.stream()
                .mapToInt(LeaderboardEntry::getScore)
                .max()
                .orElse(0);
        }
    }

    public synchronized Optional<LeaderboardEntry> getEntryByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        synchronized (entries) {
            return entries.stream()
                .filter(entry -> username.equals(entry.getUsername()))
                .findFirst();
        }
    }

    public synchronized List<LeaderboardEntry> getScores() {
        synchronized (entries) {
            return new ArrayList<>(entries);
        }
    }
} 