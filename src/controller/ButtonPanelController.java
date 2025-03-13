package controller;

import model.ButtonPanelModel;
import view.ButtonPanel;
import view.GameMainInterface;
import view.LoginUI;
import view.SettingsPanel;
import view.AccountPanel;
import utils.CustomDialogUtils;
import javax.swing.JOptionPane;
import org.json.JSONObject;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import api.APIClient;

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
            model.pauseGame();
            if (model.isGamePaused()) {
                view.setPlayPauseIcon("play");
                view.showPauseOverlay();
            } else {
                view.setPlayPauseIcon("pause");
                view.hidePauseOverlay();
            }
        }
    }

    public void handleReset() {
        int confirm = CustomDialogUtils.showConfirmDialog(
            view,
            "Are you sure you want to reset the game?",
            "Confirm Reset"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            model.resetGame();
            view.requestFocusInWindow();
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
                // Stop any ongoing game
                model.stopGame();
                
                // Attempt to logout
                String response = APIClient.logoutUser();
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.getString("status").equals("success")) {
                    mainFrame.dispose();
                    new LoginUI().setVisible(true);
                } else {
                    String errorMessage = jsonResponse.optString("message", "Unknown error during logout");
                    CustomDialogUtils.showErrorDialog(
                        mainFrame,
                        "Error during logout: " + errorMessage,
                        "Logout Error"
                    );
                }
            } catch (Exception e) {
                CustomDialogUtils.showErrorDialog(
                    mainFrame,
                    "Error during logout: " + e.getMessage(),
                    "Logout Error"
                );
            }
        }
    }

    public void handleSettings() {
        boolean wasRunning = model.isGameStarted();
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
        boolean wasRunning = model.isGameStarted();
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
        boolean wasRunning = model.isGameStarted();
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