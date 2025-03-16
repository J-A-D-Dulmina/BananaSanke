package view;

import utils.ImageLoader;
import javax.swing.*;
import java.awt.*;
import model.HealthPanelModel;
import controller.HealthPanelController;

/**
 * Panel displaying the player's health with heart icons.
 */
public class HealthPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3077270849243182659L;
	private JLabel healthLabel;
    private JLabel[] hearts = new JLabel[3];
    private final HealthPanelController controller;

    /**
     * Constructs the health panel with health text and heart icons.
     */
    public HealthPanel() {
        // Initialize MVC components
        HealthPanelModel model = new HealthPanelModel();
        this.controller = new HealthPanelController(model, this);

        setPreferredSize(new Dimension(180, 60));
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Health text label
        healthLabel = new JLabel("Health: ");
        healthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        healthLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(healthLabel, gbc);

        // Panel for heart icons
        JPanel heartPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        heartPanel.setOpaque(false);

        for (int i = 0; i < 3; i++) {
            // Try loading with full path first
            ImageIcon heartGif = ImageLoader.loadImage("resources/heart_icon.gif", 18, 18);
            
            // If that fails, try alternate paths
            if (heartGif == null) {
                System.out.println("Trying alternate path for heart icon...");
                heartGif = ImageLoader.loadImage("heart_icon.gif", 18, 18);
            }
            
            if (heartGif != null) {
                System.out.println("Heart icon loaded successfully");
                hearts[i] = new JLabel(heartGif);
            } else {
                System.out.println("Failed to load heart icon, using text fallback");
                hearts[i] = new JLabel("♥");
                hearts[i].setFont(new Font("Arial", Font.BOLD, 18));
                hearts[i].setForeground(Color.RED);
            }
            heartPanel.add(hearts[i]);
        }

        gbc.gridx = 1;
        add(heartPanel, gbc);
    }

    /**
     * Updates the health display based on current health.
     */
    public void updateHealthDisplay(int currentHealth) {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisible(i < currentHealth);
        }
    }

    /**
     * Updates the health label text.
     */
    public void updateHealthLabel(String text) {
        healthLabel.setText(text);
        healthLabel.setForeground(Color.RED);
    }

    /**
     * Resets the health display to initial state.
     */
    public void resetHealthDisplay() {
        healthLabel.setText("Health: ");
        healthLabel.setForeground(Color.WHITE);
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisible(true);
        }
    }

    /**
     * Shows a game over message but doesn't end the game.
     * This is shown after the 3rd heart disappears, but the player still has one more attempt.
     */
    public void showGameOverMessage() {
        BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, this);
        if (parent != null) {
            // Show a message dialog but don't end the game yet
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    parent,
                    "GAME OVER! You'll get one more chance!",
                    "Game Over",
                    JOptionPane.WARNING_MESSAGE
                );
            });
        }
    }

    /**
     * Handles game over state.
     */
    public void handleGameOver() {
        // This will be handled by the parent panel
        BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, this);
        if (parent != null) {
            parent.showGameOver(0); // Score will be handled by the game logic
        }
    }

    /**
     * Reduces health when eating wrong answer.
     * @return true if still alive, false if game over
     */
    public boolean loseHealth() {
        controller.handleHealthLoss();
        return !controller.isGameOver();
    }

    /**
     * Resets health to full (3 hearts).
     */
    public void resetHealth() {
        controller.handleReset();
    }

    /**
     * Gets the current health value.
     * @return Current health count
     */
    public int getCurrentHealth() {
        return controller.getCurrentHealth();
    }
}
