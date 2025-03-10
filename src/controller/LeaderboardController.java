package controller;

import model.ScoreManager;
import view.LeaderboardPanel;

/**
 * Controls the leaderboard by managing score retrieval and UI updates.
 */
public class LeaderboardController {
    private final ScoreManager scoreManager;
    private final LeaderboardPanel leaderboardPanel;

    /**
     * Initializes the leaderboard controller with a score manager and leaderboard UI.
     */
    public LeaderboardController(ScoreManager scoreManager, LeaderboardPanel leaderboardPanel) {
        this.scoreManager = scoreManager;
        this.leaderboardPanel = leaderboardPanel;
    }

    /**
     * Retrieves the top scores and updates the leaderboard UI.
     */
    public void updateLeaderboard() {
        leaderboardPanel.updateScores(getTopScores());
    }

    /**
     * Fetches the top scores from the score manager.
     * @return Top scores as a 2D object array.
     */
    private Object[][] getTopScores() {
        return scoreManager.getTopScores(5);
    }
}
