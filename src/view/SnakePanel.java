package view;

import controller.SankeGameController;
import model.SnakeGameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SnakePanel extends JPanel {
    private final SankeGameController gameController;
    private final SnakeGameLogic gameLogic;
    private boolean gameStarted = false;
    private JPanel startOverlay;
    private Image rightMouth, leftMouth, upMouth, downMouth, snakeBody, foodImage;

    public SnakePanel() {
        setLayout(null); // Use null layout for absolute positioning
        setBackground(Color.BLACK);
        gameLogic = new SnakeGameLogic(480, 500, 20);
        gameController = new SankeGameController(gameLogic, this);
        
        // Start in paused state
        gameLogic.setRunning(false);

        loadImages();
        setFocusable(true);
        requestFocusInWindow();
        setupKeyBindings();
        createStartOverlay();
    }

    private void createStartOverlay() {
        startOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 255)); // Solid black background
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw the "Click to Start" message
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                String message = "Click to Start the Game";
                FontMetrics fm = g.getFontMetrics();
                int messageWidth = fm.stringWidth(message);
                g.drawString(message, (getWidth() - messageWidth) / 2, getHeight() / 2);
            }
        };
        
        startOverlay.setOpaque(false);
        startOverlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameStarted) {
                    gameStarted = true;
                    gameLogic.setRunning(true);
                    gameController.startGame();
                    remove(startOverlay);
                    repaint();
                }
                requestFocusInWindow();
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Add the overlay once the component is added to its parent
        if (startOverlay.getParent() == null) {
            add(startOverlay);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Update overlay size when panel is resized
        if (startOverlay != null) {
            startOverlay.setBounds(0, 0, width, height);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Always fill the background with black first
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Always draw snake and food
        drawFood(g);
        drawSnake(g);
    }

    private void drawFood(Graphics g) {
        // Get food positions and numbers
        List<Point> foodPositions = gameLogic.getFoodPositions();
        int firstFoodNumber = gameLogic.getFirstFoodNumber();
        int secondFoodNumber = gameLogic.getSecondFoodNumber();

        // Draw food with numbers inside
        for (int i = 0; i < foodPositions.size(); i++) {
            Point foodPos = foodPositions.get(i);
            int foodNumber = (i == 0) ? firstFoodNumber : secondFoodNumber;

            g.drawImage(foodImage, foodPos.x, foodPos.y, 20, 20, this);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(String.valueOf(foodNumber), foodPos.x + 7, foodPos.y + 15);
        }
    }

    private void drawSnake(Graphics g) {
        List<Point> snake = gameLogic.getSnakePositions();
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            if (i == 0) { // Snake head
                switch (gameLogic.getDirection()) {
                    case "UP":
                        g.drawImage(upMouth, p.x, p.y, 20, 20, this);
                        break;
                    case "DOWN":
                        g.drawImage(downMouth, p.x, p.y, 20, 20, this);
                        break;
                    case "LEFT":
                        g.drawImage(leftMouth, p.x, p.y, 20, 20, this);
                        break;
                    case "RIGHT":
                        g.drawImage(rightMouth, p.x, p.y, 20, 20, this);
                        break;
                }
            } else { // Snake body
                g.drawImage(snakeBody, p.x, p.y, 20, 20, this);
            }
        }
    }

    public SnakeGameLogic getGameLogic() {
        return gameLogic;
    }

    private void loadImages() {
        String basePath = "resources/";
        rightMouth = new ImageIcon(basePath + "rightmouth.png").getImage();
        leftMouth = new ImageIcon(basePath + "leftmouth.png").getImage();
        upMouth = new ImageIcon(basePath + "upmouth.png").getImage();
        downMouth = new ImageIcon(basePath + "downmouth.png").getImage();
        snakeBody = new ImageIcon(basePath + "snakebody.png").getImage();
        foodImage = new ImageIcon(basePath + "food.png").getImage();
    }

    /**
     * ✅ Fix: This method correctly sets up key bindings to control the snake
     */
    private void setupKeyBindings() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "moveUp");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");

        getActionMap().put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.changeDirection("UP");
            }
        });

        getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.changeDirection("DOWN");
            }
        });

        getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.changeDirection("LEFT");
            }
        });

        getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.changeDirection("RIGHT");
            }
        });
    }

    public SankeGameController getGameController() {
        return gameController;
    }

    // Add method to reset the panel state
    public void resetToStart() {
        gameStarted = false;
        gameLogic.setRunning(false);
        
        // Remove existing overlay if it exists
        if (startOverlay != null && startOverlay.getParent() != null) {
            remove(startOverlay);
        }
        
        // Create and add new overlay
        createStartOverlay();
        startOverlay.setBounds(0, 0, getWidth(), getHeight());
        add(startOverlay);
        
        // Make sure the overlay is on top
        setComponentZOrder(startOverlay, 0);
        
        revalidate();
        repaint();
        
        // Request focus for key bindings
        requestFocusInWindow();
    }
}
