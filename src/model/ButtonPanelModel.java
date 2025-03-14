package model;

import view.SnakePanel;
import interfaces.IGameController;
import interfaces.IGameLogic;
import interfaces.IButtonPanelModel;
import interfaces.ISessionManager;

public class ButtonPanelModel implements IButtonPanelModel {
    private final IGameLogic gameLogic;
    private final SnakePanel snakePanel;
    private IGameController gameController;
    private String username;
    private final ISessionManager sessionManager;

    public ButtonPanelModel(SnakeGameLogic gameLogic, SnakePanel snakePanel) {
        if (gameLogic == null || snakePanel == null) {
            throw new IllegalArgumentException("GameLogic and SnakePanel cannot be null");
        }
        this.gameLogic = gameLogic;
        this.snakePanel = snakePanel;
        this.gameController = snakePanel.getGameController();
        this.sessionManager = SessionManagerImpl.getInstance();
        this.username = sessionManager.getUsername();
    }

    @Override
    public void updateUsername(String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = newUsername.trim();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public IGameLogic getGameLogic() {
        return gameLogic;
    }

    @Override
    public SnakePanel getSnakePanel() {
        return snakePanel;
    }

    @Override
    public void startGame() {
        if (!isGameStarted()) {
            if (sessionManager.getAuthToken() != null && !sessionManager.getAuthToken().isEmpty()) {
                snakePanel.startGame();
                updateGameController();
            } else {
                throw new IllegalStateException("No valid API session found");
            }
        }
    }

    @Override
    public void pauseGame() {
        updateGameController();
        if (gameController != null && isGameStarted()) {
            gameController.pauseGame();
            gameLogic.setRunning(false);
        }
    }

    @Override
    public void resumeGame() {
        updateGameController();
        if (gameController != null && isGameStarted() && gameController.isPaused()) {
            gameController.pauseGame(); // Toggle pause state
            gameLogic.setRunning(true);
        }
    }

    @Override
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
            // This will handle showing the start overlay
            snakePanel.resetToStart();
        }
    }

    @Override
    public void stopGame() {
        updateGameController();
        if (gameController != null) {
            gameController.stopGame();
            gameLogic.setRunning(false);
        }
    }

    @Override
    public boolean isGameStarted() {
        return snakePanel != null && snakePanel.isGameStarted();
    }

    @Override
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