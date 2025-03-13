package model;

import java.net.MalformedURLException;
import java.net.URL;
import api.BananaAPI;

/**
 * GameEngine handles game logic and fetching puzzles from the API.
 */
public class GameEngine {
    private int score = 0;
    private Game currentGame = null;
    private final BananaAPI bananaAPI;

    public GameEngine() {
        this.bananaAPI = new BananaAPI();
    }
    
    /**
     * Returns the correct answer for the current game.
     * @return The correct answer.
     */
    public int getCurrentGameSolution() {
        return (currentGame != null) ? currentGame.getSolution() : -1;
    }

    /**
     * Gets the current game instance.
     * @return The current Game object.
     */
    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Fetches the next game puzzle.
     * @return URL of the new puzzle image.
     */
    public URL nextGame() {
        try {
            currentGame = bananaAPI.getRandomGame();
            return currentGame.getLocation();
        } catch (MalformedURLException e) {
            System.err.println("Error retrieving the game image: " + e.getMessage());
            return null;
        }
    }
    

    /**
     * Validates the user's answer.
     * @param answer The user's input.
     * @return True if correct, otherwise false.
     */
    public boolean checkSolution(int answer) {
        if (currentGame != null && answer == currentGame.getSolution()) {
            score++;
            return true;
        }
        return false;
    }

    /**
     * Retrieves the current game score.
     * @return The score.
     */
    public int getScore() {
        return score;
    }
}
