package model;

/**
 * Model class for managing timer state in the game.
 */
public class TimerPanelModel {
    private int timeRemaining;
    private boolean isRunning;
    private static final int INITIAL_TIME = 30; // 30 seconds per question

    public TimerPanelModel() {
        resetTimer();
    }

    public void resetTimer() {
        timeRemaining = INITIAL_TIME;
        isRunning = false;
    }

    public void startTimer() {
        isRunning = true;
    }

    public void stopTimer() {
        isRunning = false;
    }

    public void decrementTime() {
        if (isRunning && timeRemaining > 0) {
            timeRemaining--;
        }
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }
} 