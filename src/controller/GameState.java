package controller;

/**
 * Manages the state of the snake game including score, pause state, and game over state.
 */
public class GameState {
    private int score;
    private boolean isPaused;
    private boolean isGameOver;

    public GameState() {
        reset();
    }

    public void reset() {
        score = 0;
        isPaused = false;
        isGameOver = false;
    }

    public void incrementScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
} 