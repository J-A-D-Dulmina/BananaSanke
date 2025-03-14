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
import model.SessionManagerImpl;
import model.SoundManager;
import interfaces.IButtonPanelController;
import interfaces.IButtonPanelModel;
import interfaces.ISoundManager;
import interfaces.ISessionManager;

public class ButtonPanelController implements IButtonPanelController {
    private final IButtonPanelModel model;
    private final ButtonPanel view;
    private final GameMainInterface mainFrame;
    private final ISoundManager soundManager;
    private final ISessionManager sessionManager;

    public ButtonPanelController(ButtonPanelModel model, ButtonPanel view, GameMainInterface mainFrame) {
        this.model = model;
        this.view = view;
        this.mainFrame = mainFrame;
        this.soundManager = SoundManager.getInstance();
        this.sessionManager = SessionManagerImpl.getInstance();
    }

    @Override
    public IButtonPanelModel getModel() {
        return model;
    }

    @Override
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
                System.err.println("Reset error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleLogout() {
        int confirm = CustomDialogUtils.showConfirmDialog(
            mainFrame,
            "Are you sure you want to logout?",
            "Confirm Logout"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // First stop all sounds
                soundManager.stopAll();

                // Then cleanup game state
                cleanupGameState();
                
                // Make the API call to logout
                String response = APIClient.logoutUser();
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.getString("status").equals("success")) {
                    // Only clear session after successful server logout
                    sessionManager.logout();
                    
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void handlePauseOverlayClick() {
        if (model.isGamePaused()) {
            model.resumeGame();
            view.setPlayPauseIcon("pause");
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