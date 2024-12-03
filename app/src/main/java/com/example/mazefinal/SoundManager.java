package com.example.mazefinal;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

/**
 * Provides sound effects for the game
 */
public class SoundManager {
    private SoundPool soundPool;
    private boolean soundEnabled = true;
    private final int newGameSound;
    private final int gameOverSound;
    private final int errorSound;
    private final int clickSound;
    private final int timerSound;

    /**
     * Inits a enw sound manager for a given context
     */
    public SoundManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder().setMaxStreams(1)
                .setAudioAttributes(audioAttributes).build();

        newGameSound = soundPool.load(context, R.raw.new_game, 1);
        gameOverSound = soundPool.load(context, R.raw.game_over, 1);
        errorSound = soundPool.load(context, R.raw.error, 1);
        clickSound = soundPool.load(context, R.raw.click, 1);
        timerSound = soundPool.load(context, R.raw.timer, 1);
    }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void toggleSoundEnabled() { soundEnabled = !soundEnabled; }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public void playNewGameSound() { play(newGameSound); }
    public void playGameOverSound() { play(gameOverSound); }
    public void playErrorSound() { play(errorSound); }
    public void playClickSound() { play(clickSound); }
    public void playTimerSound() { play(timerSound); }

    private void play(int id) {
        if (soundEnabled && soundPool != null) {
            soundPool.play(id, 1, 1, 0, 0, 1);
        }
    }
}
