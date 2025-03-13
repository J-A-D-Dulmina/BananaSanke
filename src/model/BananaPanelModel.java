package model;

import java.awt.Point;
import java.util.Random;

public class BananaPanelModel {
    private Point position;
    private int score;
    private boolean isVisible;
    private Random random;
    private int panelWidth;
    private int panelHeight;
    private int bananaSize;

    public BananaPanelModel() {
        this.random = new Random();
        this.score = 0;
        this.isVisible = false;
        this.bananaSize = 30; // Default size
        this.position = new Point(0, 0);
    }

    public void setPanelDimensions(int width, int height) {
        this.panelWidth = width;
        this.panelHeight = height;
    }

    public void generateNewPosition() {
        if (panelWidth > 0 && panelHeight > 0) {
            int x = random.nextInt(panelWidth - bananaSize);
            int y = random.nextInt(panelHeight - bananaSize);
            position.setLocation(x, y);
            isVisible = true;
        }
    }

    public void hideBanana() {
        isVisible = false;
    }

    public void incrementScore() {
        score++;
    }

    public boolean isCollision(Point snakeHead) {
        if (!isVisible) return false;
        
        int distance = (int) Math.sqrt(
            Math.pow(snakeHead.x - position.x, 2) + 
            Math.pow(snakeHead.y - position.y, 2)
        );
        
        return distance < bananaSize;
    }

    public Point getPosition() {
        return position;
    }

    public int getScore() {
        return score;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int getBananaSize() {
        return bananaSize;
    }

    public void setBananaSize(int size) {
        this.bananaSize = size;
    }

    public void reset() {
        score = 0;
        isVisible = false;
        position.setLocation(0, 0);
    }
} 