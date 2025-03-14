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
import interfaces.IButtonPanelController;

public class ButtonPanel extends JPanel {
    private static final long serialVersionUID = -1015522477771146689L;
    private JButton leaderboardBtn, resetBtn, settingsBtn, accountBtn;
    private ImageIcon leaderboardIcon, resetIcon, settingsIcon, accountIcon;
    private GameMainInterface gameMainInterface;
    private SnakePanel snakePanel;
    private JPanel namePanel;
    private IButtonPanelController controller;
    private JPanel pauseOverlay;

    public ButtonPanel(GameMainInterface gameMainInterface, SnakeGameLogic gameLogic, SnakePanel snakePanel) {
        this.gameMainInterface = gameMainInterface;
        this.snakePanel = snakePanel;

        // Initialize MVC components
        ButtonPanelModel model = new ButtonPanelModel(gameLogic, snakePanel);
        this.controller = new ButtonPanelController(model, this, gameMainInterface);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 65));
        setBackground(new Color(40, 40, 40));

        // Load all icons
        leaderboardIcon = resizeIcon(new ImageIcon("resources/leaderboard_icon.png"));
        resetIcon = resizeIcon(new ImageIcon("resources/reset_icon.png"));
        settingsIcon = resizeIcon(new ImageIcon("resources/settings_icon.png"));
        accountIcon = resizeIcon(new ImageIcon("resources/account_icon.png"));

        // Create all buttons
        leaderboardBtn = createStyledButton(leaderboardIcon);
        resetBtn = createResetButton(); 
        settingsBtn = createStyledButton(settingsIcon);
        accountBtn = createStyledButton(accountIcon);

        // Add button listeners
        setupButtonListeners();

        // Create and setup username display
        setupUsernameDisplay();

        // Create the three main panels
        JPanel leftPanel = createLeftPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel rightPanel = createRightPanel();

        // Add the panels to the main panel
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private void setupButtonListeners() {
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
        nameLabel.setVerticalAlignment(SwingConstants.CENTER);
        nameLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        namePanel.add(Box.createVerticalGlue());
        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalGlue());
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(leaderboardBtn);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(accountBtn);
        panel.add(Box.createHorizontalGlue());
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(Box.createHorizontalGlue());
        panel.add(namePanel);
        panel.add(Box.createHorizontalGlue());
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(Box.createHorizontalGlue());
        panel.add(settingsBtn);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(resetBtn);
        panel.add(Box.createHorizontalStrut(5));
        return panel;
    }

    private JButton createStyledButton(ImageIcon icon) {
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(30, 30));
        button.setMinimumSize(new Dimension(30, 30));
        button.setMaximumSize(new Dimension(30, 30));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
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
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        return button;
    }

    private ImageIcon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
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
