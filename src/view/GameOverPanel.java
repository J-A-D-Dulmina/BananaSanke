package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controller.GameOverController;
import model.GameOverModel;
import interfaces.ISessionManager;
import model.SessionManagerImpl;

public class GameOverPanel extends JDialog {
    private static final long serialVersionUID = 1L;
    private final GameMainInterface mainFrame;
    private final GameOverController controller;
    private final GameOverModel model;
    private final ISessionManager sessionManager;

    public GameOverPanel(GameMainInterface mainFrame, int finalScore) {
        super(mainFrame, "Game Over", true);
        this.mainFrame = mainFrame;
        this.sessionManager = SessionManagerImpl.getInstance();
        
        // Initialize MVC components
        this.model = new GameOverModel();
        this.controller = new GameOverController(this);
        
        // Reduced height from 300 to 250 since we're removing the username display
        setSize(400, 250);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                resetAndClose();
            }
        });
        
        initializeComponents();
        
        // Set initial game results - we still pass username for model consistency
        // but we won't display it
        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            username = "Guest";
        }
        controller.setGameResults(finalScore, 0, username);
        
        // Update the display
        updateDisplay();
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

        // Score
        JLabel scoreLabel = createStyledLabel("Final Score: " + model.getFinalScore(), 24);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        // Username label removed
        centerPanel.add(scoreLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void resetAndClose() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            mainFrame.getSnakePanel().getGameController().resetGame();
            mainFrame.getSnakePanel().resetToStart();
            mainFrame.requestFocusInWindow();
        });
    }

    public void updateDisplay() {
        // Only update the score label since username was removed
        JLabel scoreLabel = null;
        for (Component comp : ((JPanel)getContentPane().getComponent(0)).getComponents()) {
            if (comp instanceof JPanel && BorderLayout.CENTER.equals(((BorderLayout)((JPanel)getContentPane().getComponent(0)).getLayout()).getConstraints(comp))) {
                for (Component innerComp : ((JPanel)comp).getComponents()) {
                    if (innerComp instanceof JLabel && ((JLabel)innerComp).getText().startsWith("Final Score")) {
                        scoreLabel = (JLabel)innerComp;
                        break;
                    }
                }
                break;
            }
        }
        
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + model.getFinalScore());
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
} 