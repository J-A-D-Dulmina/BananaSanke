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
    private static final String DEFAULT_MESSAGE = "Eat the correct number!";

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
        URL questionImageUrl = gameController.getNextGame();

        if (questionImageUrl != null) {
            currentGame = gameController.getCurrentGame();
            int answer = currentGame.getSolution();
            // Print the new puzzle answer to console
            System.out.println("New Puzzle - Answer: " + answer);

            ImageIcon imageIcon = new ImageIcon(questionImageUrl);
            Image resizedImage = imageIcon.getImage().getScaledInstance(480, 250, Image.SCALE_SMOOTH);
            questArea.setIcon(new ImageIcon(resizedImage));
        } else {
            questArea.setText("API Error: Unable to load image.");
            System.err.println("Failed to load new puzzle image");
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
}
