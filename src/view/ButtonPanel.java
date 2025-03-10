package view;

import javax.swing.*;
import java.awt.*;
import model.SnakeGameLogic;
import api.APIClient;
import model.SessionManager;

public class ButtonPanel extends JPanel {
    private static final long serialVersionUID = -1015522477771146689L;
    private JButton leaderboardBtn, playPauseBtn, resetBtn, logoutBtn, settingsBtn, accountBtn;
    private ImageIcon leaderboardIcon, pauseIcon, resetIcon, logoutIcon, settingsIcon, accountIcon;
    private GameMainInterface gameMainInterface;
    private SnakeGameLogic gameLogic; 
    private SnakePanel snakePanel;

    public ButtonPanel(GameMainInterface gameMainInterface, SnakeGameLogic gameLogic, SnakePanel snakePanel) { 
        this.gameMainInterface = gameMainInterface;
        this.gameLogic = gameLogic; 
        this.snakePanel = snakePanel;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(500, 65));
        setBackground(new Color(40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.VERTICAL;

        leaderboardIcon = resizeIcon(new ImageIcon("resources/leaderboard_icon.png"));
        pauseIcon = resizeIcon(new ImageIcon("resources/pause_icon.png"));
        resetIcon = resizeIcon(new ImageIcon("resources/reset_icon.png"));
        logoutIcon = resizeIcon(new ImageIcon("resources/Logout.png"));
        settingsIcon = resizeIcon(new ImageIcon("resources/settings_icon.png"));
        accountIcon = resizeIcon(new ImageIcon("resources/account_icon.png"));

        leaderboardBtn = createStyledButton(leaderboardIcon);
        playPauseBtn = createStyledButton(pauseIcon);
        resetBtn = createResetButton(); 
        logoutBtn = createLogoutButton();
        settingsBtn = createStyledButton(settingsIcon);
        accountBtn = createStyledButton(accountIcon);

        playPauseBtn.addActionListener(e -> {
            boolean isCurrentlyRunning = gameLogic.isRunning();
            gameLogic.setRunning(!isCurrentlyRunning);
            
            if (!isCurrentlyRunning) {
                playPauseBtn.setIcon(pauseIcon);
                snakePanel.hidePauseOverlay();
            } else {
                playPauseBtn.setIcon(resizeIcon(new ImageIcon("resources/play_icon.png")));
                snakePanel.showPauseOverlay();
            }
        });

        settingsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Settings coming soon!");
        });

        accountBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Account settings coming soon!");
        });

        String loggedInUsername = SessionManager.getUsername();
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            loggedInUsername = "Guest"; 
        }

        JLabel nameLabel = new JLabel(loggedInUsername, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);

        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setOpaque(false);
        namePanel.setBorder(BorderFactory.createEmptyBorder(0, 90, 0, 90));
        namePanel.add(nameLabel, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(logoutBtn, gbc);

        gbc.gridx = 1;
        add(leaderboardBtn, gbc);

        gbc.gridx = 2;
        add(accountBtn, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 2;
        add(namePanel, gbc);

        gbc.gridx = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(settingsBtn, gbc);
        
        gbc.gridx = 6;
        add(resetBtn, gbc);
        
        gbc.gridx = 7;
        add(playPauseBtn, gbc);

        setVerticalAlignment();
        
        leaderboardBtn.addActionListener(e -> gameMainInterface.showLeaderboard());
    }

    private JButton createResetButton() {
        JButton button = new JButton(resetIcon);
        button.setPreferredSize(new Dimension(30, 30));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset the game?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("Reset button clicked!");  
                gameLogic.reset();  
                
                snakePanel.resetToStart();

                System.out.println("UI repaint triggered!");
            }
        });

        return button;
    }

    private ImageIcon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private JButton createStyledButton(ImageIcon icon) {
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(30, 30));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }
    
    
    
    private JButton createResettButton() {
        JButton button = new JButton(logoutIcon);
        button.setPreferredSize(new Dimension(30, 30)); 
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        button.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                gameMainInterface,
                "Are you sure you want to Reset?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
               
            }
        });

        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton(logoutIcon);
        button.setPreferredSize(new Dimension(30, 30)); 
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        button.addActionListener(e -> {
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

        return button;
    }

    private void setVerticalAlignment() {
        Component[] components = getComponents();
        for (Component comp : components) {
            GridBagConstraints gbc = ((GridBagLayout) getLayout()).getConstraints(comp);
            gbc.anchor = GridBagConstraints.CENTER;
        }
    }
}
