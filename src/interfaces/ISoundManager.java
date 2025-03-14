package interfaces;

/**
 * Interface defining the operations for the sound manager.
 */
public interface ISoundManager {
    /**
     * Plays the eat sound effect.
     */
    void playEatSound();
    
    /**
     * Plays the wrong food sound effect.
     */
    void playWrongFoodSound();
    
    /**
     * Plays the game over sound effect.
     */
    void playGameOverSound();
    
    /**
     * Plays the button click sound effect.
     */
    void playButtonClickSound();
    
    /**
     * Plays the background music.
     */
    void playBackgroundMusic();
    
    /**
     * Pauses the background music.
     */
    void pauseBackgroundMusic();
    
    /**
     * Stops the background music.
     */
    void stopBackgroundMusic();
    
    /**
     * Starts the running sound effect.
     */
    void startRunningSound();
    
    /**
     * Stops the running sound effect.
     */
    void stopRunningSound();
    
    /**
     * Sets the volume level.
     * @param volume The volume level (0.0 to 1.0).
     */
    void setVolume(float volume);
    
    /**
     * Sets the muted state.
     * @param muted True to mute sounds, false to unmute.
     */
    void setMuted(boolean muted);
    
    /**
     * Gets the current volume level.
     * @return The current volume level (0.0 to 1.0).
     */
    float getVolume();
    
    /**
     * Checks if sound is muted.
     * @return True if sound is muted, false otherwise.
     */
    boolean isMuted();
    
    /**
     * Checks if background music is playing.
     * @return True if background music is playing, false otherwise.
     */
    boolean isPlaying();
    
    /**
     * Stops all sounds including background music and sound effects.
     * This is useful for cleanup operations like logout.
     */
    void stopAll();
} 