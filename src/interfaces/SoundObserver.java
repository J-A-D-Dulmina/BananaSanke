package interfaces;

/**
 * Interface for observers of the sound manager.
 * This replaces the deprecated java.util.Observer interface.
 */
public interface SoundObserver {
    /**
     * Called when a sound-related update occurs.
     */
    void onSoundUpdate();
} 