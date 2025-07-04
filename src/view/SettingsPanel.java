package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import model.SoundManager;
import controller.SettingsController;
import interfaces.SoundObserver;

public class SettingsPanel extends JDialog implements SoundObserver {
    private static final long serialVersionUID = 1L;
    private JSlider volumeSlider;
    private JToggleButton muteButton;
    private JLabel volumeNumberLabel;
    private ImageIcon muteIcon;
    private ImageIcon audioIcon;
    private final SoundManager soundManager;
    private final SettingsController controller;

    public SettingsPanel(GameMainInterface mainFrame) {
        super(mainFrame, true);
        this.soundManager = SoundManager.getInstance();
        soundManager.addObserver(this);
        this.controller = new SettingsController(this);
        
        setUndecorated(true);
        setSize(400, 300);
        setResizable(false);
        setLocationRelativeTo(mainFrame);
        setBackground(new Color(0, 0, 0, 0));

        // Load icons
        muteIcon = new ImageIcon("resources/mute_audio.png");
        audioIcon = new ImageIcon("resources/audio.png");
        
        // Resize icons to 24x24
        muteIcon = new ImageIcon(muteIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        audioIcon = new ImageIcon(audioIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        // Create main panel with rounded corners
        JPanel mainPanel = new JPanel() {
            private static final long serialVersionUID = 6593606916065772418L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fill(new RoundRectangle2D.Double(5, 5, getWidth() - 10, getHeight() - 10, 20, 20));
                
                // Draw panel background
                g2.setColor(new Color(40, 40, 40));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 5, getHeight() - 5, 20, 20));
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        
        // Header panel with close button
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        
        JLabel closeButton = new JLabel("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(new Color(220, 0, 0));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.handleClose();
            }
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 0, 0));
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(new Color(220, 0, 0));
            }
        });
        headerPanel.add(closeButton);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Title
        JLabel titleLabel = new JLabel("Sound Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Volume control section
        JPanel volumePanel = new JPanel();
        volumePanel.setLayout(new BoxLayout(volumePanel, BoxLayout.Y_AXIS));
        volumePanel.setOpaque(false);
        volumePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel volumeLabel = new JLabel("Volume");
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumePanel.add(volumeLabel);
        volumePanel.add(Box.createVerticalStrut(10));

        // Volume control section with horizontal layout
        JPanel volumeControlPanel = new JPanel();
        volumeControlPanel.setLayout(new BoxLayout(volumeControlPanel, BoxLayout.X_AXIS));
        volumeControlPanel.setOpaque(false);
        volumeControlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumeControlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Volume slider with custom colors
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50) {
            private static final long serialVersionUID = 8706604664729312745L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint track background
                g2.setColor(new Color(60, 60, 60));
                g2.fillRoundRect(0, getHeight() / 2 - 2, getWidth(), 4, 4, 4);

                // Paint filled portion of track
                int fillWidth = (int) ((getWidth() - 2) * (getValue() / 100.0));
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.YELLOW,
                    fillWidth, 0, Color.GREEN
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, getHeight() / 2 - 2, fillWidth, 4, 4, 4);

                // Paint thumb
                g2.setColor(Color.WHITE);
                int thumbX = (int) ((getWidth() - 12) * (getValue() / 100.0));
                g2.fillOval(thumbX, getHeight() / 2 - 6, 12, 12);
                g2.dispose();
            }
        };
        volumeSlider.setOpaque(false);
        volumeSlider.setPreferredSize(new Dimension(200, 40));
        volumeSlider.setMaximumSize(new Dimension(200, 40));

        // Volume number label
        volumeNumberLabel = new JLabel("50%");
        volumeNumberLabel.setFont(new Font("Arial", Font.BOLD, 16));
        volumeNumberLabel.setForeground(Color.WHITE);
        volumeNumberLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        volumeControlPanel.add(volumeSlider);
        volumeControlPanel.add(volumeNumberLabel);
        volumePanel.add(volumeControlPanel);
        volumePanel.add(Box.createVerticalStrut(20));

        // Mute button with icon
        muteButton = createStyledToggleButton("", new Color(60, 60, 60), Color.WHITE);
        muteButton.setIcon(audioIcon);
        muteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumePanel.add(muteButton);

        contentPanel.add(volumePanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add the main panel to the dialog
        add(mainPanel);

        // Add window drag functionality
        addWindowDragListener();

        // Set up listeners
        setupListeners();

        // Initialize view state
        controller.updateView();
    }

    private void setupListeners() {
        // Volume slider listener
        volumeSlider.addChangeListener(e -> {
            controller.handleVolumeChange(volumeSlider.getValue());
        });

        // Mute button listener
        muteButton.addActionListener(e -> {
            controller.handleMuteToggle();
        });
    }

    @Override
    public void onSoundUpdate() {
        SwingUtilities.invokeLater(() -> {
            controller.updateView();
        });
    }

    // View update methods for controller
    public void updateVolumeSlider(int value) {
        volumeSlider.setValue(value);
    }

    public void updateVolumeLabel(int value) {
        volumeNumberLabel.setText(value + "%");
    }

    public void updateMuteButton(boolean isMuted) {
        muteButton.setSelected(isMuted);
        muteButton.setIcon(isMuted ? muteIcon : audioIcon);
    }

    public void setVolumeSliderEnabled(boolean enabled) {
        volumeSlider.setEnabled(enabled);
    }

    private JToggleButton createStyledToggleButton(String text, Color bgColor, Color textColor) {
        JToggleButton button = new JToggleButton(text) {
            private static final long serialVersionUID = 9183447387730347843L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected()) {
                    g2.setColor(new Color(180, 0, 0));
                } else if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                
                super.paintComponent(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(50, 50);
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
        
        return button;
    }

    private void addWindowDragListener() {
        Point[] dragStart = {null};
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStart[0] = e.getPoint();
            }
            
            public void mouseReleased(MouseEvent e) {
                dragStart[0] = null;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragStart[0] != null) {
                    Point current = e.getLocationOnScreen();
                    setLocation(current.x - dragStart[0].x, current.y - dragStart[0].y);
                }
            }
        });
    }
} 