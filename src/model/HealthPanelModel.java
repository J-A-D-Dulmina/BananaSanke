package model;

/**
 * Model class for managing health state in the game.
 */
public class HealthPanelModel {
    private int currentHealth;
    private int wrongAttempts;
    private static final int MAX_HEALTH = 3;
    private static final int MAX_ATTEMPTS = 4;

    public HealthPanelModel() {
        resetHealth();
    }

    public void resetHealth() {
        currentHealth = MAX_HEALTH;
        wrongAttempts = 0;
    }

    public boolean loseHealth() {
        wrongAttempts++;
        
        if (wrongAttempts <= MAX_HEALTH) {
            currentHealth--;
            return true;
        } else if (wrongAttempts == MAX_ATTEMPTS) {
            currentHealth = 0;
            return false;
        }
        
        return false;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getWrongAttempts() {
        return wrongAttempts;
    }

    public boolean isLastAttempt() {
        return wrongAttempts == MAX_HEALTH;
    }

    public boolean isGameOver() {
        return currentHealth <= 0;
    }
} 