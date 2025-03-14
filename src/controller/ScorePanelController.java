package controller;

import model.ScorePanelModel;
import model.ScoreObserver;
import view.ScorePanel;

/**
 * Controller class for managing score-related logic and interactions.
 */
public class ScorePanelController {
    private final ScorePanelModel model;
    public ScorePanelController(ScorePanel view) {
        this.model = new ScorePanelModel();
        // Register the view as an observer
        this.model.addObserver(view);
    }

    /**
     * Handles score increment when food is eaten.
     */
    public void handleFoodEaten() {
        model.incrementScore();
    }

    /**
     * Handles game reset by resetting the score.
     */
    public void handleGameReset() {
        model.resetScore();
    }

    /**
     * Gets the current score.
     * @return The current score value.
     */
    public int getCurrentScore() {
        return model.getScore();
    }

    /**
     * Sets the score to a new value.
     * @param newScore The new score value.
     */
    public void setScore(int newScore) {
        model.setScore(newScore);
    }

    /**
     * Adds an observer to be notified of score changes.
     * @param observer The observer to add.
     */
    public void addObserver(ScoreObserver observer) {
        model.addObserver(observer);
    }

    /**
     * Removes an observer from the list.
     * @param observer The observer to remove.
     */
    public void removeObserver(ScoreObserver observer) {
        model.removeObserver(observer);
    }
} 