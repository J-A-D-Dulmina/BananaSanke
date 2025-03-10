package model;

/**
 * Manages score retrieval. Currently returns mock data.
 */
public class ScoreManager {
    
    /**
     * Retrieves the top scores. This is currently a placeholder until a database is connected.
     * @param limit The number of top scores to retrieve.
     * @return A 2D array containing rank, player name, and score.
     */
    public Object[][] getTopScores(int limit) {
        return new Object[][] {
            {1, "Player1", 1500},
            {2, "Player2", 1300},
            {3, "Player3", 1100},
            {4, "Player4", 900},
            {5, "Player5", 750}
        };
    }
}
