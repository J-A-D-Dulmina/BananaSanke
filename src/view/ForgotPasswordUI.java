package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;
import api.APIClient;
import org.json.JSONObject;
import factory.ComponentFactory;
import interfaces.IAPIClient;

/**
 * Forgot Password UI dialog allowing users to reset their password via email.
 */
public class ForgotPasswordUI extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 819206079673105794L;
	private JTextField emailField, usernameField;
    private JButton sendEmailButton;
    private JLabel messageLabel, backgroundLabel, backToLoginLabel;
    private final IAPIClient apiClient;

    /**
     * Constructs the Forgot Password UI dialog.
     * @param parent The parent JFrame from which this dialog is launched.
     */
    public ForgotPasswordUI(JFrame parent) {
        super(parent, "Forgot Password", true);
        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
//        setUndecorated(true);

        this.apiClient = ComponentFactory.getAPIClient();

        // Ensures Login UI reopens when the dialog is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                new LoginUI().setVisible(true);
            }
        });

        initializeComponents();
    }

    /**
     * Initializes all UI components and layouts.
     */
    private void initializeComponents() {
        backgroundLabel = createBackgroundLabel("resources/background_image login.png");
        add(backgroundLabel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Title
        JLabel titleLabel = createLabel("Forgot Password", Color.WHITE, new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Username section
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.setOpaque(false);
        usernamePanel.setMaximumSize(new Dimension(220, 80));

        JLabel usernameLabel = createLabel("Username", Color.WHITE, new Font("Arial", Font.BOLD, 14));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = createPlaceholderField("Enter your username");
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        usernamePanel.add(usernameLabel);
        usernamePanel.add(Box.createVerticalStrut(5));
        usernamePanel.add(usernameField);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 140, 5, 140);
        mainPanel.add(usernamePanel, gbc);

        // Email section
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setOpaque(false);
        emailPanel.setMaximumSize(new Dimension(220, 80));

        JLabel emailLabel = createLabel("Email Address", Color.WHITE, new Font("Arial", Font.BOLD, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField = createPlaceholderField("Enter your email");
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        emailPanel.add(emailLabel);
        emailPanel.add(Box.createVerticalStrut(5));
        emailPanel.add(emailField);
        
        gbc.gridy = 2;
        mainPanel.add(emailPanel, gbc);

        // Send Email button
        sendEmailButton = createButton("Send Email", Color.GREEN, Color.WHITE, e -> sendResetEmail());
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(sendEmailButton, gbc);

        // Back to Login link
        backToLoginLabel = createLabel("Back to Login", Color.YELLOW, new Font("Arial", Font.BOLD, 14));
        backToLoginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            }
        });
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 5, 10);
        mainPanel.add(backToLoginLabel, gbc);

        // Message label
        messageLabel = createLabel("", Color.RED, null);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        mainPanel.add(messageLabel, gbc);

        backgroundLabel.add(mainPanel);
    }

    /**
     * Creates a background label with an image.
     * @param imagePath Path to the background image.
     * @return JLabel with the background image.
     */
    private JLabel createBackgroundLabel(String imagePath) {
        ImageIcon backgroundImage = new ImageIcon(imagePath);
        Image scaledImage = backgroundImage.getImage().getScaledInstance(500, 400, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setLayout(new GridBagLayout());
        return label;
    }

    /**
     * Creates a JLabel with specified text, color, and font.
     * @param text The label text.
     * @param color The text color.
     * @param font The font style (nullable).
     * @return A styled JLabel.
     */
    private JLabel createLabel(String text, Color color, Font font) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        if (font != null) {
            label.setFont(font);
        }
        return label;
    }

    /**
     * Creates a placeholder-styled JTextField.
     * @param placeholder The placeholder text.
     * @return A JTextField with placeholder functionality.
     */
    private JTextField createPlaceholderField(String placeholder) {
        JTextField field = new JTextField(20) {
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

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(220, 45);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setText(placeholder);
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
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        field.setPreferredSize(new Dimension(220, 45));
        field.setMinimumSize(new Dimension(220, 45));
        field.setMaximumSize(new Dimension(220, 45));
        field.setOpaque(true);
        field.setCaretColor(Color.BLACK);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        return field;
    }

    /**
     * Configures placeholder behavior for a JTextField.
     * @param field The text field to configure.
     * @param placeholder The placeholder text.
     */
    private void configurePlaceholderField(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setText(placeholder);
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
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        field.setPreferredSize(new Dimension(220, 45));
        field.setMaximumSize(new Dimension(220, 45));
        field.setOpaque(true);
        field.setCaretColor(Color.BLACK);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    /**
     * Creates a styled JButton.
     * @param text Button text.
     * @param bgColor Background color.
     * @param textColor Text color.
     * @param action Action listener for button click.
     * @return A styled JButton.
     */
    private JButton createButton(String text, Color bgColor, Color textColor, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background color based on state
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                
                // Draw the text
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g.setColor(textColor);
                g.drawString(getText(), x, y);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(140, 40);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        return button;
    }

    /**
     * Handles the reset email sending logic.
     */
    private void sendResetEmail() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || email.isEmpty()) {
            showMessage("Please enter both username and email", false);
            return;
        }

        try {
            String response = apiClient.requestPasswordReset(username, email);
            JSONObject jsonResponse = new JSONObject(response);
            
            if (jsonResponse.getString("status").equals("success")) {
                showMessage("Password reset instructions sent to your email", true);
                
                // Close this dialog and open reset password UI
                dispose();
                SwingUtilities.invokeLater(() -> {
                    ResetPasswordUI resetUI = new ResetPasswordUI(null, username, email);
                    resetUI.setVisible(true);
                });
            } else {
                String errorMessage = jsonResponse.optString("message", "Unknown error occurred");
                showMessage(errorMessage, false);
            }
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    /**
     * Displays a message in the UI.
     * @param message The message text.
     * @param success True if the message is a success message, false otherwise.
     */
    private void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setForeground(success ? Color.GREEN : Color.RED);
    }
}
