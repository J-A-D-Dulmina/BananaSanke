package view;

import javax.swing.*;
import java.awt.*;

/**
 * Panel displaying the player's score.
 */
public class ScorePanel extends JPanel {
    private static final long serialVersionUID = 4401016708078053350L;
    private int score = 0;
    private final JLabel scoreLabel;

    /**
     * Constructs the score panel with an initial score display.
     */
    public ScorePanel() {
        setPreferredSize(new Dimension(180, 60));
        setLayout(new GridBagLayout()); // Ensures vertical centering
        setOpaque(false);

        // Dynamically updating score label
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(Color.WHITE);

        add(scoreLabel); // Fully centered
    }

    /**
     * Increments the score and updates the display.
     */
    public void incrementScore() {
        score++;
        updateScoreLabel();
    }

    /**
     * Updates the score label in the UI.
     */
    private void updateScoreLabel() {
        SwingUtilities.invokeLater(() -> scoreLabel.setText("Score: " + score));
    }

    /**
     * Retrieves the current score.
     * @return The current score value.
     */
    public int getScore() {
        return score;
    }
}
