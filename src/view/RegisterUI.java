package view;

import controller.RegisterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        addComponent(panel, createLabel("Create an Account", Color.WHITE, new Font("SansSerif", Font.BOLD, 28)), gbc, 0, 0, 2);

        usernameField = createPlaceholderField("Username");
        addComponent(panel, usernameField, gbc, 0, 1, 2);

        emailField = createPlaceholderField("Email");
        addComponent(panel, emailField, gbc, 0, 2, 2);

        passwordField = createPlaceholderPasswordField("Password");
        addComponent(panel, passwordField, gbc, 0, 3, 2);

        registerButton = createButton("Register", new Color(0, 200, 0), Color.WHITE, e -> validateAndRegister());
        addComponent(panel, registerButton, gbc, 0, 4, 2);

        backToLoginLabel = createLabel("Back to Login", Color.YELLOW, new Font("Arial", Font.BOLD, 14));
        backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            }
        });
        addComponent(panel, backToLoginLabel, gbc, 0, 5, 2);

        messageLabel = createLabel("", Color.RED, null);
        addComponent(panel, messageLabel, gbc, 0, 6, 2);

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
        JLabel label = new JLabel(text, SwingConstants.CENTER);
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
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setPreferredSize(new Dimension(220, 45));
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
        gbc.anchor = GridBagConstraints.CENTER;
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

        // Validate input fields
        if (username.isEmpty() || username.equals("Username")) {
            showMessage("Username is required!", false);
            registerButton.setEnabled(true);
            return;
        }
        if (email.isEmpty() || email.equals("Email") || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showMessage("Enter a valid email!", false);
            registerButton.setEnabled(true);
            return;
        }
        if (password.isEmpty() || password.equals("Password") || password.length() < 6) {
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
