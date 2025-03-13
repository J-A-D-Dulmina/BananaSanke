package controller;

import api.APIClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import model.SessionManager;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.text.SimpleDateFormat;
import model.LeaderboardModel;
import view.LeaderboardPanel;
import javax.swing.SwingUtilities;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controls the leaderboard by managing score retrieval and UI updates.
 */
public class LeaderboardController {
    private static final int REFRESH_INTERVAL = 30000; // 30 seconds
    private final LeaderboardModel model;
    private final LeaderboardPanel view;
    private Timer refreshTimer;
    private final AtomicBoolean isUpdating;
    private volatile boolean isInitialized;
    
    public LeaderboardController(LeaderboardModel model, LeaderboardPanel view) {
        if (model == null || view == null) {
            throw new IllegalArgumentException("Model and view cannot be null");
        }
        this.model = model;
        this.view = view;
        this.isUpdating = new AtomicBoolean(false);
        this.isInitialized = false;
        
        // Initialize the view and start updates
        if (SwingUtilities.isEventDispatchThread()) {
            initializeAndStart();
        } else {
            SwingUtilities.invokeLater(this::initializeAndStart);
        }
    }

    private void initializeAndStart() {
        try {
            // Initialize view first
            initializeView();
            // Then start auto-refresh if initialization was successful
            if (isInitialized) {
                startAutoRefresh();
            }
        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
            // Show error to user
            view.showError("Failed to load leaderboard: " + e.getMessage());
        }
    }

    private void initializeView() {
        if (!isInitialized) {
            try {
                // Initial update of the leaderboard
                model.fetchLeaderboard();
                List<LeaderboardEntry> entries = model.getEntries();
                view.updateLeaderboard(entries);
                updateUserStats(entries);
                isInitialized = true;
            } catch (Exception e) {
                System.err.println("Error initializing view: " + e.getMessage());
                e.printStackTrace();
                view.showError("Failed to initialize leaderboard: " + e.getMessage());
                // Don't rethrow - handle the error gracefully
                isInitialized = false;
            }
        }
    }

    public void updateLeaderboard() {
        // Prevent concurrent updates
        if (!isUpdating.compareAndSet(false, true)) {
            return;
        }

        try {
            view.showLoading(true);
            model.fetchLeaderboard();
            SwingUtilities.invokeLater(() -> {
                try {
                    List<LeaderboardEntry> entries = model.getEntries();
                    view.updateLeaderboard(entries);
                    updateUserStats(entries);
                } catch (Exception e) {
                    System.err.println("Error updating leaderboard view: " + e.getMessage());
                    e.printStackTrace();
                    view.showError("Failed to update leaderboard: " + e.getMessage());
                } finally {
                    view.showLoading(false);
                    isUpdating.set(false);
                }
            });
        } catch (Exception e) {
            System.err.println("Error in updateLeaderboard: " + e.getMessage());
            e.printStackTrace();
            view.showError("Failed to fetch leaderboard data: " + e.getMessage());
            view.showLoading(false);
            isUpdating.set(false);
        }
    }

    private void updateUserStats(List<LeaderboardEntry> entries) {
        String currentUsername = SessionManager.getUsername();
        if (currentUsername == null || entries.isEmpty()) {
            view.updateUserRank(0, 0);
            view.updateBestScore(0);
            return;
        }

        int userScore = 0;
        boolean found = false;

        for (LeaderboardEntry entry : entries) {
            if (currentUsername.equals(entry.getUsername())) {
                found = true;
                userScore = entry.getScore();
                view.updateBestScore(userScore);
                break;
            }
        }

        // Use server-provided rank if available
        int userRank = model.getUserRank();
        if (userRank > 0) {
            view.updateUserRank(userRank, model.getTotalPlayers());
        } else if (found) {
            // Fallback to calculating rank locally if server didn't provide it
            int rank = 1;
            for (LeaderboardEntry entry : entries) {
                if (currentUsername.equals(entry.getUsername())) {
                    view.updateUserRank(rank, model.getTotalPlayers());
                    break;
                }
                rank++;
            }
        } else {
            view.updateUserRank(0, model.getTotalPlayers());
            view.updateBestScore(0);
        }
    }

    private void startAutoRefresh() {
        stopAutoRefresh(); // Ensure any existing timer is cancelled
        refreshTimer = new Timer("LeaderboardRefresh", true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isUpdating.get()) {
                    updateLeaderboard();
                }
            }
        }, REFRESH_INTERVAL, REFRESH_INTERVAL);
    }

    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }

    public void onClose() {
        stopAutoRefresh();
    }

    public static class LeaderboardEntry implements Comparable<LeaderboardEntry> {
        private final int id;
        private final String username;
        private final int userId;
        private final int score;
        private final String createdAt;

        public LeaderboardEntry(int id, String username, int userId, int score, String createdAt) {
            this.id = id;
            this.username = username != null ? username : "";
            this.userId = userId;
            this.score = score;
            this.createdAt = createdAt != null ? createdAt : "";
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public int getUserId() { return userId; }
        public int getScore() { return score; }
        public String getCreatedAt() { return createdAt; }

        @Override
        public int compareTo(LeaderboardEntry other) {
            if (other == null) return 1;
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

    public interface LeaderboardCallback {
        void onSuccess(List<LeaderboardEntry> entries);
        void onFailure(String error);
    }

    public int getUserBestScore(String username) {
        if (model == null || username == null || username.isEmpty()) {
            return 0;
        }
        
        return model.getEntryByUsername(username)
            .map(LeaderboardEntry::getScore)
            .orElse(0);
    }

    public List<LeaderboardEntry> getCurrentScores() {
        return model != null ? model.getScores() : new ArrayList<>();
    }

    public int getTotalPlayers() {
        return model != null ? model.getTotalPlayers() : 0;
    }

    public int getHighestScore() {
        return model != null ? model.getHighestScore() : 0;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
