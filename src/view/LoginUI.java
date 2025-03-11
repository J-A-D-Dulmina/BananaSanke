package view;

import javax.swing.*;
import controller.LoginController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.border.AbstractBorder;
import java.awt.geom.RoundRectangle2D;

/**
 * Login UI for user authentication.
 */
public class LoginUI extends JFrame {
    private static final long serialVersionUID = -4454754220864018946L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JLabel messageLabel, backgroundLabel, backToRegisterLabel;
    private final LoginController controller;

    /**
     * Constructs the login UI window.
     */
    public LoginUI() {
        setTitle("Game Login");
        setSize(500, 450);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        controller = new LoginController(this);
        initializeComponents();
    }

    /**
     * Initializes UI components and layouts.
     */
    private void initializeComponents() {
        backgroundLabel = createBackgroundLabel("resources/background_image login.png");
        add(backgroundLabel, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Game Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        addComponent(panel, titleLabel, gbc, 0, 0, 2);

        emailField = createPlaceholderField("Email");
        addComponent(panel, emailField, gbc, 0, 1, 2);

        passwordField = createPlaceholderPasswordField("Password");
        addComponent(panel, passwordField, gbc, 0, 2, 2);

        loginButton = createButton("Login", Color.YELLOW, Color.BLACK, 
        	    e -> validateAndLogin());
        addComponent(panel, loginButton, gbc, 0, 3, 2);

        registerButton = createButton("Register", new Color(0, 200, 0), Color.WHITE, 
            e -> openRegisterUI());
        addComponent(panel, registerButton, gbc, 0, 4, 2);

        backToRegisterLabel = createLabel("Forget Password", Color.YELLOW, new Font("Arial", Font.BOLD, 14));
        backToRegisterLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToRegisterLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> new ForgotPasswordUI(LoginUI.this).setVisible(true));
            }
        });
        addComponent(panel, backToRegisterLabel, gbc, 0, 5, 2);

        messageLabel = createLabel("", Color.RED, null);
        addComponent(panel, messageLabel, gbc, 0, 6, 2);

        backgroundLabel.add(panel);
    }
    
    private void validateAndLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Ensure the user is not using placeholder values
        if (email.equals("Email") || email.isEmpty()) {
            showMessage("Please enter your email!", false);
            return;
        }

        if (password.equals("Password") || password.isEmpty()) {
            showMessage("Please enter your password!", false);
            return;
        }

        // Call login controller
        controller.handleLogin(email, password);
    }

    private JLabel createBackgroundLabel(String imagePath) {
        ImageIcon backgroundImage = new ImageIcon(imagePath);
        Image scaledImage = backgroundImage.getImage().getScaledInstance(500, 450, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setLayout(new GridBagLayout());
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
        field.setPreferredSize(new Dimension(220, 45));
        
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

    private JLabel createLabel(String text, Color color, Font font) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(color);
        if (font != null) {
            label.setFont(font);
        }
        return label;
    }

    private void addComponent(JPanel panel, JComponent component, GridBagConstraints gbc, int x, int y, int gridWidth) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridWidth;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(component, gbc);
    }

    private void openRegisterUI() {
        dispose();
        SwingUtilities.invokeLater(() -> new RegisterUI(this).setVisible(true));
    }

    public void showMessage(String message, boolean success) {
        messageLabel.setForeground(success ? Color.GREEN : Color.RED);
        messageLabel.setText(message);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
