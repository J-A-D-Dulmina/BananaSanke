package controller;

import model.TimerPanelModel;
import view.TimerPanel;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Controller class for managing timer panel interactions.
 */
public class TimerPanelController {
    private final TimerPanelModel model;
    private final TimerPanel view;
    private Timer timer;

    public TimerPanelController(TimerPanelModel model, TimerPanel view) {
        this.model = model;
        this.view = view;
        initializeTimer();
    }

    private void initializeTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.decrementTime();
                view.updateTimerDisplay(model.getTimeRemaining());
                
                if (model.isTimeUp()) {
                    handleTimeUp();
                }
            }
        });
    }

    public void startTimer() {
        model.startTimer();
        timer.start();
    }

    public void stopTimer() {
        model.stopTimer();
        timer.stop();
    }

    public void resetTimer() {
        model.resetTimer();
        timer.stop();
        view.updateTimerDisplay(model.getTimeRemaining());
    }

    private void handleTimeUp() {
        stopTimer();
        // Notify the parent panel about time up
        view.handleTimeUp();
    }

    public boolean isRunning() {
        return model.isRunning();
    }

    public int getTimeRemaining() {
        return model.getTimeRemaining();
    }
} 