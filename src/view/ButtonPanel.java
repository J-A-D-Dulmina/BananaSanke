package view;

import javax.swing.*;
import java.awt.*;
import model.SnakeGameLogic;
import api.APIClient;
import model.SessionManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.SoundManager;

public class ButtonPanel extends JPanel {
    private static final long serialVersionUID = -1015522477771146689L;
    private JButton leaderboardBtn, playPauseBtn, resetBtn, logoutBtn, settingsBtn, accountBtn;
    private ImageIcon leaderboardIcon, pauseIcon, resetIcon, logoutIcon, settingsIcon, accountIcon;
    private GameMainInterface gameMainInterface;
    private SnakeGameLogic gameLogic; 
    private SnakePanel snakePanel;
    private JPanel namePanel;

    public ButtonPanel(GameMainInterface gameMainInterface, SnakeGameLogic gameLogic, SnakePanel snakePanel) { 
        this.gameMainInterface = gameMainInterface;
        this.gameLogic = gameLogic; 
        this.snakePanel = snakePanel;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(600, 65));
        setBackground(new Color(40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.VERTICAL;

        // Load all icons
        leaderboardIcon = resizeIcon(new ImageIcon("resources/leaderboard_icon.png"));
        pauseIcon = resizeIcon(new ImageIcon("resources/pause_icon.png"));
        resetIcon = resizeIcon(new ImageIcon("resources/reset_icon.png"));
        logoutIcon = resizeIcon(new ImageIcon("resources/Logout.png"));
        settingsIcon = resizeIcon(new ImageIcon("resources/settings_icon.png"));
        accountIcon = resizeIcon(new ImageIcon("resources/account_icon.png"));

        // Create all buttons
        leaderboardBtn = createStyledButton(leaderboardIcon);
        playPauseBtn = createStyledButton(pauseIcon);
        resetBtn = createResetButton(); 
        logoutBtn = createLogoutButton();
        settingsBtn = createStyledButton(settingsIcon);
        accountBtn = createStyledButton(accountIcon);

        // Add button listeners
        setupButtonListeners();

        // Create and setup username display
        setupUsernameDisplay();

        // Add components to panel
        addComponentsToPanel(gbc);

        setVerticalAlignment();
    }

    private void setupButtonListeners() {
        // Play/Pause button
        playPauseBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            if (!snakePanel.isGameStarted()) {
                // If game hasn't started, start it
                snakePanel.startGame();
                playPauseBtn.setIcon(pauseIcon);
            } else {
                // If game has started, toggle pause state
                boolean isCurrentlyRunning = gameLogic.isRunning();
                gameLogic.setRunning(!isCurrentlyRunning);
                
                if (!isCurrentlyRunning) {
                    playPauseBtn.setIcon(pauseIcon);
                    snakePanel.hidePauseOverlay();
                } else {
                    playPauseBtn.setIcon(resizeIcon(new ImageIcon("resources/play_icon.png")));
                    snakePanel.showPauseOverlay();
                }
            }
        });

        // Settings button
        settingsBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            // Pause the game if it's running
            boolean wasRunning = gameLogic.isRunning();
            if (wasRunning) {
                gameLogic.setRunning(false);
                playPauseBtn.setIcon(resizeIcon(new ImageIcon("resources/play_icon.png")));
                snakePanel.showPauseOverlay();
            }
            SettingsPanel settingsPanel = new SettingsPanel(gameMainInterface);
            settingsPanel.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    snakePanel.requestFocusInWindow();
                }
            });
            settingsPanel.setVisible(true);
        });

        // Account button
        accountBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            // Pause the game if it's running
            boolean wasRunning = gameLogic.isRunning();
            if (wasRunning) {
                gameLogic.setRunning(false);
                playPauseBtn.setIcon(resizeIcon(new ImageIcon("resources/play_icon.png")));
                snakePanel.showPauseOverlay();
            }
            AccountPanel accountPanel = new AccountPanel(gameMainInterface);
            accountPanel.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    snakePanel.requestFocusInWindow();
                }
            });
            accountPanel.setVisible(true);
        });

        // Leaderboard button
        leaderboardBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            // Pause the game if it's running
            boolean wasRunning = gameLogic.isRunning();
            if (wasRunning) {
                gameLogic.setRunning(false);
                playPauseBtn.setIcon(resizeIcon(new ImageIcon("resources/play_icon.png")));
                snakePanel.showPauseOverlay();
            }
            gameMainInterface.showLeaderboard();
            snakePanel.requestFocusInWindow();
        });

        // Reset button
        resetBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset the game?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                gameLogic.reset();  
                snakePanel.resetToStart();
                snakePanel.requestFocusInWindow();
            }
        });

        // Logout button
        logoutBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            int confirm = JOptionPane.showConfirmDialog(
                gameMainInterface,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                APIClient.logoutUser();
                gameMainInterface.dispose();  
                LoginUI loginUI = new LoginUI();
                loginUI.setVisible(true);
            }
        });

        // Pause overlay click listener
        snakePanel.addPauseOverlayClickListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameLogic.isRunning()) {
                    SoundManager.getInstance().playButtonClickSound();
                    gameLogic.setRunning(true);
                    playPauseBtn.setIcon(pauseIcon);
                    snakePanel.hidePauseOverlay();
                    snakePanel.requestFocusInWindow();
                }
            }
        });
    }

    private void setupUsernameDisplay() {
        String loggedInUsername = SessionManager.getUsername();
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            loggedInUsername = "Guest"; 
        }

        JLabel nameLabel = new JLabel(loggedInUsername, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);

        namePanel = new JPanel(new BorderLayout());
        namePanel.setOpaque(false);
        namePanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        namePanel.add(nameLabel, BorderLayout.CENTER);
    }

    private void addComponentsToPanel(GridBagConstraints gbc) {
        // Left side buttons (logout, leaderboard, account)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 5);
        add(logoutBtn, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(leaderboardBtn, gbc);

        gbc.gridx = 2;
        add(accountBtn, gbc);

        // Center username
        gbc.gridx = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 20, 5, 20);
        add(namePanel, gbc);

        // Right side buttons (settings, reset, play/pause)
        gbc.gridx = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(settingsBtn, gbc);
        
        gbc.gridx = 6;
        add(resetBtn, gbc);
        
        gbc.gridx = 7;
        gbc.insets = new Insets(5, 5, 5, 10);
        add(playPauseBtn, gbc);
    }

    private JButton createStyledButton(ImageIcon icon) {
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(30, 30));
        button.setMinimumSize(new Dimension(30, 30));
        button.setMaximumSize(new Dimension(30, 30));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private JButton createResetButton() {
        JButton button = new JButton(resetIcon);
        button.setPreferredSize(new Dimension(30, 30));
        button.setMinimumSize(new Dimension(30, 30));
        button.setMaximumSize(new Dimension(30, 30));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton(logoutIcon);
        button.setPreferredSize(new Dimension(30, 30));
        button.setMinimumSize(new Dimension(30, 30));
        button.setMaximumSize(new Dimension(30, 30));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private ImageIcon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void setVerticalAlignment() {
        Component[] components = getComponents();
        for (Component comp : components) {
            GridBagConstraints gbc = ((GridBagLayout) getLayout()).getConstraints(comp);
            gbc.anchor = GridBagConstraints.CENTER;
        }
    }

    public void updateUsername(String newUsername) {
        if (namePanel != null) {
            Component[] components = namePanel.getComponents();
            for (Component component : components) {
                if (component instanceof JLabel) {
                    JLabel nameLabel = (JLabel) component;
                    nameLabel.setText(newUsername);
                    namePanel.revalidate();
                    namePanel.repaint();
                    break;
                }
            }
        }
    }
}
