package view;

import controller.RegisterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.border.AbstractBorder;
import java.awt.geom.RoundRectangle2D;

/**
 * Register UI dialog for user account creation.
 */
public class RegisterUI extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8669527432326739701L;
	private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JLabel messageLabel, backgroundLabel, backToLoginLabel;
    private final RegisterController controller;

    public RegisterUI(JFrame parent) {
        super(parent, "Register", true);
        setSize(500, 450);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        controller = new RegisterController(this);  // Initialize the controller

        initializeComponents();
    }

    private void initializeComponents() {
        backgroundLabel = createBackgroundLabel("resources/background_image login.png");
        add(backgroundLabel, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = createLabel("Create an Account", Color.WHITE, new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, gbc);

        // Username section
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 2, 10);
        panel.add(createLabel("Username", Color.WHITE, new Font("Arial", Font.BOLD, 14)), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = createPlaceholderField("3-20 characters, letters and numbers only");
        panel.add(usernameField, gbc);

        // Email section
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 10, 2, 10);
        panel.add(createLabel("Email Address", Color.WHITE, new Font("Arial", Font.BOLD, 14)), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = createPlaceholderField("Enter valid email address");
        panel.add(emailField, gbc);

        // Password section
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 10, 2, 10);
        panel.add(createLabel("Password", Color.WHITE, new Font("Arial", Font.BOLD, 14)), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = createPlaceholderPasswordField("Minimum 6 characters");
        panel.add(passwordField, gbc);

        // Register button
        gbc.gridy++;
        gbc.insets = new Insets(15, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        registerButton = createButton("Register", new Color(0, 200, 0), Color.WHITE, e -> validateAndRegister());
        panel.add(registerButton, gbc);

        // Back to Login link
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 5, 10);
        backToLoginLabel = createLabel("Back to Login", Color.YELLOW, new Font("Arial", Font.BOLD, 14));
        backToLoginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            }
        });
        panel.add(backToLoginLabel, gbc);

        // Message label
        gbc.gridy++;
        messageLabel = createLabel("", Color.RED, null);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, gbc);

        backgroundLabel.add(panel);
    }

    private JLabel createBackgroundLabel(String imagePath) {
        ImageIcon backgroundImage = new ImageIcon(imagePath);
        Image scaledImage = backgroundImage.getImage().getScaledInstance(500, 450, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setLayout(new GridBagLayout());
        return label;
    }

    private JLabel createLabel(String text, Color color, Font font) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setForeground(color);
        if (font != null) {
            label.setFont(font);
        }
        return label;
    }

    private JTextField createPlaceholderField(String placeholder) {
        JTextField field = new JTextField(20);
        configurePlaceholderField(field, placeholder);
        return field;
    }

    private JPasswordField createPlaceholderPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        configurePlaceholderField(field, placeholder);
        field.setEchoChar((char) 0);
        return field;
    }

    private void configurePlaceholderField(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setText(placeholder);
        field.setPreferredSize(new Dimension(280, 45));  // Made wider to accommodate validation text
        
        // Make field non-opaque for custom painting
        field.setOpaque(false);
        
        // Custom rounded border and background
        field.setBorder(BorderFactory.createCompoundBorder(
            new AbstractBorder() {
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
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        // Add custom background painting
        field.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintBackground(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, field.getWidth(), field.getHeight(), 10, 10));
                g2.dispose();
            }
        });

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('*');
                    }
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }

    private JButton createButton(String text, Color bgColor, Color textColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setPreferredSize(new Dimension(140, 40));
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }
    

    private void addComponent(JPanel panel, JComponent component, GridBagConstraints gbc, int x, int y, int gridWidth) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridWidth;
        panel.add(component, gbc);
    }
    
    public void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setForeground(success ? Color.GREEN : Color.RED); // Green for success, red for error
    }


    private void validateAndRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Disable the Register button to prevent multiple clicks
        registerButton.setEnabled(false);

        // Validate username
        if (username.isEmpty() || username.equals("3-20 characters, letters and numbers only")) {
            showMessage("Username is required!", false);
            registerButton.setEnabled(true);
            return;
        }
        if (!username.matches("^[a-zA-Z0-9]{3,20}$")) {
            showMessage("Username must be 3-20 characters long and contain only letters and numbers!", false);
            registerButton.setEnabled(true);
            return;
        }

        // Validate email
        if (email.isEmpty() || email.equals("Enter valid email address")) {
            showMessage("Email is required!", false);
            registerButton.setEnabled(true);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showMessage("Please enter a valid email address!", false);
            registerButton.setEnabled(true);
            return;
        }

        // Validate password
        if (password.isEmpty() || password.equals("Minimum 6 characters")) {
            showMessage("Password is required!", false);
            registerButton.setEnabled(true);
            return;
        }
        if (password.length() < 6) {
            showMessage("Password must be at least 6 characters!", false);
            registerButton.setEnabled(true);
            return;
        }

        // Introduce a 2-second delay before calling the registration method
        Timer timer = new Timer(2000, e -> {
            // Actual registration logic after delay
            controller.handleRegister(username, email, password);
        });

        timer.setRepeats(false); // Only run once
        timer.start();
    }


    public void showMessage(String message) {
        messageLabel.setText(message);
    }
}
