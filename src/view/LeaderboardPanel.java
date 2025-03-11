package view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import controller.LeaderboardController;
import controller.LeaderboardController.LeaderboardEntry;

public class LeaderboardPanel extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8573280961674429920L;
	private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private JButton closeButton;
    private GameMainInterface gameMainInterface;
    private JLabel titleLabel;
    private JPanel mainPanel;
    private JPanel tablePanel;
    private JScrollPane scrollPane;
    private Image backgroundImage;
    private LeaderboardController leaderboardController;

    // Game colors
    private static final Color GAME_YELLOW = new Color(255, 255, 0);
    private static final Color DARK_GREEN = new Color(0, 100, 0);
    private static final Color GAME_WHITE = new Color(255, 255, 255);
    private static final Color BLACK_OVERLAY = new Color(0, 0, 0, 180);

    public LeaderboardPanel(GameMainInterface gameMainInterface) {
        super(gameMainInterface, "Leaderboard", true);
        this.gameMainInterface = gameMainInterface;
        this.leaderboardController = LeaderboardController.getInstance();
        
        // Remove window decoration
        setUndecorated(true);
        
        // Load background image
        try {
            backgroundImage = new ImageIcon("resources/Leader board background.jpg").getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Set dialog properties
        setSize(600, 400);
        setLocationRelativeTo(gameMainInterface);
        setResizable(false);
        
        // Set the window shape to have rounded corners
        setShape(new RoundRectangle2D.Float(0, 0, 600, 400, 30, 30));
        
        // Create main panel with background image and black overlay
        mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background image
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                
                // Draw semi-transparent black overlay
                g2d.setColor(BLACK_OVERLAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        // Set content pane directly to main panel
        setContentPane(mainPanel);

        // Create top panel for close button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        topPanel.setOpaque(false);

        // Create close button with custom styling
        closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setForeground(Color.RED);
        closeButton.setBackground(new Color(0, 0, 0, 0));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(30, 25));

        // Add hover effect to close button
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.RED);
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.RED);
            }
        });

        // Add close button to top panel
        topPanel.add(closeButton);

        // Create and style title
        titleLabel = new JLabel("Global Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(GAME_WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(topPanel, BorderLayout.NORTH);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create table panel with transparent background
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        // Create table model with custom renderer for center alignment
        String[] columnNames = {"Rank", "Username", "High Score", "Date Achieved"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create and style table
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setBackground(new Color(0, 0, 0, 150));
        leaderboardTable.setForeground(GAME_WHITE);
        leaderboardTable.setSelectionBackground(new Color(255, 255, 255, 50));
        leaderboardTable.setSelectionForeground(GAME_WHITE);
        leaderboardTable.setFont(new Font("Arial", Font.BOLD, 14));
        leaderboardTable.setRowHeight(30);
        leaderboardTable.setShowGrid(true);
        leaderboardTable.setGridColor(new Color(255, 255, 255, 50));
        leaderboardTable.setIntercellSpacing(new Dimension(0, 0));
        leaderboardTable.setOpaque(false);

        // Custom grid line renderer
        leaderboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(false);
                setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 2));
                return this;
            }
        });

        // Style table header
        JTableHeader header = leaderboardTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35)); // Reduced header height
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(DARK_GREEN);
                setForeground(Color.WHITE);
                setFont(new Font("Arial", Font.BOLD, 15)); // Slightly smaller font
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                return this;
            }
        });
        header.setReorderingAllowed(false);

        // Create scroll pane with transparent background
        scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        scrollPane.setBackground(new Color(0, 0, 0, 0));

        // Add components to panels
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Add close button action
        closeButton.addActionListener(e -> dispose());

        // Load leaderboard data
        loadLeaderboardData();

        // Make the dialog background transparent
        setBackground(new Color(0, 0, 0, 0));
    }

    private void loadLeaderboardData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Fetch leaderboard data using the controller
        leaderboardController.fetchTopScores(new LeaderboardController.LeaderboardCallback() {
            @Override
            public void onSuccess(List<LeaderboardEntry> entries) {
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < entries.size(); i++) {
                        LeaderboardEntry entry = entries.get(i);
                        tableModel.addRow(new Object[]{
                            String.valueOf(i + 1),
                            entry.getUsername(),
                            String.valueOf(entry.getScore()),
                            entry.getDateAchieved()
                        });
                    }

                    // Get current user's rank
                    leaderboardController.getCurrentUserRank(new LeaderboardController.RankCallback() {
                        @Override
                        public void onSuccess(int rank, int totalPlayers) {
                            SwingUtilities.invokeLater(() -> {
                                titleLabel.setText(String.format("Global Leaderboard (Your Rank: %d/%d)", 
                                    rank, totalPlayers));
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            System.err.println("Failed to get user rank: " + error);
                        }
                    });

                    setCursor(Cursor.getDefaultCursor());
                });
            }

            @Override
            public void onFailure(String error) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(LeaderboardPanel.this,
                        error,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    setCursor(Cursor.getDefaultCursor());
                });
            }
        });
    }
}
