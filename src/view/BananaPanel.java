package view;

import controller.BananaPanelController;
import model.BananaPanelModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Panel that contains the button panel, API section, and bottom bar.
 */
public class BananaPanel extends JPanel {
    private static final long serialVersionUID = 6514599655410446347L;
    private final ScorePanel scorePanel; // Score Panel Reference
    private final APISection apiSection; // API Section Reference
    private final SnakePanel snakePanel;
    private final HealthPanel healthPanel;
    private final TimerPanel timerPanel;
    private BananaPanelController controller;
    private int score;
    private static final Color BANANA_COLOR = new Color(255, 225, 53);
    private static final Color BANANA_PEEL_COLOR = new Color(255, 200, 0);
    

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
        timerPanel = new TimerPanel();

        // Top section: Button panel
        ButtonPanel buttonPanel = new ButtonPanel(mainFrame, snakePanel.getGameLogic(), snakePanel);
        add(buttonPanel, BorderLayout.NORTH);

        // Center section: API section with an image and label
        apiSection = new APISection(scorePanel); // Pass ScorePanel to APISection
        
        add(apiSection, BorderLayout.CENTER);

        // Bottom section: Score panel (left) and Health panel (right)
        JPanel bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);

        controller = new BananaPanelController(this);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                controller.setPanelDimensions(getWidth(), getHeight());
            }
        });
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

        // Timer panel (center)
        JPanel timerPanelContainer = new JPanel(new GridBagLayout());
        timerPanelContainer.setOpaque(false);
        timerPanelContainer.add(timerPanel);
        bottomPanel.add(timerPanelContainer, BorderLayout.CENTER);

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

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    /**
     * Returns the Snake Panel for game control.
     */
    public SnakePanel getSnakePanel() {
        return snakePanel;
    }

    /**
     * Shows the game over screen with the final score.
     */
    public void showGameOver(int score) {
        try {
            timerPanel.stop();
            snakePanel.getGameLogic().setRunning(false);
            
            Component comp = this;
            while (comp != null && !(comp instanceof GameMainInterface)) {
                comp = comp.getParent();
            }
            
            if (comp instanceof GameMainInterface) {
                GameMainInterface mainFrame = (GameMainInterface) comp;
                GameOverPanel gameOverPanel = new GameOverPanel(mainFrame, score);
                gameOverPanel.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Game Over! Final Score: " + score,
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Game Over! Final Score: " + score,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        BananaPanelModel model = controller.getModel();
        if (model.isVisible()) {
            Point position = model.getPosition();
            int size = model.getBananaSize();
            
            // Draw banana peel
            g2d.setColor(BANANA_PEEL_COLOR);
            g2d.fillOval(position.x - size/2, position.y - size/2, size, size);
            
            // Draw banana
            g2d.setColor(BANANA_COLOR);
            g2d.fillOval(position.x - size/3, position.y - size/3, size*2/3, size*2/3);
        }
    }

    public void updateScore(int newScore) {
        this.score = newScore;
    }

    public int getScore() {
        return score;
    }

    public void checkCollision(Point snakeHead) {
        controller.checkCollision(snakeHead);
    }

    public void generateNewBanana() {
        controller.generateNewBanana();
    }

    public void reset() {
        controller.reset();
    }

    public void setBananaSize(int size) {
        controller.setBananaSize(size);
    }

    public void dispose() {
        controller.dispose();
    }
}
