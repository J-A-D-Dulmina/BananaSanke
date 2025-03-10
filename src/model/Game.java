package model;

import java.net.URL;

/**
 * Represents a game question with an image URL and correct solution.
 */
public class Game {
    private final URL location;
    private final int solution;

    public Game(URL location, int solution) {
        this.location = location;
        this.solution = solution;
    }

    public URL getLocation() {
        return location;
    }

    public int getSolution() {
        return solution;
    }
}
