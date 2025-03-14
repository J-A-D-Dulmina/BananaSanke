package model;

import view.SnakePanel;
import controller.SnakeGameController;
import interfaces.IGameController;
import interfaces.IGameLogic;

public class ButtonPanelModel {
    private final IGameLogic gameLogic;
    private final SnakePanel snakePanel;
    private IGameController gameController;
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

    public IGameLogic getGameLogic() {
        return gameLogic;
    }

    public SnakePanel getSnakePanel() {
        return snakePanel;
    }

    public void startGame() {
        if (!isGameStarted()) {
            if (SessionManager.getAuthToken() != null && !SessionManager.getAuthToken().isEmpty()) {
                snakePanel.startGame();
                updateGameController();
            } else {
                throw new IllegalStateException("No valid API session found");
            }
        }
    }

    public void pauseGame() {
        updateGameController();
        if (gameController != null && isGameStarted()) {
            gameController.pauseGame();
            gameLogic.setRunning(false);
        }
    }

    public void resumeGame() {
        updateGameController();
        if (gameController != null && isGameStarted() && gameController.isPaused()) {
            gameController.pauseGame(); // Toggle pause state
            gameLogic.setRunning(true);
        }
    }

    public void resetGame() {
        updateGameController();
        if (gameController != null) {
            // First stop the game
            gameController.stopGame();
            gameLogic.setRunning(false);
            
            // Then reset everything
            gameController.resetGame();
            gameLogic.reset();
            
            // Ensure snake panel is in start state
            snakePanel.resetToStart();
        }
    }

    public void stopGame() {
        updateGameController();
        if (gameController != null) {
            gameController.stopGame();
            gameLogic.setRunning(false);
        }
    }

    public boolean isGameStarted() {
        return snakePanel != null && snakePanel.isGameStarted();
    }

    public boolean isGamePaused() {
        updateGameController();
        return gameController != null && gameController.isPaused();
    }

    private void updateGameController() {
        if (snakePanel != null) {
            IGameController currentController = snakePanel.getGameController();
            if (currentController != null) {
                gameController = currentController;
            }
        }
    }
} 