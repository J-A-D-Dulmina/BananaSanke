package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.HashMap;
import java.util.Map;

public class SoundManager extends Observable {
    private static SoundManager instance;
    private Clip backgroundMusic;
    private Map<String, Clip> soundEffects;
    private float volume = 0.5f; // 50% default volume
    private boolean isMuted = false;
    private boolean isPlaying = false;
    private Clip runningSound = null;

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
            
            // Create compatible line info
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            
            // Check if system supports the audio format
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
            
            // Create compatible line info
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            
            // Check if system supports the audio format
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Error: Audio line not supported");
                return;
            }
            
            backgroundMusic = (Clip) AudioSystem.getLine(info);
            backgroundMusic.open(audioStream);
            
            // Set up loop points for continuous playback
            backgroundMusic.setLoopPoints(0, -1);
            
            // Add listener to handle clip completion
            backgroundMusic.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && isPlaying) {
                    backgroundMusic.setFramePosition(0);
                    backgroundMusic.start();
                }
            });

            // Set initial volume
            updateVolume();
            
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    public void playEatSound() {
        if (runningSound != null && runningSound.isRunning()) {
            runningSound.stop();
        }
        playSound("eat");
        // Restart running sound after a short delay
        if (!isMuted) {
            new Thread(() -> {
                try {
                    Thread.sleep(300); // Wait for eat sound to finish
                    startRunningSound();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    public void playWrongFoodSound() {
        if (runningSound != null && runningSound.isRunning()) {
            runningSound.stop();
        }
        playSound("wrong_food");
        // Restart running sound after a short delay
        if (!isMuted) {
            new Thread(() -> {
                try {
                    Thread.sleep(300); // Wait for wrong food sound to finish
                    startRunningSound();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    public void playGameOverSound() {
        if (runningSound != null && runningSound.isRunning()) {
            runningSound.stop();
        }
        playSound("game_over");
        // Don't restart running sound after game over
    }

    private void playSound(String name) {
        if (!isMuted && soundEffects.containsKey(name)) {
            try {
                Clip clip = soundEffects.get(name);
                clip.setFramePosition(0);
                updateClipVolume(clip);
                clip.start();
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
        if (!isMuted && soundEffects.containsKey("snake_running")) {
            try {
                if (runningSound == null) {
                    runningSound = soundEffects.get("snake_running");
                }
                runningSound.setFramePosition(0);
                updateClipVolume(runningSound);
                runningSound.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception e) {
                System.err.println("Error playing running sound: " + e.getMessage());
            }
        }
    }

    public void stopRunningSound() {
        if (runningSound != null) {
            try {
                runningSound.stop();
            } catch (Exception e) {
                System.err.println("Error stopping running sound: " + e.getMessage());
            }
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
        updateVolume();
        setChanged();
        notifyObservers();
    }

    public void setMuted(boolean muted) {
        this.isMuted = muted;
        updateVolume();
        
        if (muted) {
            // Stop all sounds when muted
            pauseBackgroundMusic();
            stopRunningSound();
            // Stop any playing sound effects
            for (Clip clip : soundEffects.values()) {
                if (clip.isRunning()) {
                    clip.stop();
                }
            }
        } else {
            // Only restart background music if it was playing before
            if (isPlaying) {
                playBackgroundMusic();
            }
            // Only restart running sound if the game is running
            if (runningSound != null && runningSound.isRunning()) {
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
            updateClipVolume(clip);
        }
    }

    private void updateClipVolume(Clip clip) {
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain;
            
            if (isMuted) {
                gain = gainControl.getMinimum();
            } else {
                gain = (range * volume) + gainControl.getMinimum();
            }
            
            gainControl.setValue(gain);
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
} 