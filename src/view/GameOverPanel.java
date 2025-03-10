package view;

import javax.swing.*;
import java.awt.*;
import model.SessionManager;

public class GameOverPanel extends JDialog {
    private static final long serialVersionUID = 1L;
    private final GameMainInterface mainFrame;
    private final int finalScore;

    public GameOverPanel(GameMainInterface mainFrame, int finalScore) {
        super(mainFrame, "Game Over", true);
        this.mainFrame = mainFrame;
        this.finalScore = finalScore;
        
        setSize(400, 300);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Main panel with dark background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Game Over text
        JLabel gameOverLabel = createStyledLabel("Game Over!", 32);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        String username = SessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            username = "Guest";
        }
        JLabel usernameLabel = createStyledLabel("Player: " + username, 24);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Score
        JLabel scoreLabel = createStyledLabel("Final Score: " + finalScore, 24);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // Try Again button
        JButton tryAgainButton = createStyledButton("Try Again");
        tryAgainButton.addActionListener(e -> {
            dispose();
            mainFrame.getSnakeGameLogic().reset();
        });

        // Leaderboard button
        JButton leaderboardButton = createStyledButton("Leaderboard");
        leaderboardButton.addActionListener(e -> {
            mainFrame.showLeaderboard();
        });

        buttonPanel.add(tryAgainButton);
        buttonPanel.add(leaderboardButton);

        // Add components with spacing
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(gameOverLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scoreLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 150, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
} 