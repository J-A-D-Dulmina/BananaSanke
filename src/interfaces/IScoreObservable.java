package interfaces;

import model.ScoreObserver;

/**
 * Interface for objects that can be observed for score changes.
 */
public interface IScoreObservable {
    /**
     * Adds an observer to be notified of score changes.
     * @param observer The observer to add.
     */
    void addObserver(ScoreObserver observer);

    /**
     * Removes an observer from the notification list.
     * @param observer The observer to remove.
     */
    void removeObserver(ScoreObserver observer);

    /**
     * Notifies all observers of a score change.
     * @param score The new score value.
     */
    void notifyObservers(int score);
} 