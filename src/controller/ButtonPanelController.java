package controller;

import model.ButtonPanelModel;
import view.ButtonPanel;
import view.GameMainInterface;
import view.SettingsPanel;
import view.AccountPanel;
import utils.CustomDialogUtils;
import javax.swing.JOptionPane;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import interfaces.IButtonPanelController;
import interfaces.IButtonPanelModel;
import interfaces.ISoundManager;
import factory.ComponentFactory;

public class ButtonPanelController implements IButtonPanelController {
    private final IButtonPanelModel model;
    private final ButtonPanel view;
    private final GameMainInterface mainFrame;
    private final ISoundManager soundManager;
    public ButtonPanelController(ButtonPanelModel model, ButtonPanel view, GameMainInterface mainFrame) {
        this.model = model;
        this.view = view;
        this.mainFrame = mainFrame;
        this.soundManager = ComponentFactory.getSoundManager();
        ComponentFactory.getSessionManager();
        ComponentFactory.getAPIClient();
    }

    @Override
    public IButtonPanelModel getModel() {
        return model;
    }

    @Override
    public void handlePlayPause() {
        // This functionality has been removed
    }

    @Override
    public void handleReset() {
        int confirm = CustomDialogUtils.showConfirmDialog(
            view,
            "Are you sure you want to reset the game?",
            "Confirm Reset"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // First stop any running sounds
                soundManager.stopRunningSound();
                
                // Stop the current game completely
                model.stopGame();
                
                // Reset the game state and model
                model.resetGame();
                
                // Update UI elements
                view.hidePauseOverlay();
                
                // Request focus back to the game panel
                view.requestFocusInWindow();
            } catch (Exception e) {
                CustomDialogUtils.showErrorDialog(
                    mainFrame,
                    "Error resetting game: " + e.getMessage(),
                    "Reset Error"
                );
                System.err.println("Reset error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleLogout() {
        // This functionality has been removed
    }

    @Override
    public void handleSettings() {
        boolean wasRunning = model.isGameStarted() && !model.isGamePaused();
        if (wasRunning) {
            model.pauseGame();
            view.showPauseOverlay();
        }
        
        SettingsPanel settingsPanel = new SettingsPanel(mainFrame);
        settingsPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                view.requestFocusInWindow();
            }
        });
        settingsPanel.setVisible(true);
    }

    @Override
    public void handleAccount() {
        boolean wasRunning = model.isGameStarted() && !model.isGamePaused();
        if (wasRunning) {
            model.pauseGame();
            view.showPauseOverlay();
        }
        
        AccountPanel accountPanel = new AccountPanel(mainFrame);
        accountPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                view.requestFocusInWindow();
            }
        });
        accountPanel.setVisible(true);
    }

    @Override
    public void handleLeaderboard() {
        boolean wasRunning = model.isGameStarted() && !model.isGamePaused();
        if (wasRunning) {
            model.pauseGame();
            view.showPauseOverlay();
        }
        mainFrame.showLeaderboard();
        view.requestFocusInWindow();
    }

    @Override
    public void handlePauseOverlayClick() {
        if (model.isGamePaused()) {
            model.resumeGame();
            view.hidePauseOverlay();
            view.requestFocusInWindow();
        }
    }

    @Override
    public void updateUsername(String newUsername) {
        model.updateUsername(newUsername);
        view.updateUsernameDisplay(newUsername);
    }
} 