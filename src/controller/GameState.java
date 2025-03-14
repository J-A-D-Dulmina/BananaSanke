package controller;

import interfaces.IGameState;

/**
 * Manages the state of the snake game including score, pause state, and game over state.
 */
public class GameState implements IGameState {
    private int score;
    private boolean isPaused;
    private boolean isGameOver;

    public GameState() {
        reset();
    }

    @Override
    public void reset() {
        score = 0;
        isPaused = false;
        isGameOver = false;
    }

    @Override
    public void incrementScore() {
        score++;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
} 