package view;

import javax.swing.*;
import java.awt.*;
import model.SnakeGameLogic;
import model.ButtonPanelModel;
import controller.ButtonPanelController;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.SoundManager;
import utils.CustomDialogUtils;

public class ButtonPanel extends JPanel {
    private static final long serialVersionUID = -1015522477771146689L;
    private JButton leaderboardBtn, playPauseBtn, resetBtn, logoutBtn, settingsBtn, accountBtn;
    private ImageIcon leaderboardIcon, pauseIcon, resetIcon, logoutIcon, settingsIcon, accountIcon;
    private GameMainInterface gameMainInterface;
    private SnakePanel snakePanel;
    private JPanel namePanel;
    private ButtonPanelController controller;

    public ButtonPanel(GameMainInterface gameMainInterface, SnakeGameLogic gameLogic, SnakePanel snakePanel) {
        this.gameMainInterface = gameMainInterface;
        this.snakePanel = snakePanel;

        // Initialize MVC components
        ButtonPanelModel model = new ButtonPanelModel(gameLogic, snakePanel);
        this.controller = new ButtonPanelController(model, this, gameMainInterface);

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
            controller.handlePlayPause();
        });

        // Settings button
        settingsBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            controller.handleSettings();
        });

        // Account button
        accountBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            controller.handleAccount();
        });

        // Leaderboard button
        leaderboardBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            controller.handleLeaderboard();
        });

        // Reset button
        resetBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            controller.handleReset();
        });

        // Logout button
        logoutBtn.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            controller.handleLogout();
        });

        // Pause overlay click listener
        snakePanel.addPauseOverlayClickListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.handlePauseOverlayClick();
            }
        });
    }

    private void setupUsernameDisplay() {
        String loggedInUsername = controller.getModel().getUsername();
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

    public void setPlayPauseIcon(String state) {
        if (state.equals("pause")) {
            playPauseBtn.setIcon(pauseIcon);
        } else {
            playPauseBtn.setIcon(resizeIcon(new ImageIcon("resources/play_icon.png")));
        }
    }

    public void showPauseOverlay() {
        snakePanel.showPauseOverlay();
    }

    public void hidePauseOverlay() {
        snakePanel.hidePauseOverlay();
    }

    public void updateUsernameDisplay(String newUsername) {
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
