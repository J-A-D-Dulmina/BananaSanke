package view;

import javax.swing.*;
import java.awt.*;
import model.SnakeGameLogic;
import model.SessionManager;
import api.APIClient;
import model.SoundManager;
import utils.CustomDialogUtils;

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
        soundManager = SoundManager.getInstance();
        
        // Start playing background music
        soundManager.playBackgroundMusic();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Add window closing listener
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                confirmAndExit();
            }
        });

        // Initialize components
        initializeComponents();

        // Set window properties
        setSize(1000, 600);
        setResizable(false);
        
        // Center the window on screen
        setLocationRelativeTo(null);
        
        // Request focus to ensure keyboard input works
        requestFocus();
    }

    private void initializeComponents() {
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
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                splitPane.setDividerLocation(getWidth() / 2);
            }
        });

        // Add components to frame
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
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
        int confirm = CustomDialogUtils.showConfirmDialog(
            this,
            "Are you sure you want to exit the game?",
            "Exit Game"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Destroy auth token and logout user
            APIClient.logoutUser();
            SessionManager.logout();
            
            // Close the game window and exit the application
            dispose();
            System.exit(0);
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
                                buttonPanel.updateUsername(newUsername);
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
