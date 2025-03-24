package interfaces;

/**
 * Interface for notifying about high scores
 */
public interface IHighScoreNotifier {
    
    /**
     * Notify that a new high score has been achieved
     * @param score The new high score value
     */
    void notifyNewHighScore(int score);
    
    /**
     * Checks if a score is a high score and notifies if true
     * @param score The score to check
     * @return true if score is a high score
     */
    boolean checkAndNotifyHighScore(int score);
    
    /**
     * Adds a listener to be notified of high score events
     * @param listener The listener to add
     */
    void addHighScoreListener(IHighScoreListener listener);
    
    /**
     * Removes a high score listener
     * @param listener The listener to remove
     */
    void removeHighScoreListener(IHighScoreListener listener);
} 