package controller;

import model.GameEngine;
import model.Game;
import java.net.URL;

/**
 * Controls game logic and interactions between the model and the view.
 */
public class GameController {
    private final GameEngine gameEngine;

    public GameController() {
        this.gameEngine = new GameEngine();
    }

    /**
     * Retrieves the next game puzzle.
     * @return The game image URL.
     */
    public URL getNextGame() {
        return gameEngine.nextGame();
    }
    
    /**
     * Retrieves the correct answer for the current game.
     * @return The correct answer.
     */
    public int getCorrectAnswer() {
        return gameEngine.getCurrentGameSolution(); // Calls the method in GameEngine
    }

    /**
     * Gets the current game instance.
     * @return The current Game object.
     */
    public Game getCurrentGame() {
        return gameEngine.getCurrentGame();
    }

    /**
     * Checks the user's answer.
     * @param answer User's input.
     * @return True if correct, otherwise false.
     */
    public boolean checkAnswer(int answer) {
        return gameEngine.checkSolution(answer);
    }

    /**
     * Retrieves the current score.
     * @return The game score.
     */
    public int getScore() {
        return gameEngine.getScore();
    }
}
