package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import api.BananaAPI;
import view.APISection;

public class SnakeGameLogic {
    private final int tileSize;
    private final int panelWidth;
    private final int panelHeight;
    private final List<Point> snake;
    private Food food;
    private String direction;
    private boolean running;
    private int score;
    private boolean waitingForAnswer;

    public SnakeGameLogic(int panelWidth, int panelHeight, int tileSize) {
        this.tileSize = tileSize;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.snake = new ArrayList<>();
        this.direction = "RIGHT";
        this.running = true;
        this.score = 0;
        this.waitingForAnswer = false;

        // Initialize snake with 3 segments
        snake.add(new Point(100, 100));
        snake.add(new Point(80, 100));
        snake.add(new Point(60, 100));

        food = new Food(panelWidth, panelHeight, tileSize);
        
        // Wait briefly for APISection to initialize
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Set initial game from APISection
        if (APISection.getInstance() != null) {
            Game currentGame = APISection.getInstance().getCurrentGame();
            if (currentGame != null) {
                System.out.println("API Response: " + currentGame.getLocation() + ", Answer: " + currentGame.getSolution());
            }
            food.setCurrentGame(currentGame);
        }
        food.spawnFood(snake);
    }

    public void updateGame() {
        if (!running) return;

        Point head = snake.get(0);
        Point newHead = new Point(head);

        // Move the snake in the current direction
        switch (direction) {
            case "UP":
                newHead.y -= tileSize;
                break;
            case "DOWN":
                newHead.y += tileSize;
                break;
            case "LEFT":
                newHead.x -= tileSize;
                break;
            case "RIGHT":
                newHead.x += tileSize;
                break;
        }

        // Wrap-around screen logic
        if (newHead.x < 0) newHead.x = panelWidth - tileSize;
        if (newHead.x >= panelWidth) newHead.x = 0;
        if (newHead.y < 0) newHead.y = panelHeight - tileSize;
        if (newHead.y >= panelHeight) newHead.y = 0;

        // Check if the snake eats either of the two food items
        boolean ateFirstFood = newHead.equals(food.getFirstFoodPosition());
        boolean ateSecondFood = newHead.equals(food.getSecondFoodPosition());

        if (ateFirstFood || ateSecondFood) {
            // Get the current API answer
            int correctAnswer = APISection.getInstance().getCorrectAnswer();
            
            // Check if we ate the food with the correct answer
            if (ateFirstFood && food.getFirstFoodNumber() == correctAnswer) {
                // Load next question from APISection
                APISection.getInstance().loadNextQuestion();
                Game newGame = APISection.getInstance().getCurrentGame();
                if (newGame != null) {
                    System.out.println("API Response: " + newGame.getLocation() + ", Answer: " + newGame.getSolution());
                }
                food.setCurrentGame(newGame);
                score++;
            } else if (ateSecondFood && food.getSecondFoodNumber() == correctAnswer) {
                // Load next question from APISection
                APISection.getInstance().loadNextQuestion();
                Game newGame = APISection.getInstance().getCurrentGame();
                if (newGame != null) {
                    System.out.println("API Response: " + newGame.getLocation() + ", Answer: " + newGame.getSolution());
                }
                food.setCurrentGame(newGame);
                score++;
            }
            
            // Spawn new food regardless of whether the answer was correct
            food.spawnFood(snake);
        } else {
            snake.remove(snake.size() - 1);
        }

        if (snake.contains(newHead)) {
            running = false;
        } else {
            snake.add(0, newHead);
        }
    }

    public int getScore() {
        return score;
    }

    public boolean isRunning() {
        return running;
    }

    public List<Point> getFoodPositions() {
        List<Point> foodPositions = new ArrayList<>();
        foodPositions.add(food.getFirstFoodPosition());
        foodPositions.add(food.getSecondFoodPosition());
        return foodPositions;
    }

    public int getFirstFoodNumber() {
        return food.getFirstFoodNumber();
    }

    public int getSecondFoodNumber() {
        return food.getSecondFoodNumber();
    }

    public List<Point> getSnakePositions() {
        return new ArrayList<>(snake);
    }
    
    public void changeDirection(String newDirection) {
        if (!isOppositeDirection(newDirection)) {
            direction = newDirection;
        }
    }

    private boolean isOppositeDirection(String newDirection) {
        return (direction.equals("LEFT") && newDirection.equals("RIGHT")) ||
               (direction.equals("RIGHT") && newDirection.equals("LEFT")) ||
               (direction.equals("UP") && newDirection.equals("DOWN")) ||
               (direction.equals("DOWN") && newDirection.equals("UP"));
    }

    public String getDirection() {
        return direction;
    }
    
    public void reset() {
        System.out.println("Resetting game...");

        snake.clear();  
        direction = "RIGHT";  
        running = true;
        score = 0;
        waitingForAnswer = false;

        // Reset snake to original starting position
        snake.add(new Point(100, 100));
        snake.add(new Point(80, 100));
        snake.add(new Point(60, 100));

        // Get new puzzle from APISection
        APISection.getInstance().loadNextQuestion();
        Game newGame = APISection.getInstance().getCurrentGame();
        if (newGame != null) {
            System.out.println("API Response: " + newGame.getLocation() + ", Answer: " + newGame.getSolution());
        }
        food.setCurrentGame(newGame);
        food.spawnFood(snake);

        System.out.println("Snake size after reset: " + snake.size());
    }

    public boolean isWaitingForAnswer() {
        return waitingForAnswer;
    }
}
