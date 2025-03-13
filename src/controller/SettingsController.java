package controller;

import model.SoundManager;
import view.SettingsPanel;

public class SettingsController {
    private final SoundManager soundManager;
    private final SettingsPanel settingsPanel;

    public SettingsController(SettingsPanel settingsPanel) {
        this.settingsPanel = settingsPanel;
        this.soundManager = SoundManager.getInstance();
    }

    public void handleVolumeChange(int volumeValue) {
        float volume = volumeValue / 100f;
        soundManager.setVolume(volume);
    }

    public void handleMuteToggle() {
        soundManager.playButtonClickSound();
        boolean newMuteState = !soundManager.isMuted();
        soundManager.setMuted(newMuteState);
    }

    public void handleClose() {
        soundManager.playButtonClickSound();
        settingsPanel.dispose();
    }

    public void updateView() {
        settingsPanel.updateVolumeSlider((int)(soundManager.getVolume() * 100));
        settingsPanel.updateVolumeLabel((int)(soundManager.getVolume() * 100));
        settingsPanel.updateMuteButton(soundManager.isMuted());
        settingsPanel.setVolumeSliderEnabled(!soundManager.isMuted());
    }
} 