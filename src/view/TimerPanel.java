package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.TimerPanelController;
import model.TimerPanelModel;

public class TimerPanel extends JPanel {
    private JLabel timerLabel;
    private JLabel remainLabel;
    private TimerPanelController controller;
    private boolean isRunning = false;

    public TimerPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        setBackground(new Color(30, 30, 30));
        setPreferredSize(new Dimension(200, 60));
        setMaximumSize(new Dimension(200, 60));

        // Initialize MVC components
        this.controller = new TimerPanelController(this);

        // Create panel for labels
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setOpaque(false);

        // Create "Seconds" label
        remainLabel = new JLabel("Timer:");
        remainLabel.setFont(new Font("Arial", Font.BOLD, 16));
        remainLabel.setForeground(Color.WHITE);
        labelPanel.add(remainLabel);

        // Create timer label with custom styling
        timerLabel = new JLabel("20");  
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.WHITE);
        labelPanel.add(timerLabel);

        add(labelPanel);
    }

    public void updateTimeDisplay(String timeString) {
        timerLabel.setText(timeString);
        // Change color to red when 3 or fewer seconds remain
        if (controller.getTotalSeconds() <= 3) {
            timerLabel.setForeground(Color.RED);
        } else {
            timerLabel.setForeground(Color.WHITE);
        }
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            controller.startTimer();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            controller.pauseTimer();
        }
    }

    public void reset() {
        stop();
        controller.resetTimer();
        timerLabel.setForeground(Color.WHITE);
    }

    public int getSeconds() {
        return controller.getTotalSeconds();
    }

    public void dispose() {
        controller.dispose();
    }

    public void notifyTimeUp() {
        // Find the BananaPanel parent
        Component parent = this;
        while (parent != null && !(parent instanceof BananaPanel)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof BananaPanel) {
            BananaPanel bananaPanel = (BananaPanel) parent;
            // Get the snake panel and stop the game
            SnakePanel snakePanel = bananaPanel.getSnakePanel();
            if (snakePanel != null) {
                snakePanel.getGameLogic().handleTimeUp();
            }
        }
    }
} 