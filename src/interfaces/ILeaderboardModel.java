package interfaces;

import java.util.List;
import java.util.Optional;
import controller.LeaderboardController.LeaderboardEntry;

/**
 * Interface for the LeaderboardModel that manages leaderboard data
 */
public interface ILeaderboardModel {
    
    /**
     * Fetches the leaderboard data from the server
     * 
     * @return a list of leaderboard entries
     * @throws Exception if an error occurs during the fetch
     */
    List<LeaderboardEntry> fetchLeaderboard() throws Exception;
    
    /**
     * Gets the user's rank
     * 
     * @return the user's rank
     */
    int getUserRank();
    
    /**
     * Gets the user's score
     * 
     * @return the user's score
     */
    int getUserScore();
    
    /**
     * Gets the total number of players
     * 
     * @return the total number of players
     */
    int getTotalPlayers();
    
    /**
     * Gets the last error message
     * 
     * @return the last error message
     */
    String getLastError();
    
    /**
     * Gets the leaderboard entries
     * 
     * @return a list of leaderboard entries
     */
    List<LeaderboardEntry> getEntries();
    
    /**
     * Gets the highest score in the leaderboard
     * 
     * @return the highest score
     */
    int getHighestScore();
    
    /**
     * Gets a leaderboard entry by username
     * 
     * @param username the username to search for
     * @return an optional containing the leaderboard entry if found
     */
    Optional<LeaderboardEntry> getEntryByUsername(String username);
    
    /**
     * Gets all scores in the leaderboard
     * 
     * @return a list of leaderboard entries
     */
    List<LeaderboardEntry> getScores();
} 