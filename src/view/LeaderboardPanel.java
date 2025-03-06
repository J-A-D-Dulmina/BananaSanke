package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class LeaderboardPanel extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8573280961674429920L;
	private final JTable leaderboardTable;
    private final DefaultTableModel tableModel;

    public LeaderboardPanel(JFrame parent) {
        super(parent, "Leaderboard - Top 20 Players", true);
        setSize(400, 450);
        setResizable(false);
       
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Center the LeaderboardPanel in the screen
        setLocationRelativeTo(null); 
        
        // Title panel with custom "Close" button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.DARK_GRAY);
        titlePanel.setPreferredSize(new Dimension(getWidth(), 40));

        JLabel titleLabel = new JLabel("Leaderboard - Top 20 Players", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

//        JButton closeButton = new JButton("Close");
//        closeButton.setPreferredSize(new Dimension(80, 30));
//        closeButton.setFocusPainted(false);
//        closeButton.setBorderPainted(false);
//        closeButton.setBackground(Color.RED);
//        closeButton.setForeground(Color.WHITE);
//        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
//        closeButton.addActionListener(e -> dispose());

        titlePanel.add(titleLabel, BorderLayout.CENTER);
//        titlePanel.add(closeButton, BorderLayout.EAST);

        tableModel = new DefaultTableModel(new String[]{"Rank", "Player", "Score"}, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 5939869733069513951L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setRowHeight(25);
        leaderboardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leaderboardTable.setFocusable(false);

        JTableHeader tableHeader = leaderboardTable.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setPreferredSize(new Dimension(380, 360));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(contentPanel);
    }

    public void updateScores(Object[][] data) {
        tableModel.setRowCount(0);
        int limit = Math.min(data.length, 20);
        for (int i = 0; i < limit; i++) {
            tableModel.addRow(data[i]);
        }
    }
}
