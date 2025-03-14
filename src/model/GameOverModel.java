package model;

import interfaces.IGameOverModel;

public class GameOverModel implements IGameOverModel {
    private int finalScore;
    private int highScore;
    private String playerName;
    private boolean isHighScore;
    private boolean isGameSaved;

    public GameOverModel() {
        this.finalScore = 0;
        this.highScore = 0;
        this.playerName = "";
        this.isHighScore = false;
        this.isGameSaved = false;
    }

    public void setGameResults(int finalScore, int highScore, String playerName) {
        this.finalScore = finalScore;
        this.highScore = highScore;
        this.playerName = playerName;
        this.isHighScore = finalScore > highScore;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
        this.isHighScore = finalScore > highScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isHighScore() {
        return isHighScore;
    }

    public void setHighScore(boolean highScore) {
        isHighScore = highScore;
    }

    public boolean isGameSaved() {
        return isGameSaved;
    }

    public void setGameSaved(boolean gameSaved) {
        isGameSaved = gameSaved;
    }

    public void reset() {
        this.finalScore = 0;
        this.highScore = 0;
        this.playerName = "";
        this.isHighScore = false;
        this.isGameSaved = false;
    }
} 