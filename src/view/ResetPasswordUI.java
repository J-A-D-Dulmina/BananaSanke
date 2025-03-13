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

public class ResetPasswordUI extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField tokenField;
    private JPasswordField newPasswordField, confirmPasswordField;
    private JButton resetButton, resendTokenButton;
    private JLabel messageLabel, backgroundLabel, backToLoginLabel;
    private String username;
    private String email;
    private Timer cooldownTimer;
    private int cooldownSeconds = 60;

    public ResetPasswordUI(JFrame parent, String username, String email) {
        super(parent, "Reset Password", true);
        this.username = username;
        this.email = email;
        setSize(450, 550);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        initializeComponents();
        setupCooldownTimer();
    }

    private void setupCooldownTimer() {
        cooldownTimer = new Timer(1000, e -> {
            if (cooldownSeconds > 0) {
                cooldownSeconds--;
                resendTokenButton.setText("Resend Token (" + cooldownSeconds + "s)");
                resendTokenButton.setEnabled(false);
            } else {
                resendTokenButton.setText("Resend Token");
                resendTokenButton.setEnabled(true);
                cooldownTimer.stop();
            }
        });
    }

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
        JLabel titleLabel = createLabel("Reset Password", Color.WHITE, new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Token section
        JPanel tokenPanel = new JPanel();
        tokenPanel.setLayout(new BoxLayout(tokenPanel, BoxLayout.Y_AXIS));
        tokenPanel.setOpaque(false);
        tokenPanel.setMaximumSize(new Dimension(220, 120));

        JLabel tokenLabel = createLabel("Reset Token", Color.WHITE, new Font("Arial", Font.BOLD, 14));
        tokenLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tokenField = createPlaceholderField("Enter reset token from email");
        tokenField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add resend token button
        resendTokenButton = createButton("Resend Token", Color.BLUE, Color.WHITE, e -> resendToken());
        resendTokenButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resendTokenButton.setEnabled(false); // Initially disabled
        
        tokenPanel.add(tokenLabel);
        tokenPanel.add(Box.createVerticalStrut(5));
        tokenPanel.add(tokenField);
        tokenPanel.add(Box.createVerticalStrut(5));
        tokenPanel.add(resendTokenButton);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 140, 5, 140);
        mainPanel.add(tokenPanel, gbc);

        // New Password section
        JPanel newPasswordPanel = new JPanel();
        newPasswordPanel.setLayout(new BoxLayout(newPasswordPanel, BoxLayout.Y_AXIS));
        newPasswordPanel.setOpaque(false);
        newPasswordPanel.setMaximumSize(new Dimension(220, 80));

        JLabel newPasswordLabel = createLabel("New Password", Color.WHITE, new Font("Arial", Font.BOLD, 14));
        newPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        newPasswordField = createPasswordField("Enter new password");
        newPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        newPasswordPanel.add(newPasswordLabel);
        newPasswordPanel.add(Box.createVerticalStrut(5));
        newPasswordPanel.add(newPasswordField);
        
        gbc.gridy = 2;
        mainPanel.add(newPasswordPanel, gbc);

        // Confirm Password section
        JPanel confirmPasswordPanel = new JPanel();
        confirmPasswordPanel.setLayout(new BoxLayout(confirmPasswordPanel, BoxLayout.Y_AXIS));
        confirmPasswordPanel.setOpaque(false);
        confirmPasswordPanel.setMaximumSize(new Dimension(220, 80));

        JLabel confirmPasswordLabel = createLabel("Confirm Password", Color.WHITE, new Font("Arial", Font.BOLD, 14));
        confirmPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordField = createPasswordField("Confirm new password");
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        confirmPasswordPanel.add(confirmPasswordLabel);
        confirmPasswordPanel.add(Box.createVerticalStrut(5));
        confirmPasswordPanel.add(confirmPasswordField);
        
        gbc.gridy = 3;
        mainPanel.add(confirmPasswordPanel, gbc);

        // Reset Password button
        resetButton = createButton("Reset Password", Color.GREEN, Color.WHITE, e -> resetPassword());
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(resetButton, gbc);

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
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 10, 5, 10);
        mainPanel.add(backToLoginLabel, gbc);

        // Message label
        messageLabel = createLabel("", Color.RED, null);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6;
        mainPanel.add(messageLabel, gbc);

        backgroundLabel.add(mainPanel);
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20) {
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
                
                if (String.valueOf(getPassword()).isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, 
                        getInsets().left, 
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g2.dispose();
                }
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
        
        field.setForeground(Color.BLACK);
        field.setBackground(Color.WHITE);
        field.setEchoChar((char) 0); // Initially show placeholder
        field.setText("");
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
        field.setOpaque(false);
        field.setCaretColor(Color.BLACK);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setEchoChar('•');
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }

    private void resetPassword() {
        String token = tokenField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        try {
            if (token.isEmpty() || token.equals("Enter reset token from email")) {
                showMessage("Please enter the reset token from your email!", false);
                return;
            }

            if (newPassword.isEmpty()) {
                showMessage("Please enter a new password!", false);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showMessage("Passwords do not match!", false);
                return;
            }

            String response = APIClient.verifyResetToken(username, token, newPassword);
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                showMessage("Password reset successful!", true);
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            } else {
                showMessage(jsonResponse.getString("message"), false);
            }
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), false);
            e.printStackTrace();
        } finally {
            // Clear sensitive data
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        }
    }

    private void resendToken() {
        try {
            String response = APIClient.requestPasswordReset(username, email);
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                showMessage("New reset token sent to your email!", true);
                // Start cooldown timer
                cooldownSeconds = 60;
                resendTokenButton.setText("Resend Token (" + cooldownSeconds + "s)");
                resendTokenButton.setEnabled(false);
                cooldownTimer.start();
            } else {
                showMessage(jsonResponse.getString("message"), false);
            }
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    // Reuse the same UI component creation methods from ForgotPasswordUI
    private JLabel createBackgroundLabel(String imagePath) {
        ImageIcon backgroundImage = new ImageIcon(imagePath);
        Image scaledImage = backgroundImage.getImage().getScaledInstance(450, 550, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setLayout(new GridBagLayout());
        return label;
    }

    private JLabel createLabel(String text, Color color, Font font) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        if (font != null) {
            label.setFont(font);
        }
        return label;
    }

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

    private JButton createButton(String text, Color bgColor, Color textColor, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                
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

    private void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setForeground(success ? Color.GREEN : Color.RED);
    }

    // Add this new method for styling password fields
    private void stylePasswordField(JPasswordField field, String placeholder) {
        field.setEchoChar((char) 0); // Initially show placeholder text
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setPreferredSize(new Dimension(220, 45));
        field.setMaximumSize(new Dimension(220, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setCaretColor(Color.BLACK);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•'); // Show dots when typing
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setEchoChar((char) 0); // Show placeholder text
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Add this inner class for rounded borders
    private class RoundedBorder extends AbstractBorder {
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
    }

    @Override
    public void dispose() {
        if (cooldownTimer != null) {
            cooldownTimer.stop();
        }
        super.dispose();
    }
} 