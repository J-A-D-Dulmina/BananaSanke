package controller;

import view.GameOverPanel;
import view.GameMainInterface;
import view.LoginUI;
import model.GameOverModel;
import org.json.JSONObject;
import javax.swing.SwingUtilities;
import factory.ComponentFactory;
import interfaces.IAPIClient;
import interfaces.IGameOverController;
import interfaces.IGameOverModel;

public class GameOverController implements IGameOverController {
    private final GameOverPanel view;
    private final IGameOverModel model;
    private final IAPIClient apiClient;

    public GameOverController(GameOverPanel view) {
        this.view = view;
        this.model = new GameOverModel();
        this.apiClient = ComponentFactory.getAPIClient();
    }

    @Override
    public void setGameResults(int finalScore, int highScore, String playerName) {
        model.setGameResults(finalScore, highScore, playerName);
        view.updateDisplay();
    }

    @Override
    public void saveGameResult() {
        if (model.isHighScore()) {
            try {
                String response = apiClient.updateHighScore(model.getFinalScore());
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

    @Override
    public void resetGame() {
        model.reset();
        view.updateDisplay();
    }

    @Override
    public IGameOverModel getModel() {
        return model;
    }

    @Override
    public void dispose() {
        model.reset();
    }

    @Override
    public void updateHighScore() {
        try {
            String response = apiClient.getBestScore();
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

    @Override
    public void saveScore() {
        try {
            String response = apiClient.updateHighScore(model.getFinalScore());
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

    @Override
    public void restartGame() {
        view.dispose();
        SwingUtilities.invokeLater(() -> {
            GameMainInterface gameUI = new GameMainInterface();
            gameUI.setVisible(true);
        });
    }

    @Override
    public void returnToMainMenu() {
        view.dispose();
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
} 