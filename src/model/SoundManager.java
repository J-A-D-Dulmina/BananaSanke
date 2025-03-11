package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import view.APISection;
import view.BananaPanel;
import view.SnakePanel;
import java.awt.Component;

public class SoundManager extends Observable {
    private static SoundManager instance;
    private Clip backgroundMusic;
    private Map<String, Clip> soundEffects;
    private float volume = 0.5f; // 50% default volume
    private boolean isMuted = false;
    private boolean isPlaying = false;
    private volatile Clip runningSound = null;
    private final Object runningSoundLock = new Object();

    private SoundManager() {
        soundEffects = new HashMap<>();
        loadBackgroundMusic();
        loadSoundEffects();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSoundEffects() {
        loadClip("eat", "resources/Sound Effect/Food_Eat_Sound.wav");
        loadClip("wrong_food", "resources/Sound Effect/Wrong_Food_Sound.wav");
        loadClip("game_over", "resources/Sound Effect/Game_Over_Sound.wav");
        loadClip("snake_running", "resources/Sound Effect/Snake_Running_Sound.wav");
        loadClip("button_click", "resources/Sound Effect/Button_click.wav");
    }

    private void loadClip(String name, String path) {
        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                System.err.println("Error: Sound effect not found at " + soundFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioStream.getFormat();
            
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Error: Audio line not supported for " + name);
                return;
            }
            
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            soundEffects.put(name, clip);
            
        } catch (Exception e) {
            System.err.println("Error loading sound effect " + name + ": " + e.getMessage());
        }
    }

    private void loadBackgroundMusic() {
        try {
            File musicFile = new File("resources/Sound Effect/background_music.wav");
            if (!musicFile.exists()) {
                System.err.println("Error: Audio file not found at " + musicFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            AudioFormat format = audioStream.getFormat();
            
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Error: Audio line not supported");
                return;
            }
            
            backgroundMusic = (Clip) AudioSystem.getLine(info);
            backgroundMusic.open(audioStream);
            backgroundMusic.setLoopPoints(0, -1);
            
            backgroundMusic.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && isPlaying && !isMuted) {
                    SwingUtilities.invokeLater(() -> {
                        backgroundMusic.setFramePosition(0);
                        backgroundMusic.start();
                    });
                }
            });

            updateVolume();
            
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    public void playEatSound() {
        synchronized (runningSoundLock) {
            stopRunningSound();
            playSound("eat");
            if (!isMuted) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(300);
                        startRunningSound();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }

    public void playWrongFoodSound() {
        synchronized (runningSoundLock) {
            stopRunningSound();
            playSound("wrong_food");
            if (!isMuted) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(300);
                        if (isGameRunning()) {
                            startRunningSound();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }

    private boolean isGameRunning() {
        try {
            if (APISection.getInstance() != null) {
                Component parent = SwingUtilities.getAncestorOfClass(BananaPanel.class, APISection.getInstance());
                if (parent instanceof BananaPanel) {
                    BananaPanel bananaPanel = (BananaPanel) parent;
                    SnakePanel snakePanel = bananaPanel.getSnakePanel();
                    if (snakePanel != null) {
                        SnakeGameLogic gameLogic = snakePanel.getGameLogic();
                        return gameLogic != null && gameLogic.isRunning();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking game running state: " + e.getMessage());
        }
        return false;
    }

    public void playGameOverSound() {
        synchronized (runningSoundLock) {
            stopRunningSound();
            playSound("game_over");
        }
    }

    private void playSound(String name) {
        if (!isMuted && soundEffects.containsKey(name)) {
            try {
                Clip clip = soundEffects.get(name);
                if (clip != null) {
                    clip.setFramePosition(0);
                    updateClipVolume(clip);
                    clip.start();
                }
            } catch (Exception e) {
                System.err.println("Error playing sound " + name + ": " + e.getMessage());
            }
        }
    }

    public void playBackgroundMusic() {
        if (backgroundMusic != null && !isPlaying && !isMuted) {
            try {
                backgroundMusic.setFramePosition(0);
                backgroundMusic.start();
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                isPlaying = true;
                setChanged();
                notifyObservers();
            } catch (Exception e) {
                System.err.println("Error playing background music: " + e.getMessage());
            }
        }
    }

    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && isPlaying) {
            try {
                backgroundMusic.stop();
                isPlaying = false;
                setChanged();
                notifyObservers();
            } catch (Exception e) {
                System.err.println("Error pausing background music: " + e.getMessage());
            }
        }
    }

    public void startRunningSound() {
        synchronized (runningSoundLock) {
            if (!isMuted && soundEffects.containsKey("snake_running")) {
                try {
                    if (runningSound == null) {
                        runningSound = soundEffects.get("snake_running");
                    }
                    if (runningSound != null && !runningSound.isRunning()) {
                        runningSound.setFramePosition(0);
                        updateClipVolume(runningSound);
                        runningSound.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                } catch (Exception e) {
                    System.err.println("Error playing running sound: " + e.getMessage());
                }
            }
        }
    }

    public void stopRunningSound() {
        synchronized (runningSoundLock) {
            if (runningSound != null && runningSound.isRunning()) {
                try {
                    runningSound.stop();
                } catch (Exception e) {
                    System.err.println("Error stopping running sound: " + e.getMessage());
                }
            }
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        updateVolume();
        setChanged();
        notifyObservers();
    }

    public void setMuted(boolean muted) {
        this.isMuted = muted;
        updateVolume();
        
        if (muted) {
            pauseBackgroundMusic();
            stopRunningSound();
            for (Clip clip : soundEffects.values()) {
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                }
            }
        } else {
            if (isPlaying) {
                playBackgroundMusic();
            }
            if (isGameRunning()) {
                startRunningSound();
            }
        }
        setChanged();
        notifyObservers();
    }

    private void updateVolume() {
        if (backgroundMusic != null) {
            updateClipVolume(backgroundMusic);
        }
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                updateClipVolume(clip);
            }
        }
    }

    private void updateClipVolume(Clip clip) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = isMuted ? gainControl.getMinimum() : (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
            }
        } catch (Exception e) {
            System.err.println("Error updating volume: " + e.getMessage());
        }
    }

    public float getVolume() {
        return volume;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void playButtonClickSound() {
        if (!isMuted) {
            playSound("button_click");
        }
    }
} 