package view;

import utils.ImageLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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

        setPreferredSize(new Dimension(220, 70));
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;

        // Health text label
        healthLabel = new JLabel("Health: ");
        healthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        healthLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(healthLabel, gbc);

        // Panel for heart icons
        JPanel heartPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        heartPanel.setOpaque(false);
        // Don't constrain the heart panel size, let it adjust to the natural size of the GIFs
        // heartPanel.setPreferredSize(new Dimension(120, 40));
        
        // Print debug information about loaded GIF
        System.out.println("Loading heart icon...");

        // Load heart icon
        ImageIcon heartIcon = null;
        try {
            heartIcon = ImageLoader.loadImage("resources/heart_icon_new.gif");
  
            
        } catch (Exception e) {
            System.err.println("Error loading heart icon: " + e.getMessage());
        }

        // Create heart icons
        for (int i = 0; i < 3; i++) {
            if (heartIcon != null && heartIcon.getIconWidth() > 0) {
                // Important: Don't scale animated GIFs as it can break the animation
                // Use the original GIF as is with its natural size
                hearts[i] = new JLabel(heartIcon); 
                hearts[i].setOpaque(false);
                
                // Don't set preferredSize - let the GIF display at natural size
                // hearts[i].setPreferredSize(new Dimension(18, 18));
                
                // Add padding with an empty border to prevent clipping
                hearts[i].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                
                hearts[i].setHorizontalAlignment(JLabel.CENTER);
                hearts[i].setVerticalAlignment(JLabel.CENTER);
                
              
            } else {
                // Fallback to a red dot if icon loading fails
                BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = (Graphics2D) img.getGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.RED);
                g2.fillOval(0, 0, 16, 16); // Match the size with the BufferedImage dimensions
                g2.dispose();
                
                hearts[i] = new JLabel(new ImageIcon(img));
                hearts[i].setOpaque(false);
                System.out.println("Fallback: Created red dot health indicator #" + i);
            }
            heartPanel.add(hearts[i]);
        }

        gbc.gridx = 1;
        add(heartPanel, gbc);
        
        // Ensure all hearts are visible initially
        updateHealthDisplay(3);
    }

    /**
     * Updates the health display based on current health.
     */
    public void updateHealthDisplay(int currentHealth) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < hearts.length; i++) {
                if (hearts[i] != null) {
                    hearts[i].setVisible(i < currentHealth);
                }
            }
            revalidate();
            repaint();
        });
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
     * Shows a last chance message but doesn't end the game.
     * This is shown after the 3rd heart disappears, but the player still has one more attempt.
     */
    public void showGameOverMessage() {
        BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, this);
        if (parent != null) {
            // Show a message dialog but don't end the game yet
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    parent,
                    "You're down to your LAST CHANCE!",
                    "Warning",
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
            // Get the actual score from the parent
            int finalScore = parent.getScore();
            parent.showGameOver(finalScore); // Pass the actual score
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
