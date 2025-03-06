package model;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Represents food in the snake game.
 * Handles food spawning and positioning.
 */
public class Food {
    private int x1, y1, x2, y2;
    private int number1, number2;
    private final int tileSize;
    private final int panelWidth;
    private final int panelHeight;
    private final Random random;

    /**
     * Initializes the food object with the game panel dimensions and tile size.
     * @param panelWidth Width of the game area.
     * @param panelHeight Height of the game area.
     * @param tileSize Size of each tile in the grid.
     */
    public Food(int panelWidth, int panelHeight, int tileSize) {
        this.tileSize = tileSize;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.random = new Random();
        spawnFood(null); // Spawn food initially
    }

    /**
     * Spawns two food items at random locations, ensuring they don't overlap with the snake or each other.
     * Each food is assigned a random number (0-9).
     * @param snake List of snake body segments to avoid overlap.
     */
    public void spawnFood(List<Point> snake) {
        boolean foodOnSnake;

        // Spawn first food
        do {
            x1 = random.nextInt(panelWidth / tileSize) * tileSize;
            y1 = random.nextInt(panelHeight / tileSize) * tileSize;
            foodOnSnake = (snake != null && snake.contains(new Point(x1, y1)));
        } while (foodOnSnake);

        // Assign a random number (0-9) to first food
        number1 = random.nextInt(10);

        // Spawn second food, ensuring it doesn't overlap with the first food or the snake
        do {
            x2 = random.nextInt(panelWidth / tileSize) * tileSize;
            y2 = random.nextInt(panelHeight / tileSize) * tileSize;
            foodOnSnake = (snake != null && snake.contains(new Point(x2, y2))) || (x1 == x2 && y1 == y2);
        } while (foodOnSnake);

        // Assign a random number (0-9) to second food
        number2 = random.nextInt(10);
    }

    public Point getFirstFoodPosition() {
        return new Point(x1, y1);
    }

    public int getFirstFoodNumber() {
        return number1;
    }

    public Point getSecondFoodPosition() {
        return new Point(x2, y2);
    }

    public int getSecondFoodNumber() {
        return number2;
    }
}
