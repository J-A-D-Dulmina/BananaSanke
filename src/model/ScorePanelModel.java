package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for managing the score state in the game.
 */
public class ScorePanelModel {
    private int score;
    private final List<ScoreObserver> observers;

    public ScorePanelModel() {
        this.score = 0;
        this.observers = new ArrayList<>();
    }

    /**
     * Adds an observer to be notified of score changes.
     * @param observer The observer to add.
     */
    public void addObserver(ScoreObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Removes an observer from the list.
     * @param observer The observer to remove.
     */
    public void removeObserver(ScoreObserver observer) {
        observers.remove(observer);
    }

    /**
     * Gets the current score.
     * @return The current score value.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score to a new value.
     * @param newScore The new score value.
     */
    public void setScore(int newScore) {
        this.score = newScore;
        notifyObservers();
    }

    /**
     * Increments the score by 1.
     */
    public void incrementScore() {
        this.score++;
        notifyObservers();
    }

    /**
     * Resets the score to 0.
     */
    public void resetScore() {
        this.score = 0;
        notifyObservers();
    }

    /**
     * Notifies all observers of the current score.
     */
    private void notifyObservers() {
        for (ScoreObserver observer : observers) {
            observer.onScoreUpdated(score);
        }
    }
} 