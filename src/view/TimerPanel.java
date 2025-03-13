package view;

import javax.swing.*;
import java.awt.*;
import model.TimerPanelModel;
import controller.TimerPanelController;

/**
 * Panel displaying the game timer.
 */
public class TimerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JLabel timerLabel;
    private final TimerPanelController controller;

    /**
     * Constructs the timer panel with initial display.
     */
    public TimerPanel() {
        // Initialize MVC components
        TimerPanelModel model = new TimerPanelModel();
        this.controller = new TimerPanelController(model, this);

        setLayout(new GridLayout(1, 2, 10, 5));
        setBackground(new Color(0, 0, 0, 180));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Create timer label with custom styling
        timerLabel = createStyledLabel("Time: ");
        JLabel timeValue = createStyledLabel("30");

        // Add components to panel
        add(timerLabel);
        add(timeValue);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    /**
     * Updates the timer display.
     * @param timeRemaining The remaining time in seconds.
     */
    public void updateTimerDisplay(int timeRemaining) {
        SwingUtilities.invokeLater(() -> {
            if (timerLabel != null) {
                timerLabel.setText("Time: " + timeRemaining);
            }
        });
    }

    /**
     * Starts the timer.
     */
    public void start() {
        controller.startTimer();
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        controller.stopTimer();
    }

    /**
     * Resets the timer to initial state.
     */
    public void reset() {
        controller.resetTimer();
    }

    /**
     * Handles the time up event by notifying the parent panel.
     */
    public void handleTimeUp() {
        BananaPanel parent = (BananaPanel) SwingUtilities.getAncestorOfClass(BananaPanel.class, this);
        if (parent != null) {
            parent.getSnakePanel().getGameLogic().handleTimeUp();
        }
    }

    /**
     * Gets the current time remaining.
     * @return The remaining time in seconds.
     */
    public int getTimeRemaining() {
        return controller.getTimeRemaining();
    }

    /**
     * Checks if the timer is currently running.
     * @return true if the timer is running, false otherwise.
     */
    public boolean isRunning() {
        return controller.isRunning();
    }
} 