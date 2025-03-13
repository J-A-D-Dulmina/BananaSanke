package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    private JLabel timerLabel;
    private JLabel remainLabel;
    private Timer timer;
    private int seconds = 10;  // Start from 10 seconds
    private boolean isRunning = false;

    public TimerPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        setBackground(new Color(30, 30, 30)); 
//        setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));  
        setPreferredSize(new Dimension(200, 60));
        setMaximumSize(new Dimension(200, 60));

        // Create panel for labels
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setOpaque(false);

        // Create "Time" label
        remainLabel = new JLabel("Time:");
        remainLabel.setFont(new Font("Arial", Font.BOLD, 16));
        remainLabel.setForeground(Color.WHITE);
        labelPanel.add(remainLabel);

        // Create timer label with custom styling
        timerLabel = new JLabel("10");  // Start from 10
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.WHITE);
        labelPanel.add(timerLabel);

        add(labelPanel);

        // Initialize timer to count down
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds--;
                if (seconds <= 0) {
                    stop();
                    // Notify game logic that time is up
                    BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, TimerPanel.this);
                    if (parent != null) {
                        parent.getSnakePanel().getGameLogic().handleTimeUp();
                    }
                }
                updateDisplay();
            }
        });
    }

    private void updateDisplay() {
        timerLabel.setText(String.valueOf(seconds));
        // Change color to red when 3 or fewer seconds remain
        if (seconds <= 3) {
            timerLabel.setForeground(Color.RED);
        } else {
            timerLabel.setForeground(Color.WHITE);
        }
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            timer.start();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            timer.stop();
        }
    }

    public void reset() {
        stop();
        seconds = 10;  // Reset to 10 seconds
        timerLabel.setForeground(Color.WHITE);  // Reset color
        updateDisplay();
    }

    public int getSeconds() {
        return seconds;
    }
} 