package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;


public class CustomDialogUtils {
    
    public static JOptionPane showCustomConfirmDialog(Component parentComponent, String message, String title) {
        // Create custom buttons with curved edges
    	
        JButton yesButton = new JButton("YES") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(180, 0, 0));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(220, 0, 0));
                } else {
                    g2.setColor(new Color(200, 0, 0));
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        yesButton.setForeground(Color.WHITE);
        yesButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        yesButton.setFocusPainted(false);
        yesButton.setContentAreaFilled(false);
        yesButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton noButton = new JButton("NO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 140, 0));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0, 180, 0));
                } else {
                    g2.setColor(new Color(0, 160, 0));
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        noButton.setForeground(Color.WHITE);
        noButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        noButton.setFocusPainted(false);
        noButton.setContentAreaFilled(false);
        noButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Create message label with custom font
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create button panel with spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        // Create the JOptionPane with custom styling
        JOptionPane optionPane = new JOptionPane(
            messageLabel,
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{buttonPanel},
            null
        );
        optionPane.setBackground(Color.WHITE);

        // Create and configure the dialog
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setBackground(Color.WHITE);
        
        // Apply white background to all components recursively
        applyWhiteBackground(dialog);

        // Add action listeners
        yesButton.addActionListener(e -> {
            optionPane.setValue(JOptionPane.YES_OPTION);
            dialog.dispose();
        });

        noButton.addActionListener(e -> {
            optionPane.setValue(JOptionPane.NO_OPTION);
            dialog.dispose();
        });
        
        return optionPane;
    }

    private static void applyWhiteBackground(Component component) {
        component.setBackground(Color.WHITE);
        
        if (component instanceof JPanel) {
            component.setBackground(Color.WHITE);
            for (Component child : ((JPanel) component).getComponents()) {
                applyWhiteBackground(child);
            }
        } else if (component instanceof JRootPane) {
            for (Component child : ((JRootPane) component).getComponents()) {
                applyWhiteBackground(child);
            }
        } else if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyWhiteBackground(child);
            }
        }
    }

    public static int showConfirmDialog(Component parentComponent, String message, String title) {
        JOptionPane optionPane = showCustomConfirmDialog(parentComponent, message, title);
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        
        // Center the dialog on the parent component
        if (parentComponent != null) {
            Point parentLocation = parentComponent.getLocationOnScreen();
            Dimension parentSize = parentComponent.getSize();
            Dimension dialogSize = dialog.getSize();
            int x = parentLocation.x + (parentSize.width - dialogSize.width) / 2;
            int y = parentLocation.y + (parentSize.height - dialogSize.height) / 2;
            dialog.setLocation(x, y);
        }
        
        dialog.setVisible(true);
        dialog.dispose();
        
        Object value = optionPane.getValue();
        if (value == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return JOptionPane.CLOSED_OPTION;
    }

    public static void showErrorDialog(Component parentComponent, String message, String title) {
        // Create message label with custom font
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create OK button with custom styling
        JButton okButton = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(180, 0, 0));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(220, 0, 0));
                } else {
                    g2.setColor(new Color(200, 0, 0));
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        okButton.setFocusPainted(false);
        okButton.setContentAreaFilled(false);
        okButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Create button panel with spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        // Create the JOptionPane with custom styling
        JOptionPane optionPane = new JOptionPane(
            messageLabel,
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{buttonPanel},
            null
        );
        optionPane.setBackground(Color.WHITE);

        // Create and configure the dialog
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setBackground(Color.WHITE);
        
        // Apply white background to all components recursively
        applyWhiteBackground(dialog);

        // Add action listener to OK button
        okButton.addActionListener(e -> dialog.dispose());
        
        // Center the dialog on the parent component
        if (parentComponent != null) {
            Point parentLocation = parentComponent.getLocationOnScreen();
            Dimension parentSize = parentComponent.getSize();
            Dimension dialogSize = dialog.getSize();
            int x = parentLocation.x + (parentSize.width - dialogSize.width) / 2;
            int y = parentLocation.y + (parentSize.height - dialogSize.height) / 2;
            dialog.setLocation(x, y);
        }
        
        dialog.setVisible(true);
    }
} 