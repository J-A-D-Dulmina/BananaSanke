package controller;

import api.APIClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import model.SessionManager;
import java.util.Collections;
import java.util.Comparator;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controls the leaderboard by managing score retrieval and UI updates.
 */
public class LeaderboardController {
    private static LeaderboardController instance;
    private static final String LEADERBOARD_ACTION = "get_top_scores";
    private static final int MAX_SCORES = 20;
    private static final String API_URL = "https://deshandulmina.info/api.php";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<LeaderboardEntry> currentScores;
    
    private LeaderboardController() {
        currentScores = new ArrayList<>();
    }
    
    public static LeaderboardController getInstance() {
        if (instance == null) {
            instance = new LeaderboardController();
        }
        return instance;
    }

    public interface LeaderboardCallback {
        void onSuccess(List<LeaderboardEntry> scores);
        void onFailure(String error);
    }

    public static class LeaderboardEntry implements Comparable<LeaderboardEntry> {
        private final int id;
        private final String username;
        private final int userId;
        private final int score;
        private final String createdAt;

        public LeaderboardEntry(int id, String username, int userId, int score, String createdAt) {
            this.id = id;
            this.username = username;
            this.userId = userId;
            this.score = score;
            this.createdAt = createdAt;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public int getUserId() { return userId; }
        public int getScore() { return score; }
        public String getCreatedAt() { return createdAt; }
        
        // For backward compatibility
        public String getDateAchieved() { return createdAt; }

        @Override
        public int compareTo(LeaderboardEntry other) {
            // First compare by score (descending)
            int scoreCompare = Integer.compare(other.score, this.score);
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            // If scores are equal, compare by date (most recent first)
            return other.createdAt.compareTo(this.createdAt);
        }

        @Override
        public String toString() {
            return String.format("%s: %d points (%s)", username, score, createdAt);
        }
    }

    public interface RankCallback {
        void onSuccess(int rank, int totalPlayers);
        void onFailure(String error);
    }

    public void fetchTopScores(LeaderboardCallback callback) {
        String authToken = SessionManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            callback.onFailure("Not authenticated");
            return;
        }

        try {
            // Build the URL with proper encoding
            String url = String.format("%s?action=%s&limit=%d",
                API_URL,
                URLEncoder.encode(LEADERBOARD_ACTION, StandardCharsets.UTF_8.toString()),
                MAX_SCORES
            );
            
            String response = APIClient.sendAuthenticatedGetRequest(url);
            System.out.println("Leaderboard Request URL: " + url); // Debug log
            System.out.println("API Response: " + response); // Debug log

            // Handle empty response
            if (response == null || response.isEmpty()) {
                callback.onFailure("Empty response from server");
                return;
            }

            // Handle HTML error responses
            if (response.contains("<br") || response.contains("<html") || response.contains("Fatal error")) {
                String errorMessage = response.replaceAll("<[^>]*>", "")
                    .replaceAll("\\s+", " ")
                    .trim();
                System.err.println("Server Error: " + errorMessage);
                callback.onFailure("Server Error: " + errorMessage);
                return;
            }

            // Parse JSON response
            try {
                JSONObject jsonResponse = new JSONObject(response);
                String status = jsonResponse.optString("status", "error");
                
                if ("success".equals(status)) {
                    JSONArray scoresArray = jsonResponse.getJSONArray("scores");
                    currentScores.clear();
                    
                    for (int i = 0; i < scoresArray.length(); i++) {
                        JSONObject scoreObj = scoresArray.getJSONObject(i);
                        currentScores.add(new LeaderboardEntry(
                            scoreObj.getInt("id"),
                            scoreObj.getString("username"),
                            scoreObj.getInt("user_id"),
                            scoreObj.getInt("score"),
                            scoreObj.getString("created_at")
                        ));
                    }
                    
                    // Sort scores in descending order
                    Collections.sort(currentScores);
                    callback.onSuccess(currentScores);
                } else {
                    String message = jsonResponse.optString("message", "Unknown error occurred");
                    callback.onFailure(message);
                }
            } catch (org.json.JSONException e) {
                System.err.println("JSON parsing error: " + e.getMessage());
                System.err.println("Raw response: " + response);
                callback.onFailure("Error parsing server response: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
            e.printStackTrace();
            callback.onFailure("Error fetching leaderboard: " + e.getMessage());
        }
    }

    public void getCurrentUserRank(RankCallback callback) {
        String currentUsername = SessionManager.getUsername();
        if (currentUsername == null || currentUsername.isEmpty()) {
            callback.onFailure("User not logged in");
            return;
        }

        if (currentScores.isEmpty()) {
            callback.onFailure("No scores available");
            return;
        }

        // Find current user's rank
        int rank = 1;
        boolean found = false;
        
        for (LeaderboardEntry entry : currentScores) {
            if (entry.getUsername().equals(currentUsername)) {
                found = true;
                callback.onSuccess(rank, currentScores.size());
                break;
            }
            rank++;
        }

        // If user not found in current scores
        if (!found) {
            callback.onSuccess(currentScores.size() + 1, currentScores.size() + 1);
        }
    }

    public void refreshLeaderboard(LeaderboardCallback callback) {
        fetchTopScores(callback);
    }

    /**
     * Gets the current list of scores without making a new API call
     * @return List of current leaderboard entries
     */
    public List<LeaderboardEntry> getCurrentScores() {
        return new ArrayList<>(currentScores);
    }

    /**
     * Gets the total number of players in the current leaderboard
     * @return Number of players
     */
    public int getTotalPlayers() {
        return currentScores.size();
    }

    /**
     * Gets the highest score in the current leaderboard
     * @return Highest score, or 0 if no scores available
     */
    public int getHighestScore() {
        if (currentScores.isEmpty()) {
            return 0;
        }
        return currentScores.get(0).getScore();
    }

    /**
     * Gets a user's best score from the current leaderboard
     * @param username The username to look for
     * @return The user's best score, or 0 if not found
     */
    public int getUserBestScore(String username) {
        if (username == null || username.isEmpty()) {
            return 0;
        }
        
        return currentScores.stream()
            .filter(entry -> username.equals(entry.getUsername()))
            .mapToInt(LeaderboardEntry::getScore)
            .max()
            .orElse(0);
    }
}
