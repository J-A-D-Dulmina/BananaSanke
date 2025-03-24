package controller;

import model.HealthPanelModel;
import view.HealthPanel;

/**
 * Controller class for managing health panel interactions.
 */
public class HealthPanelController {
    private final HealthPanelModel model;
    private final HealthPanel view;

    public HealthPanelController(HealthPanelModel model, HealthPanel view) {
        this.model = model;
        this.view = view;
    }

    public void handleHealthLoss() {
        boolean stillAlive = model.loseHealth();
        view.updateHealthDisplay(model.getCurrentHealth());
        
        // Show LAST CHANCE message when all hearts are gone (after 3rd hit)
        if (model.getWrongAttempts() == 3) {
            view.updateHealthLabel("LAST CHANCE!");
            // No dialog box, just update the label
        }
        
        // Actually end the game after 4th hit
        if (!stillAlive) {
            view.updateHealthLabel("GAME OVER!");
            view.handleGameOver();
        }
    }

    public void handleReset() {
        model.resetHealth();
        view.resetHealthDisplay();
    }

    public int getCurrentHealth() {
        return model.getCurrentHealth();
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }
} 