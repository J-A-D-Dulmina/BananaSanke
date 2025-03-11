package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    private JLabel timerLabel;
    private JLabel remainLabel;
    private Timer timer;
    private int seconds = 30;  // Start from 30 seconds
    private boolean isRunning = false;

    public TimerPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(200, 30));
        setMaximumSize(new Dimension(200, 30));

        // Create "Remain Time:" label
        remainLabel = new JLabel("Remain Time:");
        remainLabel.setFont(new Font("Arial", Font.BOLD, 16));
        remainLabel.setForeground(Color.WHITE);
        add(remainLabel);

        // Create timer label with custom styling
        timerLabel = new JLabel("30");  // Start from 30
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setPreferredSize(new Dimension(50, 30));
        add(timerLabel);

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
        // Change color to red when less than 10 seconds remain
        if (seconds <= 10) {
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
        seconds = 30;  // Reset to 30 seconds
        timerLabel.setForeground(Color.WHITE);  // Reset color
        updateDisplay();
    }

    public int getSeconds() {
        return seconds;
    }
} 