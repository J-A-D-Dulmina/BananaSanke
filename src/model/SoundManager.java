package model;

import interfaces.ISoundManager;
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

public class SoundManager extends Observable implements ISoundManager {
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
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioStream.getFormat();
            
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                return;
            }
            
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            soundEffects.put(name, clip);
            
        } catch (Exception e) {
            // Silently fail for sound loading errors
        }
    }

    private void loadBackgroundMusic() {
        try {
            File musicFile = new File("resources/Sound Effect/background_music.wav");
            if (!musicFile.exists()) {
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            AudioFormat format = audioStream.getFormat();
            
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
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
            // Silently fail for background music loading errors
        }
    }

    @Override
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

    @Override
    public void playWrongFoodSound() {
        synchronized (runningSoundLock) {
            stopRunningSound();
            playSound("wrong_food");
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

    @Override
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
                // Silently fail for sound playing errors
            }
        }
    }

    @Override
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
                // Silently fail for background music playing errors
            }
        }
    }

    @Override
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && isPlaying) {
            try {
                backgroundMusic.stop();
                isPlaying = false;
                setChanged();
                notifyObservers();
            } catch (Exception e) {
                // Silently fail for background music pausing errors
            }
        }
    }

    @Override
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            try {
                backgroundMusic.stop();
                backgroundMusic.setFramePosition(0);
                isPlaying = false;
                setChanged();
                notifyObservers();
            } catch (Exception e) {
                // Silently fail for background music stopping errors
            }
        }
    }

    @Override
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
                    // Silently fail for running sound errors
                }
            }
        }
    }

    @Override
    public void stopRunningSound() {
        synchronized (runningSoundLock) {
            if (runningSound != null && runningSound.isRunning()) {
                try {
                    runningSound.stop();
                } catch (Exception e) {
                    // Silently fail for running sound stopping errors
                }
            }
        }
    }

    @Override
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        updateVolume();
        setChanged();
        notifyObservers();
    }

    @Override
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
            // Silently fail for game state check errors
        }
        return false;
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
            // Silently fail for volume update errors
        }
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public boolean isMuted() {
        return isMuted;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void playButtonClickSound() {
        if (!isMuted) {
            playSound("button_click");
        }
    }

    /**
     * Stops all sounds including background music and sound effects.
     * This is useful for cleanup operations like logout.
     */
    @Override
    public void stopAll() {
        try {
            // Stop background music
            stopBackgroundMusic();
            
            // Stop running sound
            stopRunningSound();
            
            // Stop all sound effects
            for (Clip clip : soundEffects.values()) {
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                    clip.setFramePosition(0);
                }
            }
        } catch (Exception e) {
            // Silently fail for sound stopping errors
        }
    }
} 