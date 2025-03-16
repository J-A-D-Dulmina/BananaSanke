package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controller.GameOverController;
import model.GameOverModel;
import model.SoundManager;
import interfaces.ISessionManager;
import model.SessionManagerImpl;
import utils.ImageLoader;

public class GameOverPanel extends JDialog {
    private static final long serialVersionUID = 1L;
    private final GameMainInterface mainFrame;
    private final GameOverController controller;
    private final GameOverModel model;
    private final ISessionManager sessionManager;
    private JLabel scoreLabel; // Store reference to the score label
    private JLabel highScoreLabel; // Label for high score message
    private boolean isClosing = false;

    public GameOverPanel(GameMainInterface mainFrame, int finalScore) {
        super(mainFrame, "Game Over", true);
        this.mainFrame = mainFrame;
        this.sessionManager = SessionManagerImpl.getInstance();
        
        // Stop all sounds when game over panel appears
        try {
            SoundManager.getInstance().stopAll();
            System.out.println("Stopped all sounds in GameOverPanel constructor");
        } catch (Exception e) {
            System.err.println("Error stopping sounds: " + e.getMessage());
        }
        
        // Initialize MVC components
        this.model = new GameOverModel();
        this.controller = new GameOverController(this);
        
        // Set initial game results - we still pass username for model consistency
        // but we won't display it
        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            username = "Guest";
        }
        
        // Set the final score immediately
        model.setFinalScore(finalScore);
        controller.setGameResults(finalScore, 0, username);
        
        // Debug - confirm score is set correctly
        System.out.println("Score set in model: " + model.getFinalScore());
        
        // Reduced height from 300 to 250 since we're removing the username display
        setSize(400, 250);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        // Add proper disposing on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                resetAndClose();
            }
        });
        
        initializeComponents();
        
        // Force score label update with final score
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + finalScore);
            System.out.println("Explicitly set score label to: " + finalScore);
        }
        
        // Handle high score checking using controller
        if (finalScore > 0) {
            SwingUtilities.invokeLater(() -> {
                controller.checkAndDisplayHighScore(finalScore);
            });
        }
        
        // Debug output
        System.out.println("GameOverPanel created with score: " + finalScore);
        System.out.println("Model score value: " + model.getFinalScore());
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Main panel with semi-transparent dark background
        JPanel mainPanel = new JPanel() {
            private static final long serialVersionUID = 6548795517653523982L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 40, 40, 230));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create top panel for close button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topPanel.setOpaque(false);
        
        // Create close button
        JButton closeButton = createCloseButton();
        topPanel.add(closeButton);

        // Center panel for content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Game Over text
        JLabel gameOverLabel = createStyledLabel("Game Over!", 32);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Score - initialize and store reference
        scoreLabel = createStyledLabel("Final Score: " + model.getFinalScore(), 24);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // High score message - initially hidden
        highScoreLabel = new JLabel("NEW HIGH SCORE!");
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        highScoreLabel.setForeground(Color.YELLOW);
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreLabel.setVisible(false); // Initially hidden

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // Try Again button
        JButton tryAgainButton = createStyledButtontry("Try Again");
        tryAgainButton.addActionListener(e -> resetAndClose());

        // Leaderboard button
        JButton leaderboardButton = createStyledButtonleader("Leaderboard");
        leaderboardButton.addActionListener(e -> {
            mainFrame.showLeaderboard();
        });

        buttonPanel.add(tryAgainButton);
        buttonPanel.add(leaderboardButton);

        // Add components with spacing
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(gameOverLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(scoreLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(highScoreLabel); // Add high score label
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        
        // Debug to confirm scoreLabel is initialized
        System.out.println("scoreLabel initialized: " + (scoreLabel != null));
    }

    private void resetAndClose() {
        // First flag that we're closing to prevent further updates
        isClosing = true;
        
        // Make sure all sounds are stopped
        try {
            SoundManager.getInstance().stopAll();
        } catch (Exception e) {
            System.err.println("Error stopping sounds: " + e.getMessage());
        }
        
        // Ensure we're cleaning up resources
        controller.dispose();
        
        // Then dispose the dialog
        SwingUtilities.invokeLater(() -> {
            dispose();
            // Give focus back to the main frame and reset the game
            if (mainFrame != null) {
                mainFrame.getSnakePanel().getGameController().resetGame();
                mainFrame.getSnakePanel().resetToStart();
                mainFrame.requestFocusInWindow();
            }
        });
    }

    public void updateDisplay() {
        // Don't update if we're closing
        if (isClosing) {
            return;
        }
        
        // Ensure we're on the EDT
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::updateDisplay);
            return;
        }
        
        // Get score directly from model to ensure consistency
        int finalScore = model.getFinalScore();
        
        // Update score label if it exists
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + finalScore);
            System.out.println("Updated score label to: " + finalScore);
        } else {
            System.err.println("Error: scoreLabel is null in updateDisplay");
        }
        
        // Update high score message based on model state
        if (highScoreLabel != null) {
            highScoreLabel.setVisible(model.shouldShowHighScoreMessage());
            if (model.shouldShowHighScoreMessage()) {
                // Only show "NEW HIGH SCORE" without the score value
                highScoreLabel.setText("NEW HIGH SCORE");
                
                // Try to add happy snake icons on both sides
                try {
                    ImageIcon snakeIcon = ImageLoader.loadImage("resources/score_update_happy_snake.png");
                    if (snakeIcon != null) {
                        // Scale the icon to a reasonable size
                        Image img = snakeIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                        ImageIcon scaledIcon = new ImageIcon(img);
                        
                        // Set the icon on the left side
                        highScoreLabel.setIcon(scaledIcon);
                        
                        // Set the same icon on the right side
                        highScoreLabel.setHorizontalTextPosition(JLabel.CENTER);
                        highScoreLabel.setIconTextGap(2);
                        highScoreLabel.setVerticalTextPosition(JLabel.CENTER);
                        
                        // We need to use a compound label layout to have icons on both sides
                        JPanel iconPanel = new JPanel(new BorderLayout());
                        iconPanel.setOpaque(false);
                        
                        // Add left icon
                        JLabel leftIconLabel = new JLabel(scaledIcon);
                        iconPanel.add(leftIconLabel, BorderLayout.WEST);
                        
                        // Add text in center
                        JLabel textLabel = new JLabel("NEW HIGH SCORE");
                        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
                        textLabel.setForeground(Color.YELLOW);
                        textLabel.setHorizontalAlignment(JLabel.CENTER);
                        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2)); 
                        iconPanel.add(textLabel, BorderLayout.CENTER);
                        
                        // Add right icon
                        JLabel rightIconLabel = new JLabel(scaledIcon);
                        iconPanel.add(rightIconLabel, BorderLayout.EAST);
                        
                        // Replace the highScoreLabel with this panel
                        int index = 0;
                        Container parent = highScoreLabel.getParent();
                        for (int i = 0; i < parent.getComponentCount(); i++) {
                            if (parent.getComponent(i) == highScoreLabel) {
                                index = i;
                                break;
                            }
                        }
                        
                        parent.remove(highScoreLabel);
                        parent.add(iconPanel, index);
                        highScoreLabel = textLabel; // Update the reference
                    }
                } catch (Exception e) {
                    System.err.println("Error loading happy snake icon: " + e.getMessage());
                }
            }
        }
        
        // Update other display elements as needed
        repaint();
    }

    public void showMessage(String message, boolean success) {
        JOptionPane.showMessageDialog(this, message, 
            success ? "Success" : "Error", 
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("×") {
            private static final long serialVersionUID = -4636843601320308633L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(255, 0, 0, 180));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 0, 0, 220));
                } else {
                    g2d.setColor(new Color(255, 0, 0, 160));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        closeButton.setPreferredSize(new Dimension(25, 25));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> resetAndClose());
        return closeButton;
    }

    private JButton createStyledButtontry(String text) {
        JButton button = new JButton(text) {
            private static final long serialVersionUID = 8694406378529595501L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(0, 150, 0));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(0, 220, 0));
                } else {
                    g2d.setColor(new Color(0, 200, 0));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JButton createStyledButtonleader(String text) {
        JButton button = new JButton(text) {
            private static final long serialVersionUID = -1684882379236487790L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(200, 200, 0));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 255, 100));
                } else {
                    g2d.setColor(Color.YELLOW);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Shows high score message in the panel
     * @param score The high score value
     */
    public void showHighScoreMessage(int score) {
        if (highScoreLabel != null) {
            try {
                // Create panel with happy snake icons on both sides
                ImageIcon snakeIcon = utils.ImageLoader.loadImage("resources/score_update_happy_snake.png");
                if (snakeIcon != null) {
                    // Scale the icon to a reasonable size
                    Image img = snakeIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(img);
                    
                    // Create a panel with both icons
                    JPanel iconPanel = new JPanel(new BorderLayout());
                    iconPanel.setOpaque(false);
                    
                    // Add left icon
                    JLabel leftIconLabel = new JLabel(scaledIcon);
                    iconPanel.add(leftIconLabel, BorderLayout.WEST);
                    
                    // Add text in center
                    JLabel textLabel = new JLabel("NEW HIGH SCORE");
                    textLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    textLabel.setForeground(Color.YELLOW);
                    textLabel.setHorizontalAlignment(JLabel.CENTER);
                    textLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2)); 
                    iconPanel.add(textLabel, BorderLayout.CENTER);
                    
                    // Add right icon
                    JLabel rightIconLabel = new JLabel(scaledIcon);
                    iconPanel.add(rightIconLabel, BorderLayout.EAST);
                    
                    // Replace the highScoreLabel with this panel
                    int index = 0;
                    Container parent = highScoreLabel.getParent();
                    for (int i = 0; i < parent.getComponentCount(); i++) {
                        if (parent.getComponent(i) == highScoreLabel) {
                            index = i;
                            break;
                        }
                    }
                    
                    parent.remove(highScoreLabel);
                    parent.add(iconPanel, index);
                    highScoreLabel = textLabel; // Update the reference
                }
            } catch (Exception e) {
                // Fallback to simple text if icon loading fails
                highScoreLabel.setText("NEW HIGH SCORE");
                System.err.println("Error loading happy snake icon: " + e.getMessage());
            }
            
            // Make sure it's visible
            if (highScoreLabel.getParent() != null) {
                highScoreLabel.getParent().setVisible(true);
            } else {
                highScoreLabel.setVisible(true);
            }
            
            revalidate();
            repaint();
        }
    }
} 