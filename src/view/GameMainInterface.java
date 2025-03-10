package view;

import controller.LeaderboardController;
import model.ScoreManager;
import model.SessionManager; // Ensure session is cleared on logout
import model.SnakeGameLogic;

import javax.swing.*;
import api.APIClient;
import java.awt.*;

/**
 * Main game interface that contains the Banana Snake game layout.
 */
public class GameMainInterface extends JFrame {
    private static final long serialVersionUID = 1819086738853566570L;
    private final ScoreManager scoreManager;
    private LeaderboardPanel leaderboardPanel;
    private LeaderboardController leaderboardController;
    private SnakePanel snakePanel; // ✅ Declare snakePanel

    /**
     * Constructs the main game interface with all UI components.
     */
    public GameMainInterface() {
        setTitle("Banana Snake");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevents immediate close
        setLocationRelativeTo(null);

        // Ask for logout when closing the game
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                confirmAndExit();
            }
        });

        // Initialize Score Manager & Leaderboard
        scoreManager = new ScoreManager();
        leaderboardPanel = new LeaderboardPanel(this);
        leaderboardController = new LeaderboardController(scoreManager, leaderboardPanel);

        // Create UI Panels
        JPanel topBar = createTopBar();
        snakePanel = new SnakePanel();
        JPanel leftPanel = new BananaPanel(this, snakePanel);
        JPanel rightPanel = new SnakePanel();
        

        // Create Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        splitPane.setEnabled(false);
        splitPane.setDividerLocation(500);

        // Add Components
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Ensure Split Pane Resizes Properly
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                splitPane.setDividerLocation(getWidth() / 2);
            }
        });

        setSize(1000, 600);
        setResizable(false);
    }

    /**
     * Creates the top bar panel with a centered game header image.
     * @return A JPanel containing the header.
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(getWidth(), 60));

        ImageIcon imageIcon = new ImageIcon("resources/Main Header.png");
        JLabel imageLabel = new JLabel(imageIcon);

        topBar.add(imageLabel);
        return topBar;
    }

    /**
     * Displays the leaderboard by updating and making it visible.
     */
    public void showLeaderboard() {
        leaderboardController.updateLeaderboard();
        leaderboardPanel.setVisible(true);
    }

    /**
     * Asks for confirmation before logging out and closing the game.
     */
    private void confirmAndExit() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the game?",
            "Exit Game",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            APIClient.logoutUser(); // Logout the user from API
            SessionManager.logout(); // Clear local session data
            dispose(); // Close the current game window
            SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true)); // Redirect to Login
        }
    }
    
    public SnakeGameLogic getSnakeGameLogic() {
		return snakePanel.getGameLogic();
    }

}
