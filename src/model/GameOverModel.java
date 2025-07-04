package model;

import interfaces.IGameOverModel;

public class GameOverModel implements IGameOverModel {
    private int finalScore;
    private int highScore;
    private String playerName;
    private boolean isHighScore;
    private boolean isGameSaved;
    private boolean showHighScoreMessage;

    public GameOverModel() {
        this.finalScore = 0;
        this.highScore = 0;
        this.playerName = "";
        this.isHighScore = false;
        this.isGameSaved = false;
        this.showHighScoreMessage = false;
    }

    public void setGameResults(int finalScore, int highScore, String playerName) {
        System.out.println("GameOverModel: Setting game results with score " + finalScore);
        this.finalScore = finalScore;
        this.highScore = highScore;
        this.playerName = playerName;
        this.isHighScore = finalScore > highScore;
        
        // If it's a high score, automatically show the message
        if (this.isHighScore) {
            this.showHighScoreMessage = true;
        }
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        System.out.println("GameOverModel: Setting final score to " + finalScore);
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
    
    @Override
    public boolean shouldShowHighScoreMessage() {
        return showHighScoreMessage;
    }
    
    @Override
    public void setShouldShowHighScoreMessage(boolean show) {
        this.showHighScoreMessage = show;
    }

    public void reset() {
        this.finalScore = 0;
        this.highScore = 0;
        this.playerName = "";
        this.isHighScore = false;
        this.isGameSaved = false;
        this.showHighScoreMessage = false;
    }
} 