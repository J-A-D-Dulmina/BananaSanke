package controller;

import model.ButtonPanelModel;
import view.ButtonPanel;
import view.GameMainInterface;
import view.LoginUI;
import view.SettingsPanel;
import view.AccountPanel;
import utils.CustomDialogUtils;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.json.JSONObject;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import api.APIClient;
import model.SessionManager;
import model.SoundManager;

public class ButtonPanelController {
    private final ButtonPanelModel model;
    private final ButtonPanel view;
    private final GameMainInterface mainFrame;

    public ButtonPanelController(ButtonPanelModel model, ButtonPanel view, GameMainInterface mainFrame) {
        this.model = model;
        this.view = view;
        this.mainFrame = mainFrame;
    }

    public ButtonPanelModel getModel() {
        return model;
    }

    public void handlePlayPause() {
        if (!model.isGameStarted()) {
            model.startGame();
            view.setPlayPauseIcon("pause");
        } else {
            if (model.isGamePaused()) {
                model.resumeGame();
                view.setPlayPauseIcon("pause");
                view.hidePauseOverlay();
            } else {
                model.pauseGame();
                view.setPlayPauseIcon("play");
                view.showPauseOverlay();
            }
        }
        view.requestFocusInWindow();
    }

    public void handleReset() {
        int confirm = CustomDialogUtils.showConfirmDialog(
            view,
            "Are you sure you want to reset the game?",
            "Confirm Reset"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // First stop any running sounds
                SoundManager.getInstance().stopRunningSound();
                
                // Stop the current game completely
                model.stopGame();
                
                // Reset the game state and model
                model.resetGame();
                
                // Reset to initial start state
                model.getSnakePanel().resetToStart();
                
                // Update UI elements
                view.setPlayPauseIcon("play");
                view.hidePauseOverlay();
                
                // Request focus back to the game panel
                view.requestFocusInWindow();
            } catch (Exception e) {
                CustomDialogUtils.showErrorDialog(
                    mainFrame,
                    "Error resetting game: " + e.getMessage(),
                    "Reset Error"
                );
            }
        }
    }

    public void handleLogout() {
        int confirm = CustomDialogUtils.showConfirmDialog(
            mainFrame,
            "Are you sure you want to logout?",
            "Confirm Logout"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // First stop sounds
                SoundManager soundManager = SoundManager.getInstance();
                soundManager.stopBackgroundMusic();
                soundManager.stopRunningSound();

                // Then cleanup game state
                cleanupGameState();
                
                // Make the API call to logout
                String response = APIClient.logoutUser();
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.getString("status").equals("success")) {
                    // Only clear session after successful server logout
                    SessionManager.logout();
                    
                    // Finally close UI and show login
                    SwingUtilities.invokeLater(() -> {
                        try {
                            mainFrame.dispose();
                            new LoginUI().setVisible(true);
                        } catch (Exception e) {
                            System.err.println("Error showing login window: " + e.getMessage());
                            System.exit(0);
                        }
                    });
                } else {
                    String errorMessage = jsonResponse.optString("message", "Unknown error during logout");
                    CustomDialogUtils.showErrorDialog(
                        mainFrame,
                        "Error during logout: " + errorMessage,
                        "Logout Error"
                    );
                    
                    // Restore game state if server logout failed
                    if (!model.isGameStarted()) {
                        model.startGame();
                    }
                }
            } catch (Exception e) {
                CustomDialogUtils.showErrorDialog(
                    mainFrame,
                    "Error during logout: " + e.getMessage(),
                    "Logout Error"
                );
                
                // Restore game state if there was an error
                if (!model.isGameStarted()) {
                    model.startGame();
                }
            }
        }
    }

    private void cleanupGameState() {
        try {
            // Stop the game if it's running
            if (model.isGameStarted()) {
                model.stopGame();
            }
            
            // Reset game state
            model.resetGame();
            
            // Clear UI elements
            view.setPlayPauseIcon("play");
            view.hidePauseOverlay();
        } catch (Exception e) {
            System.err.println("Error during game state cleanup: " + e.getMessage());
        }
    }

    public void handleSettings() {
        boolean wasRunning = model.isGameStarted() && !model.isGamePaused();
        if (wasRunning) {
            model.pauseGame();
            view.setPlayPauseIcon("play");
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

    public void handleAccount() {
        boolean wasRunning = model.isGameStarted() && !model.isGamePaused();
        if (wasRunning) {
            model.pauseGame();
            view.setPlayPauseIcon("play");
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

    public void handleLeaderboard() {
        boolean wasRunning = model.isGameStarted() && !model.isGamePaused();
        if (wasRunning) {
            model.pauseGame();
            view.setPlayPauseIcon("play");
            view.showPauseOverlay();
        }
        mainFrame.showLeaderboard();
        view.requestFocusInWindow();
    }

    public void handlePauseOverlayClick() {
        if (model.isGamePaused()) {
            model.resumeGame();
            view.setPlayPauseIcon("pause");
            view.hidePauseOverlay();
            view.requestFocusInWindow();
        }
    }

    public void updateUsername(String newUsername) {
        model.updateUsername(newUsername);
        view.updateUsernameDisplay(newUsername);
    }
} 