package model;

import view.SnakePanel;
import controller.SnakeGameController;

public class ButtonPanelModel {
    private final SnakeGameLogic gameLogic;
    private final SnakePanel snakePanel;
    private SnakeGameController gameController;
    private String username;

    public ButtonPanelModel(SnakeGameLogic gameLogic, SnakePanel snakePanel) {
        if (gameLogic == null || snakePanel == null) {
            throw new IllegalArgumentException("GameLogic and SnakePanel cannot be null");
        }
        this.gameLogic = gameLogic;
        this.snakePanel = snakePanel;
        this.gameController = snakePanel.getGameController();
        this.username = SessionManager.getUsername();
    }

    public void updateUsername(String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = newUsername.trim();
    }

    public String getUsername() {
        return username;
    }

    public SnakeGameLogic getGameLogic() {
        return gameLogic;
    }

    public SnakePanel getSnakePanel() {
        return snakePanel;
    }

    public void startGame() {
        if (!isGameStarted()) {
            snakePanel.startGame();
            updateGameController();
        }
    }

    public void pauseGame() {
        if (isGameStarted()) {
            updateGameController();
            if (gameController != null) {
                gameController.pauseGame();
            }
        }
    }

    public void resumeGame() {
        if (isGameStarted() && isGamePaused()) {
            updateGameController();
            if (gameController != null) {
                gameController.pauseGame(); // Toggle pause state to resume
            }
        }
    }

    public void resetGame() {
        if (isGameStarted()) {
            updateGameController();
            if (gameController != null) {
                gameController.stopGame();
            }
        }
        gameLogic.reset();
        snakePanel.resetToStart();
        updateGameController();
    }

    public void stopGame() {
        if (isGameStarted()) {
            updateGameController();
            if (gameController != null) {
                gameController.stopGame();
            }
        }
    }

    public boolean isGameStarted() {
        return snakePanel != null && snakePanel.isGameStarted();
    }

    public boolean isGamePaused() {
        if (!isGameStarted()) {
            return false;
        }
        updateGameController();
        return gameController != null && gameController.isPaused();
    }

    private void updateGameController() {
        if (snakePanel != null) {
            SnakeGameController currentController = snakePanel.getGameController();
            if (currentController != null) {
                gameController = currentController;
            }
        }
    }
} 