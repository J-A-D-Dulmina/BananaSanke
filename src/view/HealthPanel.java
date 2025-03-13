package view;

import utils.ImageLoader;
import javax.swing.*;
import java.awt.*;

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
    private int currentHealth = 3;
    private int wrongAttempts = 0;
    private static final int MAX_ATTEMPTS = 4;

    /**
     * Constructs the health panel with health text and heart icons.
     */
    public HealthPanel() {
        setPreferredSize(new Dimension(180, 60)); // Set height for balance
        setLayout(new GridBagLayout()); // Align elements properly
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add spacing between elements

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
            // Load heart GIF image
            ImageIcon heartGif = ImageLoader.loadImage("resources/heart_icon.gif", 18, 18);
            if (heartGif != null) {
                hearts[i] = new JLabel(heartGif);
            } else {
                // Fallback: Static heart symbol if GIF is missing
                hearts[i] = new JLabel("♥");
                hearts[i].setFont(new Font("Arial", Font.BOLD, 18));
                hearts[i].setForeground(Color.RED);
            }
            heartPanel.add(hearts[i]);
        }

        gbc.gridx = 1;
        add(heartPanel, gbc); // Align hearts properly
    }

    /**
     * Reduces health by one heart and updates the UI.
     * @return true if still alive, false if game over
     */
    public boolean loseHealth() {
        wrongAttempts++;
        
        if (wrongAttempts <= 3) {
            currentHealth--;
            updateHealthDisplay();
            
            if (wrongAttempts == 3) {
                healthLabel.setText("FINAL CHANCE!");
                healthLabel.setForeground(Color.RED);
            }
            
            return true;
        } else if (wrongAttempts == 4) {
            currentHealth = 0;
            updateHealthDisplay();
            return false;
        }
        
        return false;
    }

    /**
     * Updates the heart display based on current health.
     */
    private void updateHealthDisplay() {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisible(i < currentHealth);
        }
    }

    /**
     * Resets health to full (3 hearts).
     */
    public void resetHealth() {
        currentHealth = 3;
        wrongAttempts = 0;
        healthLabel.setText("Health: ");
        healthLabel.setForeground(Color.WHITE);
        updateHealthDisplay();
    }

    /**
     * Gets the current health value.
     * @return Current health count
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getWrongAttempts() {
        return wrongAttempts;
    }

    public boolean isLastAttempt() {
        return wrongAttempts == 3;
    }
}
