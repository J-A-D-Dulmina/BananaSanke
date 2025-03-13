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
        
        if (model.isLastAttempt()) {
            view.updateHealthLabel("FINAL CHANCE!");
        }
        
        if (!stillAlive) {
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