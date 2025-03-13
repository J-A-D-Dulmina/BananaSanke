package view;

import javax.swing.*;
import java.awt.*;
import model.SnakeGameLogic;
import model.SessionManager;
import api.APIClient;
import model.SoundManager;
import utils.CustomDialogUtils;
import org.json.JSONObject;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Main game interface that contains the Banana Snake game layout.
 */
public class GameMainInterface extends JFrame {
    private static final long serialVersionUID = 1L;
    private SnakePanel snakePanel;
    private LeaderboardPanel leaderboardPanel;
    private SoundManager soundManager;

    /**
     * Constructs the main game interface with all UI components.
     */
    public GameMainInterface() {
        super("Banana Snake");
        
        // Verify user is logged in
        if (SessionManager.getAuthToken() == null || SessionManager.getUsername() == null) {
            throw new IllegalStateException("User must be logged in to start game");
        }
        
        try {
            // Initialize sound manager
            soundManager = SoundManager.getInstance();
            soundManager.playBackgroundMusic();

            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            // Add window closing listener
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent evt) {
                    confirmAndExit();
                }
            });

            // Initialize components
            initializeComponents();

            // Set window properties
            setSize(1000, 600);
            setResizable(false);
            setLocationRelativeTo(null);
            
            // Request focus to ensure keyboard input works
            requestFocus();
            
        } catch (Exception e) {
            System.err.println("Error initializing game interface: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error initializing game. Please try restarting the application.",
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            throw e;
        }
    }

    private void initializeComponents() {
        try {
            // Create panels
            snakePanel = new SnakePanel();
            leaderboardPanel = new LeaderboardPanel(this);

            // Create top bar
            JPanel topBar = createTopBar();

            // Create game panels
            JPanel leftPanel = new BananaPanel(this, snakePanel);
            JPanel rightPanel = snakePanel;

            // Create split pane
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
            splitPane.setResizeWeight(0.5);
            splitPane.setDividerSize(5);
            splitPane.setEnabled(false);
            splitPane.setDividerLocation(500);

            // Add resize listener
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent evt) {
                    splitPane.setDividerLocation(getWidth() / 2);
                }
            });

            // Add components to frame
            setLayout(new BorderLayout());
            add(topBar, BorderLayout.NORTH);
            add(splitPane, BorderLayout.CENTER);
            
        } catch (Exception e) {
            System.err.println("Error initializing game components: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize game components", e);
        }
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
        leaderboardPanel.setVisible(true);
    }

    /**
     * Asks for confirmation before closing the game.
     */
    private void confirmAndExit() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        
        // Show confirmation dialog
        int choice = CustomDialogUtils.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Confirm Exit"
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Attempt to logout
                APIClient.logoutUser();
                
                // Close the game window
                dispose();
                
                // Exit the application
                System.exit(0);
            } catch (Exception e) {
                // Even if logout fails, still close the game
                dispose();
                System.exit(0);
            }
        }
    }
    
    public SnakeGameLogic getSnakeGameLogic() {
        return snakePanel.getGameLogic();
    }

    public SnakePanel getSnakePanel() {
        return snakePanel;
    }

    // Add method to control music based on game state
    public void handleGameStateChange(boolean isPlaying) {
        if (isPlaying) {
            soundManager.pauseBackgroundMusic();
        } else {
            soundManager.playBackgroundMusic();
        }
    }

    public void updateUsernameDisplay(String newUsername) {
        // Update the button panel's username display
        if (snakePanel != null) {
            Component[] components = getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JSplitPane) {
                    JSplitPane splitPane = (JSplitPane) component;
                    Component leftComponent = splitPane.getLeftComponent();
                    if (leftComponent instanceof BananaPanel) {
                        BananaPanel bananaPanel = (BananaPanel) leftComponent;
                        Component[] bananaComponents = bananaPanel.getComponents();
                        for (Component bananaComponent : bananaComponents) {
                            if (bananaComponent instanceof ButtonPanel) {
                                ButtonPanel buttonPanel = (ButtonPanel) bananaComponent;
                                buttonPanel.updateUsernameDisplay(newUsername);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}
