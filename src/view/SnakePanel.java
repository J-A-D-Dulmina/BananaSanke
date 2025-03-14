package view;

import controller.SnakeGameController;
import model.SnakeGameLogic;
import model.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class SnakePanel extends JPanel {
    private static final long serialVersionUID = -6220166041632288689L;
	private final SnakeGameController gameController;
    private final SnakeGameLogic gameLogic;
    private boolean gameStarted = false;
    private JPanel startOverlay;
    private JPanel pauseOverlay;
    private Image rightMouth, leftMouth, upMouth, downMouth, snakeBody, foodImage;
    private List<MouseListener> pauseOverlayListeners = new ArrayList<>();

    public SnakePanel() {
        setLayout(null); // Use null layout for absolute positioning
        setBackground(Color.BLACK);
        gameLogic = new SnakeGameLogic(480, 500, 20);
        gameController = new SnakeGameController(this, gameLogic);
        
        // Start in paused state
        gameLogic.setRunning(false);

        loadImages();
        setFocusable(true);
        requestFocusInWindow();
        setupKeyBindings();
        createStartOverlay();
        createPauseOverlay();
        
        // Only show start overlay initially
        if (startOverlay != null) {
            add(startOverlay);
        }
    }

    private void createStartOverlay() {
        startOverlay = new JPanel() {
            private static final long serialVersionUID = -447588292493792970L;

			@Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
                
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
                    SoundManager.getInstance().playButtonClickSound();
                    startGame();
                }
            }
        });
    }

    private void createPauseOverlay() {
        pauseOverlay = new JPanel() {
            private static final long serialVersionUID = 6296788058452217848L;

			@Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                String message = "Click to Resume";
                FontMetrics fm = g.getFontMetrics();
                int messageWidth = fm.stringWidth(message);
                g.drawString(message, (getWidth() - messageWidth) / 2, getHeight() / 2);
            }
        };
        
        pauseOverlay.setOpaque(false);
        pauseOverlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameLogic.isRunning() && gameStarted) {
                    SoundManager.getInstance().playButtonClickSound();
                    gameLogic.setRunning(true);
                    hidePauseOverlay();
                    requestFocusInWindow();
                    // Notify listeners
                    for (MouseListener listener : pauseOverlayListeners) {
                        listener.mouseClicked(e);
                    }
                }
            }
        });
    }

    public void showPauseOverlay() {
        if (!gameStarted) return;  // Don't show pause overlay if game hasn't started
        
        if (pauseOverlay == null) {
            createPauseOverlay();
        }
        
        if (pauseOverlay.getParent() == null) {
            add(pauseOverlay);
            pauseOverlay.setBounds(0, 0, getWidth(), getHeight());
            setComponentZOrder(pauseOverlay, 0);
            revalidate();
            repaint();
        }
    }

    public void hidePauseOverlay() {
        if (pauseOverlay != null && pauseOverlay.getParent() != null) {
            remove(pauseOverlay);
            revalidate();
            repaint();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (!gameStarted && startOverlay != null && startOverlay.getParent() == null) {
            add(startOverlay);
            startOverlay.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Update overlay sizes when panel is resized
        if (startOverlay != null) {
            startOverlay.setBounds(0, 0, width, height);
        }
        if (pauseOverlay != null) {
            pauseOverlay.setBounds(0, 0, width, height);
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
            private static final long serialVersionUID = 632562006938625925L;

			@SuppressWarnings("deprecation")
			@Override
            public void actionPerformed(ActionEvent e) {
                gameController.handleKeyPress(new KeyEvent(SnakePanel.this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP));
            }
        });

        getActionMap().put("moveDown", new AbstractAction() {
            private static final long serialVersionUID = -6542449394368846278L;

			@SuppressWarnings("deprecation")
			@Override
            public void actionPerformed(ActionEvent e) {
                gameController.handleKeyPress(new KeyEvent(SnakePanel.this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN));
            }
        });

        getActionMap().put("moveLeft", new AbstractAction() {
            private static final long serialVersionUID = 6225678115573815137L;

			@SuppressWarnings("deprecation")
			@Override
            public void actionPerformed(ActionEvent e) {
                gameController.handleKeyPress(new KeyEvent(SnakePanel.this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT));
            }
        });

        getActionMap().put("moveRight", new AbstractAction() {
            private static final long serialVersionUID = 1904909473599413029L;

			@SuppressWarnings("deprecation")
			@Override
            public void actionPerformed(ActionEvent e) {
                gameController.handleKeyPress(new KeyEvent(SnakePanel.this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT));
            }
        });
    }

    public SnakeGameController getGameController() {
        return gameController;
    }

    // Reset method to properly handle game state
    public void resetToStart() {
        gameStarted = false;
        gameLogic.setRunning(false);
        
        // Remove existing overlays
        if (startOverlay != null && startOverlay.getParent() != null) {
            remove(startOverlay);
            startOverlay = null;
        }
        if (pauseOverlay != null && pauseOverlay.getParent() != null) {
            remove(pauseOverlay);
        }
        
        // Create fresh start overlay
        createStartOverlay();
        startOverlay.setBounds(0, 0, getWidth(), getHeight());
        add(startOverlay);
        setComponentZOrder(startOverlay, 0);
        
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    public void addPauseOverlayClickListener(MouseListener listener) {
        pauseOverlayListeners.add(listener);
    }

    public void removePauseOverlayClickListener(MouseListener listener) {
        pauseOverlayListeners.remove(listener);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            gameLogic.setRunning(true);
            gameController.startGame();
            if (startOverlay != null) {
                remove(startOverlay);
                startOverlay = null;  // Clear the reference
            }
            revalidate();
            repaint();
            requestFocusInWindow();
        }
    }
}
