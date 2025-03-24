package view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import controller.GameController;
import model.Game;

/**
 * Displays the game question fetched from the API with a background image.
 */
public class APISection extends JPanel {
    private static final long serialVersionUID = -1993748387131120068L;
    private JLabel backgroundLabel;
    private JLabel questLabel;
    private JLabel questArea;
    private JLabel messageLabel;
    private final GameController gameController;
    private final ScorePanel scorePanel; // Reference to ScorePanel
    private Game currentGame; // Store current game
    private static APISection instance; // Singleton instance

    private static final Color MESSAGE_BACKGROUND_COLOR = new Color(0, 128, 0, 200);
    private static final Font QUESTION_FONT = new Font("Courier New", Font.BOLD, 35);
    private static final Font MESSAGE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final String DEFAULT_MESSAGE = "Eat the correct number before time expire!";

    /**
     * Initializes the API section panel and its components.
     */
    public APISection(ScorePanel scorePanel) {
        this.scorePanel = scorePanel;
        setLayout(new BorderLayout());
        gameController = new GameController();
        instance = this;

        // Load background image
        backgroundLabel = createBackgroundLabel("resources/background_image.png");

        // Create question label
        questLabel = createQuestionLabel("Find the missing value");
        questLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Create question image area
        questArea = new JLabel();
        questArea.setHorizontalAlignment(SwingConstants.CENTER);

        // Create message label
        messageLabel = createMessageLabel(DEFAULT_MESSAGE);

        // Add components inside background label
        backgroundLabel.setLayout(new BorderLayout());
        backgroundLabel.add(questLabel, BorderLayout.NORTH);
        backgroundLabel.add(questArea, BorderLayout.CENTER);
        backgroundLabel.add(messageLabel, BorderLayout.SOUTH);

        // Add background label
        add(backgroundLabel, BorderLayout.CENTER);

        // Fetch and display the first question image
        loadQuestionImage();
    }

    /**
     * Gets the singleton instance of APISection
     */
    public static APISection getInstance() {
        return instance;
    }

    /**
     * Loads the question image from the API and retrieves the correct answer.
     */
    private void loadQuestionImage() {
        try {
            URL questionImageUrl = gameController.getNextGame();

            if (questionImageUrl != null) {
                currentGame = gameController.getCurrentGame();
                if (currentGame != null) {
                    ImageIcon imageIcon = new ImageIcon(questionImageUrl);
                    if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        Image resizedImage = imageIcon.getImage().getScaledInstance(480, 250, Image.SCALE_SMOOTH);
                        questArea.setIcon(new ImageIcon(resizedImage));
                    } else {
                        System.err.println("Failed to load image from URL: " + questionImageUrl);
                        questArea.setText("Error: Unable to load image");
                    }
                } else {
                    System.err.println("Failed to get current game from controller");
                    questArea.setText("Error: Unable to load game");
                }
            } else {
                System.err.println("Failed to get next game from controller");
                questArea.setText("Error: Unable to load game");
            }
        } catch (Exception e) {
            System.err.println("Error loading question image: " + e.getMessage());
            e.printStackTrace();
            questArea.setText("Error: Unable to load game");
        }
    }

    /**
     * Creates a JLabel with a background image.
     */
    private JLabel createBackgroundLabel(String imagePath) {
        ImageIcon backgroundImage = new ImageIcon(imagePath);
        JLabel label = new JLabel(backgroundImage);
        label.setLayout(new BorderLayout());
        return label;
    }

    /**
     * Creates a JLabel for the question text.
     */
    private JLabel createQuestionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(QUESTION_FONT);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Creates a JLabel for the message display.
     */
    private JLabel createMessageLabel(String message) {
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(MESSAGE_FONT);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(MESSAGE_BACKGROUND_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return label;
    }

    /**
     * Updates the message displayed in the panel.
     */
    public void updateMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Gets the current game.
     */
    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Gets the current correct answer.
     */
    public int getCorrectAnswer() {
        return currentGame != null ? currentGame.getSolution() : -1;
    }

    /**
     * Loads the next question.
     */
    public void loadNextQuestion() {
        loadQuestionImage();
    }

    /**
     * Updates the score in the ScorePanel.
     */
    public void updateScore(int score) {
        if (scorePanel != null) {
            final int scoreToUpdate = score;
            SwingUtilities.invokeLater(() -> {
                try {
                    // Get the current score
                    int currentScore = scorePanel.getScore();
                    
                    // If the new score is higher than the current score, increment it
                    if (scoreToUpdate > currentScore) {
                        scorePanel.incrementScore();
                    } else if (scoreToUpdate < currentScore) {
                        // If the new score is lower, reset and increment to the desired score
                        scorePanel.resetScore();
                        for (int i = 0; i < scoreToUpdate; i++) {
                            scorePanel.incrementScore();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error updating score: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Reduces health when eating wrong answer.
     * @return true if still alive, false if game over
     */
    public boolean reduceHealth() {
        try {
            BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, this);
            if (parent != null && parent.getHealthPanel() != null) {
                return parent.getHealthPanel().loseHealth();
            }
        } catch (Exception e) {
            System.err.println("Error reducing health: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}
