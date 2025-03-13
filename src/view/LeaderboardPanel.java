package view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import controller.LeaderboardController;
import controller.LeaderboardController.LeaderboardEntry;
import model.LeaderboardModel;
import model.SoundManager;
import utils.CustomDialogUtils;
import java.awt.image.BufferedImage;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.File;

public class LeaderboardPanel extends JDialog {
    private static final long serialVersionUID = 1L;
    private final GameMainInterface mainFrame;
    private final LeaderboardController controller;
    private BufferedImage backgroundImage;
    
    // UI Components
    private final JPanel mainPanel;
    private final JTable leaderboardTable;
    private final DefaultTableModel tableModel;
    private final JLabel rankLabel;
    private final JLabel bestScoreLabel;
    private final JLabel errorLabel;
    private final JPanel loadingPanel;
    private final JLabel loadingLabel;

    public LeaderboardPanel(GameMainInterface mainFrame) {
        super(mainFrame, "Leaderboard", true);
        this.mainFrame = mainFrame;
        loadBackgroundImage();
        
        // Initialize UI components first
        String[] columnNames = {"Rank", "Player", "Score"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        this.mainPanel = new JPanel(new BorderLayout());
        this.loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        this.rankLabel = new JLabel("Rank: -", SwingConstants.CENTER);
        this.bestScoreLabel = new JLabel("Best Score: 0", SwingConstants.CENTER);
        this.errorLabel = new JLabel("", SwingConstants.CENTER);
        this.loadingPanel = new JPanel(new BorderLayout());
        this.leaderboardTable = new JTable(tableModel);
        
        // Create model and controller after UI components
        LeaderboardModel model = new LeaderboardModel();
        this.controller = new LeaderboardController(model, this);
        
        // Initialize the UI last
        initializeUI();
    }

    private void initializeUI() {
        // Set window properties
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 400, 580, 20, 20));

        // Create background panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background image if available
                if (backgroundImage != null) {
                    g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback to gradient if image is not available
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(20, 20, 20),
                        0, getHeight(), new Color(40, 40, 40)
                    );
                    g2.setPaint(gradient);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                
                // Add semi-transparent overlay
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        backgroundPanel.setOpaque(false);

        // Configure main panel
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a top panel for header and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        // Add header (close button) to top panel
        setupHeaderPanel(topPanel);
        
        // Add title to top panel
        setupTitleLabel(topPanel);
        
        // Create content panel for status and table
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);
        
        // Add status panel
        setupStatusPanel(contentPanel);
        
        // Add table and refresh button
        setupTable(contentPanel);
        
        // Add loading panel as glass pane
        setupLoadingPanel();
        
        // Add panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add mainPanel to backgroundPanel
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        // Add backgroundPanel to dialog
        setContentPane(backgroundPanel);

        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.onClose();
            }
        });
    }

    private void setupHeaderPanel(JPanel parent) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        headerPanel.setOpaque(false);
        
        JLabel closeButton = new JLabel("×");
        closeButton.setForeground(new Color(220, 0, 0));
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SoundManager.getInstance().playButtonClickSound();
                dispose();
                controller.onClose();
            }
        });
        headerPanel.add(closeButton);
        parent.add(headerPanel, BorderLayout.NORTH);
    }

    private void setupTitleLabel(JPanel parent) {
        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        parent.add(titleLabel, BorderLayout.CENTER);
    }

    private void setupStatusPanel(JPanel parent) {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        
        // Configure labels with consistent styling
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Color labelColor = new Color(255, 255, 255);
        
        rankLabel.setFont(labelFont);
        bestScoreLabel.setFont(labelFont);
        errorLabel.setFont(labelFont);
        
        rankLabel.setForeground(labelColor);
        bestScoreLabel.setForeground(labelColor);
        errorLabel.setForeground(new Color(255, 100, 100));
        
        // Center align labels
        rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bestScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components to status panel with proper spacing
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(rankLabel);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(bestScoreLabel);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(errorLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        
        parent.add(statusPanel, BorderLayout.NORTH);
    }

    private void setupLoadingPanel() {
        loadingPanel.setOpaque(false);
        loadingPanel.setLayout(new BorderLayout());
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Create semi-transparent overlay
        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(new BorderLayout());
        overlay.add(loadingLabel, BorderLayout.CENTER);
        
        loadingPanel.add(overlay, BorderLayout.CENTER);
        loadingPanel.setVisible(false);
        
        // Add loading panel as glass pane
        setGlassPane(loadingPanel);
    }

    private void setupTable(JPanel parent) {
        // Configure table appearance
        leaderboardTable.setOpaque(false);
        leaderboardTable.setForeground(Color.WHITE);
        leaderboardTable.setBackground(new Color(0, 0, 0, 0));
        leaderboardTable.setFont(new Font("Arial", Font.PLAIN, 14));
        leaderboardTable.setRowHeight(30);
        leaderboardTable.setShowGrid(true);
        leaderboardTable.setGridColor(new Color(255, 255, 255, 100)); // Semi-transparent white grid
        leaderboardTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Center align all columns with custom renderer that includes borders
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(new Color(0, 0, 0, 0));
                setForeground(Color.WHITE);
                
                // Add bottom border to each cell
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(255, 255, 255, 100)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                
                return c;
            }
        };
        
        // Set column widths and renderers
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Set specific column widths
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Rank
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Player
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Score
        
        // Configure table header with custom renderer
        JTableHeader header = leaderboardTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBackground(new Color(0, 102, 51)); // Dark green color
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Arial", Font.BOLD, 14));
                label.setOpaque(true); // Make sure the background color is visible
                
                // Add bottom and right border to header cells
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(255, 255, 255, 150)),
                    BorderFactory.createEmptyBorder(8, 5, 8, 5)
                ));
                
                return label;
            }
        });
        header.setOpaque(true);
        header.setBackground(new Color(0, 102, 51)); // Match the renderer's background color
        header.setBorder(BorderFactory.createEmptyBorder());

        // Create scroll pane with custom styling
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Style the scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            private final Dimension ZERO_DIM = new Dimension(0, 0);
            private final Color THUMB_COLOR = new Color(255, 255, 255, 100);

            @Override
            protected void configureScrollBarColors() {
                thumbColor = THUMB_COLOR;
                trackColor = new Color(0, 0, 0, 0);
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Do nothing to make track transparent
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                
                int arc = Math.min(thumbBounds.width, 8);
                g2.fill(new RoundRectangle2D.Double(
                    thumbBounds.x + 1,
                    thumbBounds.y + 1,
                    thumbBounds.width - 2,
                    thumbBounds.height - 2,
                    arc,
                    arc
                ));
                
                g2.dispose();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return new JButton() {
                    @Override
                    public Dimension getPreferredSize() {
                        return ZERO_DIM;
                    }
                };
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return new JButton() {
                    @Override
                    public Dimension getPreferredSize() {
                        return ZERO_DIM;
                    }
                };
            }
        });

        // Create bottom panel with refresh button
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Add scroll pane to bottom panel
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create refresh button
        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            SoundManager.getInstance().playButtonClickSound();
            controller.updateLeaderboard();
        });
        
        // Add refresh button to bottom panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add bottom panel to parent
        parent.add(bottomPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(204, 163, 0)); // Darker yellow when pressed
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 215, 0)); // Brighter yellow on hover
                } else {
                    g2.setColor(new Color(255, 204, 0)); // Default yellow
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setPreferredSize(new Dimension(120, 30));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void loadBackgroundImage() {
        try {
            File file = new File("resources/leaderboard_background.png");
            
            if (file.exists()) {
                backgroundImage = ImageIO.read(file);
            }
        } catch (Exception e) {
            // Silently handle any errors
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    // Public update methods
    public void updateLeaderboard(List<LeaderboardEntry> entries) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateLeaderboard(entries));
            return;
        }
        
        tableModel.setRowCount(0);
        int rank = 1;
        // Show up to 20 entries
        for (LeaderboardEntry entry : entries.subList(0, Math.min(20, entries.size()))) {
            tableModel.addRow(new Object[]{
                rank++,
                entry.getUsername(),
                entry.getScore()
            });
        }
    }

    public void updateUserRank(int rank, int totalPlayers) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateUserRank(rank, totalPlayers));
            return;
        }
        rankLabel.setText(String.format("Rank: %d/%d", rank, totalPlayers));
    }

    public void updateBestScore(int score) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateBestScore(score));
            return;
        }
        bestScoreLabel.setText("Your Best Score: " + score);
    }

    public void showError(String error) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> showError(error));
            return;
        }
        
        errorLabel.setText(error);
        startErrorTimer();
    }

    public void showLoading(boolean show) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> showLoading(show));
            return;
        }
        loadingPanel.setVisible(show);
    }

    private void startErrorTimer() {
        Timer timer = new Timer(3000, e -> errorLabel.setText(""));
        timer.setRepeats(false);
        timer.start();
    }
}
