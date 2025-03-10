package view;

import javax.swing.*;
import java.awt.*;


/**
 * Panel that contains the button panel, API section, and bottom bar.
 */
public class BananaPanel extends JPanel {
    private static final long serialVersionUID = 6514599655410446347L;
    private final ScorePanel scorePanel; // Score Panel Reference
    private final APISection apiSection; // API Section Reference
    private final SnakePanel snakePanel;
    private final HealthPanel healthPanel;
    

    /**
     * Constructs the BananaPanel with different sections.
     * @param mainFrame Reference to the main game interface.
     */
    public BananaPanel(GameMainInterface mainFrame, SnakePanel snakePanel) {
    	this.snakePanel = snakePanel;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 600));

        // Initialize Score Panel
        scorePanel = new ScorePanel();
        healthPanel = new HealthPanel();

        // Top section: Button panel
        ButtonPanel buttonPanel = new ButtonPanel(mainFrame, snakePanel.getGameLogic(), snakePanel);
        add(buttonPanel, BorderLayout.NORTH);

        // Center section: API section with an image and label
        apiSection = new APISection(scorePanel); // Pass ScorePanel to APISection
        
        add(apiSection, BorderLayout.CENTER);

        // Bottom section: Score panel (left) and Health panel (right)
        JPanel bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * Creates the bottom bar containing the score and health panels.
     * @return A JPanel with score and health indicators.
     */
    private JPanel createBottomBar() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 60));
        bottomPanel.setBackground(new Color(30, 30, 30));

        // Score panel (left side)
        JPanel scorePanelContainer = new JPanel(new GridBagLayout());
        scorePanelContainer.setOpaque(false);
        scorePanelContainer.add(scorePanel);
        bottomPanel.add(scorePanelContainer, BorderLayout.WEST);

        // Health panel (right side)
        JPanel healthPanelContainer = new JPanel(new GridBagLayout());
        healthPanelContainer.setOpaque(false);
        healthPanelContainer.add(healthPanel);
        bottomPanel.add(healthPanelContainer, BorderLayout.EAST);

        return bottomPanel;
    }

    /**
     * Returns the Score Panel for score updates.
     */
    public ScorePanel getScorePanel() {
        return scorePanel;
    }

    public HealthPanel getHealthPanel() {
        return healthPanel;
    }
}
