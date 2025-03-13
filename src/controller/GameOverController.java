package controller;

import view.GameOverPanel;
import view.GameMainInterface;
import view.LoginUI;
import model.GameOverModel;
import api.APIClient;
import org.json.JSONObject;
import javax.swing.SwingUtilities;

public class GameOverController {
    private final GameOverPanel view;
    private final GameOverModel model;

    public GameOverController(GameOverPanel view) {
        this.view = view;
        this.model = new GameOverModel();
    }

    public void setGameResults(int finalScore, int highScore, String playerName) {
        model.setGameResults(finalScore, highScore, playerName);
        view.updateDisplay();
    }

    public void saveGameResult() {
        if (model.isHighScore()) {
            try {
                String response = APIClient.updateHighScore(model.getFinalScore());
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.getString("status").equals("success")) {
                    model.setGameSaved(true);
                    view.showMessage("New high score saved!", true);
                } else {
                    view.showMessage("Failed to save high score: " + jsonResponse.getString("message"), false);
                }
            } catch (Exception e) {
                view.showMessage("Error saving high score: " + e.getMessage(), false);
                e.printStackTrace();
            }
        }
    }

    public void resetGame() {
        model.reset();
        view.updateDisplay();
    }

    public GameOverModel getModel() {
        return model;
    }

    public void dispose() {
        model.reset();
    }

    public void updateHighScore() {
        try {
            String response = APIClient.getBestScore();
            JSONObject jsonResponse = new JSONObject(response);
            
            if (jsonResponse.getString("status").equals("success")) {
                int highScore = jsonResponse.getInt("best_score");
                model.setHighScore(highScore);
                view.updateDisplay();
            }
        } catch (Exception e) {
            System.err.println("Error fetching high score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveScore() {
        try {
            String response = APIClient.updateHighScore(model.getFinalScore());
            JSONObject jsonResponse = new JSONObject(response);
            
            if (jsonResponse.getString("status").equals("success")) {
                view.showMessage("Score saved successfully!", true);
            } else {
                view.showMessage("Failed to save score: " + jsonResponse.getString("message"), false);
            }
        } catch (Exception e) {
            view.showMessage("Error saving score: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    public void restartGame() {
        view.dispose();
        SwingUtilities.invokeLater(() -> {
            GameMainInterface gameUI = new GameMainInterface();
            gameUI.setVisible(true);
        });
    }

    public void returnToMainMenu() {
        view.dispose();
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
} 