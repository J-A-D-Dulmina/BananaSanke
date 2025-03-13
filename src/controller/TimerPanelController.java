package controller;

import view.TimerPanel;
import model.TimerPanelModel;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TimerPanelController {
    private TimerPanel view;
    private TimerPanelModel model;
    private Timer timer;
    private static final int TIMER_DELAY = 1000; // 1 second

    public TimerPanelController(TimerPanel view) {
        this.view = view;
        this.model = new TimerPanelModel();
        setupTimer();
    }

    private void setupTimer() {
        timer = new Timer(TIMER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.increment();
                view.updateTimeDisplay(model.getTimeString());
                
                // Check if time is up
                if (model.isTimeUp()) {
                    timer.stop();
                    // Notify the game that time is up
                    view.notifyTimeUp();
                }
            }
        });
    }

    public void startTimer() {
        if (!model.isRunning() && !model.isPaused()) {
            model.start();
            timer.start();
        } else if (model.isPaused()) {
            resumeTimer();
        }
    }

    public void pauseTimer() {
        if (model.isRunning()) {
            model.pause();
            timer.stop();
        }
    }

    public void resumeTimer() {
        if (model.isPaused()) {
            model.resume();
            timer.start();
        }
    }

    public void resetTimer() {
        model.reset();
        timer.stop();
        view.updateTimeDisplay(model.getTimeString());
    }

    public int getTotalSeconds() {
        return model.getTotalSeconds();
    }

    public boolean isRunning() {
        return model.isRunning();
    }

    public boolean isPaused() {
        return model.isPaused();
    }

    public void dispose() {
        if (timer != null) {
            timer.stop();
        }
    }
} 