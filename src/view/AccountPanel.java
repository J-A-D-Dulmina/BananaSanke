package view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import utils.CustomDialogUtils;
import interfaces.ISoundManager;
import interfaces.ISessionManager;
import interfaces.IAccountController;
import factory.ComponentFactory;

public class AccountPanel extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel bestScoreLabel;
    private JLabel errorLabel;
    private JLabel usernameLabel;
    private JLabel editIconLabel;
    private JPanel usernamePanel;
    private JPanel editPanel;
    private JPanel labelPanel;
    private boolean isEditingUsername = false;
    private final GameMainInterface mainFrame;
    private Image backgroundImage;
    private IAccountController controller;
    private final ISoundManager soundManager;
    private final ISessionManager sessionManager;

    public AccountPanel(GameMainInterface mainFrame) {
        super(mainFrame, "Account Settings", true);
        this.mainFrame = mainFrame;
        
        // Get dependencies from factory
        this.soundManager = ComponentFactory.getSoundManager();
        this.sessionManager = ComponentFactory.getSessionManager();
        
        ComponentFactory.createAccountModel();
        this.controller = ComponentFactory.createAccountController(this, mainFrame);
        
        // Initialize UI
        initializeUI();
        
        // Initialize view data after UI components are created
        controller.initializeView();
    }

    private void initializeUI() {
        // Load background image
        backgroundImage = new ImageIcon("resources/background_account.jpg").getImage();
        
        // Set size and position
        setSize(400, 550);
        setLocationRelativeTo(null);
        // Center vertically relative to the main frame
        int mainFrameY = mainFrame.getLocation().y;
        int mainFrameHeight = mainFrame.getHeight();
        int dialogHeight = getHeight();
        int y = mainFrameY + (mainFrameHeight - dialogHeight) / 2;
        setLocation(getX(), y);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 400, 550, 20, 20));
        
        // Initialize username components first
        usernameLabel = new JLabel(sessionManager.getUsername());
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = -7253639745702786238L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw blurred background
                int blurRadius = 40; // 80% blur effect
                int w = getWidth();
                int h = getHeight();
                
                // Create a temporary image for blurring
                Image tempImage = createImage(w, h);
                Graphics2D tempG = (Graphics2D) tempImage.getGraphics();
                tempG.drawImage(backgroundImage, 0, 0, w, h, null);
                tempG.dispose();
                
                // Apply blur effect
                for (int i = 0; i < blurRadius; i++) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                    for (int offsetX = -1; offsetX <= 1; offsetX++) {
                        for (int offsetY = -1; offsetY <= 1; offsetY++) {
                            g2.drawImage(tempImage, offsetX, offsetY, null);
                        }
                    }
                }
                
                // Draw semi-transparent overlay for better readability
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, w, h);
                
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setOpaque(false);

        // Add close button (X) to top right
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        headerPanel.setOpaque(false);

        JLabel closeButton = new JLabel("×");
        closeButton.setForeground(new Color(220, 0, 0));
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                soundManager.playButtonClickSound();
                dispose();
            }
        });

        headerPanel.add(closeButton);
        contentPanel.add(headerPanel);

        // Account icon and score section
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Account icon
        ImageIcon accountIcon = new ImageIcon("resources/account_icon.png");
        Image img = accountIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(img));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(iconLabel);
        topPanel.add(Box.createVerticalStrut(15));

        // Best Score section
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.X_AXIS));
        scorePanel.setOpaque(false);
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.setMaximumSize(new Dimension(250, 40));
        
        JPanel labelWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        labelWrapper.setOpaque(false);
        JLabel scoreLabel = new JLabel("Best Score:");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        labelWrapper.add(scoreLabel);
        
        JPanel scoreWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        scoreWrapper.setOpaque(false);
        bestScoreLabel = new JLabel("Loading...");
        bestScoreLabel.setForeground(Color.GREEN);
        bestScoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreWrapper.add(bestScoreLabel);
        
        scorePanel.add(labelWrapper);
        scorePanel.add(Box.createHorizontalStrut(5));
        scorePanel.add(scoreWrapper);
        
        topPanel.add(scorePanel);
        topPanel.add(Box.createVerticalStrut(15));

        contentPanel.add(topPanel);

        // Username section with edit icon
        setupUsernameSection();
        contentPanel.add(usernamePanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Email display section
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        emailPanel.setOpaque(false);
        emailPanel.setMaximumSize(new Dimension(340, 40));
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel emailTitleLabel = new JLabel("User Email            :");
        emailTitleLabel.setPreferredSize(new Dimension(120, 30));
        emailTitleLabel.setForeground(Color.WHITE);
        emailTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        String userEmail = sessionManager.getEmail();
        JLabel emailValueLabel = new JLabel(userEmail != null ? userEmail : "Not available");
        emailValueLabel.setForeground(Color.WHITE);
        emailValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        emailPanel.add(emailTitleLabel);
        emailPanel.add(emailValueLabel);
        
        contentPanel.add(emailPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Password section with styled fields
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setOpaque(false);
        passwordPanel.setMaximumSize(new Dimension(340, 180));
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Old Password
        JPanel oldPassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        oldPassPanel.setOpaque(false);
        JLabel oldPasswordLabel = new JLabel("Old Password       :");
        oldPasswordLabel.setPreferredSize(new Dimension(120, 30));
        oldPasswordLabel.setForeground(Color.WHITE);
        oldPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        oldPasswordField = createStyledPasswordField();
        oldPassPanel.add(oldPasswordLabel);
        oldPassPanel.add(oldPasswordField);
        
        // New Password
        JPanel newPassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        newPassPanel.setOpaque(false);
        JLabel newPasswordLabel = new JLabel("New Password      :");
        newPasswordLabel.setPreferredSize(new Dimension(120, 30));
        newPasswordLabel.setForeground(Color.WHITE);
        newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        newPasswordField = createStyledPasswordField();
        newPassPanel.add(newPasswordLabel);
        newPassPanel.add(newPasswordField);
        
        // Confirm Password
        JPanel confirmPassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        confirmPassPanel.setOpaque(false);
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setPreferredSize(new Dimension(120, 30));
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField = createStyledPasswordField();
        confirmPassPanel.add(confirmPasswordLabel);
        confirmPassPanel.add(confirmPasswordField);
        
        passwordPanel.add(oldPassPanel);
        passwordPanel.add(Box.createVerticalStrut(5));
        passwordPanel.add(newPassPanel);
        passwordPanel.add(Box.createVerticalStrut(5));
        passwordPanel.add(confirmPassPanel);
        
        contentPanel.add(passwordPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Error label
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(errorLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton updatePasswordBtn = createStyledButton("Update Password", false);
        updatePasswordBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton logoutBtn = createStyledButton("Logout", true);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(updatePasswordBtn);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(logoutBtn);
        
        contentPanel.add(buttonPanel);

        // Add action listeners
        updatePasswordBtn.addActionListener(e -> {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            controller.handlePasswordUpdate(oldPassword, newPassword, confirmPassword);
        });

        logoutBtn.addActionListener(e -> {
            int confirm = CustomDialogUtils.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout"
            );

            if (confirm == JOptionPane.YES_OPTION) {
                controller.handleLogout();
            }
        });

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void setupUsernameSection() {
        usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        usernamePanel.setBackground(new Color(40, 40, 40, 0));

        // Create username label panel
        labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        labelPanel.setBackground(new Color(40, 40, 40, 0));

        usernameLabel = new JLabel(sessionManager.getUsername());
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Create edit icon
        ImageIcon editIcon = new ImageIcon("resources/edit_pencil.png");
        Image editImg = editIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        editIconLabel = new JLabel(new ImageIcon(editImg));
        editIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editIconLabel.setToolTipText("Click to edit username");

        labelPanel.add(usernameLabel);
        labelPanel.add(editIconLabel);

        // Create username field and edit panel
        usernameField = createStyledTextField(sessionManager.getUsername());
        
        // Create edit panel with fixed width
        editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
        editPanel.setBackground(new Color(40, 40, 40, 0));
        editPanel.setPreferredSize(new Dimension(250, 35));
        editPanel.setMaximumSize(new Dimension(250, 35));
        editPanel.setMinimumSize(new Dimension(250, 35));
        
        // Add username field with fixed size
        usernameField.setPreferredSize(new Dimension(200, 30));
        usernameField.setMaximumSize(new Dimension(200, 30));
        usernameField.setMinimumSize(new Dimension(200, 30));
        editPanel.add(usernameField);
        editPanel.add(Box.createHorizontalStrut(10));
        
        // Create close button panel with fixed size
        JPanel closeButtonPanel = new JPanel(new BorderLayout());
        closeButtonPanel.setOpaque(false);
        closeButtonPanel.setPreferredSize(new Dimension(30, 30));
        closeButtonPanel.setMaximumSize(new Dimension(30, 30));
        closeButtonPanel.setMinimumSize(new Dimension(30, 30));
        
        // Create close (X) button for username edit
        JLabel closeEditButton = new JLabel("×");
        closeEditButton.setForeground(new Color(220, 0, 0));
        closeEditButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeEditButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeEditButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                soundManager.playButtonClickSound();
                cancelEditing();
            }
        });
        
        closeButtonPanel.add(closeEditButton, BorderLayout.CENTER);
        editPanel.add(closeButtonPanel);
        
        // Initially show label panel, hide edit panel
        usernamePanel.add(labelPanel);
        usernamePanel.add(editPanel);
        labelPanel.setVisible(true);
        editPanel.setVisible(false);

        // Add click listener to edit icon
        editIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                soundManager.playButtonClickSound();
                startEditing();
            }
        });

        // Add key listeners to username field
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitUsernameChange();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancelEditing();
                }
            }
        });

        // Add focus listener to username field
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // Use SwingUtilities.invokeLater to handle focus changes properly
                SwingUtilities.invokeLater(() -> {
                    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if (focusOwner != closeEditButton) {
                        submitUsernameChange();
                    }
                });
            }
        });
    }

    private void startEditing() {
        if (!isEditingUsername) {
            isEditingUsername = true;
            usernameField.setText(usernameLabel.getText());
            labelPanel.setVisible(false);
            editPanel.setVisible(true);
            usernameField.requestFocus();
            usernamePanel.revalidate();
            usernamePanel.repaint();
        }
    }

    private void submitUsernameChange() {
        if (isEditingUsername) {
            String newUsername = usernameField.getText().trim();
            if (!newUsername.isEmpty() && !newUsername.equals(usernameLabel.getText())) {
                controller.handleUsernameUpdate(newUsername);
            }
            cancelEditing();
        }
    }

    private void cancelEditing() {
        if (isEditingUsername) {
            isEditingUsername = false;
            usernameField.setText(usernameLabel.getText());
            labelPanel.setVisible(true);
            editPanel.setVisible(false);
            usernamePanel.revalidate();
            usernamePanel.repaint();
        }
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 20) {
            private static final long serialVersionUID = 6920907042275492806L;

			@Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new AbstractBorder() {
                private static final long serialVersionUID = 8800886604551493273L;

				@Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, 10, 10));
                    g2.dispose();
                }

                @Override
                public Insets getBorderInsets(Component c) {
                    return new Insets(4, 8, 4, 8);
                }
            },
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        field.setOpaque(false);
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15) {
            private static final long serialVersionUID = -9197649401410222631L;

			@Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        field.setPreferredSize(new Dimension(180, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new AbstractBorder() {
                private static final long serialVersionUID = 2616021498067549824L;

				@Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, 10, 10));
                    g2.dispose();
                }

                @Override
                public Insets getBorderInsets(Component c) {
                    return new Insets(4, 8, 4, 8);
                }
            },
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        field.setOpaque(false);
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        return field;
    }

    private JButton createStyledButton(String text, boolean isDestructive) {
        JButton button = new JButton(text) {
            private static final long serialVersionUID = 5110597267871365447L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (text.equals("Update Password")) {
                    if (getModel().isPressed()) {
                        g2.setColor(new Color(204, 163, 0)); // Darker yellow when pressed
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(255, 215, 0)); // Brighter yellow on hover
                    } else {
                        g2.setColor(new Color(255, 204, 0)); // Default yellow
                    }
                } else if (isDestructive) {
                    if (getModel().isPressed()) {
                        g2.setColor(new Color(150, 0, 0));
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(220, 0, 0));
                    } else {
                        g2.setColor(new Color(180, 0, 0));
                    }
                } else {
                    if (getModel().isPressed()) {
                        g2.setColor(new Color(50, 50, 50));
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(80, 80, 80));
                    } else {
                        g2.setColor(new Color(60, 60, 60));
                    }
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();

                super.paintComponent(g);
            }
        };
        
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        return button;
    }

    // Public methods for controller to update UI
    public void setUsername(String username) {
        usernameLabel.setText(username);
        usernameField.setText(username);
    }

    public void setEmail(String email) {
        // Update email display
        // ... (implementation depends on your UI)
    }

    public void setBestScore(int score) {
        SwingUtilities.invokeLater(() -> {
            bestScoreLabel.setText(String.valueOf(score));
        });
    }

    public void showSuccessMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setForeground(new Color(0, 200, 0));
        startMessageTimer();
    }

    public void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setForeground(Color.RED);
        startMessageTimer();
    }

    public void clearPasswordFields() {
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private void startMessageTimer() {
        Timer timer = new Timer(2000, e -> {
            errorLabel.setText("");
            errorLabel.setForeground(Color.RED);
        });
        timer.setRepeats(false);
        timer.start();
    }
} 