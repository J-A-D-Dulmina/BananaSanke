package model;

public class TimerPanelModel {
    private int seconds;
    private int minutes;
    private boolean isRunning;
    private boolean isPaused;
    private int pauseTime;
    private static final int START_TIME = 20; // 20 seconds countdown

    public TimerPanelModel() {
        reset();
    }

    public void start() {
        if (!isRunning && !isPaused) {
            isRunning = true;
            // Set initial time if not paused
            if (!isPaused) {
                minutes = START_TIME / 60;
                seconds = START_TIME % 60;
            }
        }
    }

    public void pause() {
        if (isRunning) {
            isRunning = false;
            isPaused = true;
            pauseTime = (minutes * 60) + seconds;
        }
    }

    public void resume() {
        if (isPaused) {
            isRunning = true;
            isPaused = false;
            minutes = pauseTime / 60;
            seconds = pauseTime % 60;
        }
    }

    public void reset() {
        minutes = START_TIME / 60;
        seconds = START_TIME % 60;
        isRunning = false;
        isPaused = false;
        pauseTime = 0;
    }

    public void increment() {
        if (isRunning) {
            if (seconds > 0) {
                seconds--;
            } else if (minutes > 0) {
                minutes--;
                seconds = 59;
            } else {
                isRunning = false;
            }
        }
    }

    public String getTimeString() {
        // Convert total time to seconds and display only the seconds count
        int totalSeconds = (minutes * 60) + seconds;
        return String.format("%d", totalSeconds);
    }

    public int getTotalSeconds() {
        return (minutes * 60) + seconds;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public boolean isTimeUp() {
        return minutes == 0 && seconds == 0;
    }
} 