package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Observable;

public class SoundManager extends Observable {
    private static SoundManager instance;
    private Clip backgroundMusic;
    private float volume = 0.5f; // 50% default volume
    private boolean isMuted = false;
    private boolean isPlaying = false;

    private SoundManager() {
        loadBackgroundMusic();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
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
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Error: Unsupported audio format. Please ensure the file is WAV format.");
            System.err.println("Supported formats: WAV (PCM 16-bit, 44100Hz, Stereo or Mono)");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Could not read audio file.");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Error: Audio line unavailable.");
            e.printStackTrace();
        }
    }

    public void playBackgroundMusic() {
        if (backgroundMusic != null && !isPlaying) {
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

    public void setVolume(float volume) {
        this.volume = volume;
        updateVolume();
        setChanged();
        notifyObservers();
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
        updateVolume();
        setChanged();
        notifyObservers();
    }

    private void updateVolume() {
        if (backgroundMusic != null) {
            try {
                FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
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