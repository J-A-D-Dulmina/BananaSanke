package controller;

import model.SnakeGameLogic;
import view.SnakePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Controls the game flow by managing updates and user interactions.
 */
public class SankeGameController implements ActionListener { // ✅ Corrected class name
    private final SnakeGameLogic gameLogic;
    private final SnakePanel snakePanel;
    private Timer gameTimer;

    /**
     * Initializes the game controller with logic and UI components.
     */
    public SankeGameController(SnakeGameLogic gameLogic, SnakePanel snakePanel) { // ✅ Corrected constructor
        this.gameLogic = gameLogic;
        this.snakePanel = snakePanel;
        gameTimer = new Timer(100, this); // Timer triggers every 100ms
    }

    /**
     * Starts the game by updating logic and running the timer.
     */
    public void startGame() {
        gameTimer.stop(); // Stop any existing timer
        gameLogic.updateGame(); // Ensure game state updates
        gameTimer.start();
    }

    /**
     * Stops the game timer.
     */
    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    /**
     * Handles game updates on timer events.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        gameLogic.updateGame();
        snakePanel.repaint();
    }

    /**
     * Changes the snake's direction based on user input.
     */
    public void changeDirection(String direction) {
        gameLogic.changeDirection(direction);
    }
    
    public void resetGame() {
        System.out.println("Game resetting...");

        stopGame();  
        gameLogic.reset();  

        // ✅ Restart the game timer
        SwingUtilities.invokeLater(() -> {
            snakePanel.repaint();
            startGame();
        });

        System.out.println("Restarting game...");
    }



}
