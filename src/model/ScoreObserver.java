package model;

/**
 * Interface for observing score changes in the game.
 */
public interface ScoreObserver {
    /**
     * Called when the score is updated.
     * @param newScore The new score value.
     */
    void onScoreUpdated(int newScore);
} 