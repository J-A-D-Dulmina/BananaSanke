package view;

import utils.ImageLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import model.HealthPanelModel;
import controller.HealthPanelController;
import java.net.URL;

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

        // Skip external file loading and directly create the heart icon
        ImageIcon heartIcon = createHeartIcon(20, 20);
        System.out.println("Created heart icon directly in code to avoid loading issues");
        
        // Create and add heart labels to panel
        for (int i = 0; i < 3; i++) {
            // Always create a new copy of the icon to avoid sharing issues
            ImageIcon heartCopy = new ImageIcon(heartIcon.getImage());
            hearts[i] = new JLabel(heartCopy);
            hearts[i].setPreferredSize(new Dimension(20, 20));
            hearts[i].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            heartPanel.add(hearts[i]);
            System.out.println("Added heart #" + i);
        }

        gbc.gridx = 1;
        add(heartPanel, gbc);
        
        // Ensure all hearts are visible initially
        updateHealthDisplay(3);
    }

    /**
     * Creates a heart-shaped icon with proper styling
     */
    private ImageIcon createHeartIcon(int width, int height) {
        // Create a custom heart shape
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a red heart shape
        g2.setColor(new Color(255, 0, 0, 240));
        
        // Create heart shape using Bezier curves for smoother look
        int centerX = width / 2;
        int size = Math.min(width, height);
        
        // Create a smooth heart shape
        int[] xPoints = new int[]{
            centerX, centerX + size/2, centerX + size/3, centerX, 
            centerX - size/3, centerX - size/2, centerX
        };
        
        int[] yPoints = new int[]{
            size/4, size/2, size - size/4, size - size/6, 
            size - size/4, size/2, size/4
        };
        
        // Fill the heart shape
        g2.fillPolygon(xPoints, yPoints, xPoints.length);
        
        // Add shading for 3D effect
        g2.setColor(new Color(255, 0, 0, 255));
        int shadowSize = size/8;
        
        // Two circles for top of heart with better position
        g2.fillOval(centerX - size/4 - shadowSize/2, size/5, size/2, size/2);
        g2.fillOval(centerX - shadowSize/2, size/5, size/2, size/2);
        
        g2.dispose();
        return new ImageIcon(img);
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
