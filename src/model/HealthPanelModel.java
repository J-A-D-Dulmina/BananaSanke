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
        
        // First attempt: First heart disappears (currentHealth = 2)
        if (wrongAttempts == 1) {
            currentHealth = 2;
            return true;
        } 
        // Second attempt: First and second hearts disappear (currentHealth = 1)
        else if (wrongAttempts == 2) {
            currentHealth = 1;
            return true;
        }
        // Third attempt: All hearts disappear (currentHealth = 0)
        else if (wrongAttempts == 3) {
            currentHealth = 0;
            // Still alive, but no hearts
            return true;
        }
        // Fourth attempt: Game over
        else if (wrongAttempts == MAX_ATTEMPTS) {
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
        return wrongAttempts >= MAX_ATTEMPTS;
    }
} 