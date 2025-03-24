package view;

import controller.ScorePanelController;
import model.ScoreObserver;
import javax.swing.*;
import java.awt.*;

/**
 * ScorePanel displays the current score in the game.
 */
public class ScorePanel extends JPanel implements ScoreObserver {
    private static final long serialVersionUID = 4401016708078053350L;
    private JLabel scoreLabel;
    private JLabel scoreValue;
    private final ScorePanelController controller;

    /**
     * Constructs the score panel with an initial score display.
     */
    public ScorePanel() {
        controller = new ScorePanelController(this);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridLayout(1, 2, 10, 5));
        setBackground(new Color(0, 0, 0, 180));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Create labels with custom styling
        scoreLabel = createStyledLabel("Score:");
        scoreValue = createStyledLabel("0");

        // Add components to panel
        add(scoreLabel);
        add(scoreValue);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    /**
     * Updates the score display when notified by the model.
     * @param newScore The new score value.
     */
    @Override
    public void onScoreUpdated(int newScore) {
        SwingUtilities.invokeLater(() -> {
            scoreValue.setText(String.valueOf(newScore));
        });
    }

    /**
     * Gets the current score.
     * @return The current score value.
     */
    public int getScore() {
        return controller.getCurrentScore();
    }

    /**
     * Resets the score.
     */
    public void resetScore() {
        controller.handleGameReset();
    }

    /**
     * Increments the score.
     */
    public void incrementScore() {
        controller.handleFoodEaten();
    }
}
