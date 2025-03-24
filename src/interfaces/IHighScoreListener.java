package interfaces;

/**
 * Interface for objects that want to listen for high score events
 */
public interface IHighScoreListener {
    
    /**
     * Called when a new high score is achieved
     * @param score The new high score value
     */
    void onNewHighScore(int score);
} 