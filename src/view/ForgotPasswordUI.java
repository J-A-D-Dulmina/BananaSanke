package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        addComponent(panel, createLabel("Forgot Password", Color.WHITE, new Font("SansSerif", Font.BOLD, 28)), gbc, 0, 0, 2);

        usernameField = createPlaceholderField("Enter your username");
        addComponent(panel, usernameField, gbc, 0, 1, 2);

        emailField = createPlaceholderField("Enter your email");
        addComponent(panel, emailField, gbc, 0, 2, 2);

        sendEmailButton = createButton("Send Email", new Color(0, 200, 255), Color.WHITE, e -> sendResetEmail());
        addComponent(panel, sendEmailButton, gbc, 0, 3, 2);

        // Back to Login label with clickable functionality
        backToLoginLabel = createLabel("Back to Login", Color.YELLOW, new Font("Arial", Font.BOLD, 14));
        backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            }
        });
        addComponent(panel, backToLoginLabel, gbc, 0, 4, 2);

        messageLabel = createLabel("", Color.RED, null);
        addComponent(panel, messageLabel, gbc, 0, 5, 2);

        backgroundLabel.add(panel);
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
        JLabel label = new JLabel(text, SwingConstants.CENTER);
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
        JTextField field = new JTextField(20);
        configurePlaceholderField(field, placeholder);
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
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setPreferredSize(new Dimension(220, 45));

        // Focus listeners to handle placeholder logic
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
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setPreferredSize(new Dimension(140, 40));
        button.addActionListener(action);
        return button;
    }

    /**
     * Adds a component to the given panel with specified layout constraints.
     * @param panel The panel to add the component to.
     * @param component The component to be added.
     * @param gbc GridBagConstraints for layout positioning.
     * @param x Grid X position.
     * @param y Grid Y position.
     * @param gridWidth Width of the grid cell.
     */
    private void addComponent(JPanel panel, JComponent component, GridBagConstraints gbc, int x, int y, int gridWidth) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridWidth;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(component, gbc);
    }

    /**
     * Handles the reset email sending logic.
     */
    private void sendResetEmail() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        
        if (username.isEmpty() || username.equals("Enter your username")) {
            showMessage("Enter a valid username!", false);
            return;
        }
        
        if (email.isEmpty() || email.equals("Enter your email") || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showMessage("Enter a valid email!", false);
            return;
        }
        
        showMessage("Password reset email sent!", true);
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
