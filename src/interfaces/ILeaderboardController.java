package interfaces;

/**
 * Interface for the LeaderboardController that manages leaderboard functionality
 */
public interface ILeaderboardController {
    
    /**
     * Updates the leaderboard data and refreshes the view
     */
    void updateLeaderboard();
    
    /**
     * Updates the user interface with leaderboard data
     */
    void updateUI();
    
    /**
     * Cleans up resources when the leaderboard is closed
     */
    void onClose();
} 