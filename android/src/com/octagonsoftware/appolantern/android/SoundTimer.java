package com.octagonsoftware.appolantern.android;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.octagonsoftware.appolantern.android.R;

/**
 * Plays spooky sound effects periodically at random times.
 * 
 * Can be stopped and started as the app gains or loses focus.
 * 
 * @author markroth8
 */
public class SoundTimer
{
    // Minimum and maximum delay between sound effects
    private static final int MIN_SOUND_DELAY = 10000;
    private static final int MAX_SOUND_DELAY = 20000;

    private static final int[] SOUNDS = {
        R.raw.chainsaw,
        R.raw.creaking_door_spooky,
        R.raw.evil_laugh_6,
        R.raw.evil_laugh_9,
        R.raw.female_scream_horror,
        R.raw.godzilla_roar,
        R.raw.rusty_door
    };
    
    private final SoundManager _soundManager;
    private ScheduledThreadPoolExecutor _executor;
    private final Random _random = new Random();

    public SoundTimer(SoundManager soundManager) {
        _soundManager = soundManager;
    }
    
    public synchronized void start() {
        if (_executor == null) {
            _executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
            scheduleNextSound();
        }
    }
    
    public synchronized void stop() {
        if (_executor != null) {
            _executor.shutdownNow();
            _executor = null;
        }
    }
    
    private void scheduleNextSound() {
        _executor.schedule(new PlaySoundTask(randomSoundId()), randomDelay(), TimeUnit.MILLISECONDS);
    }
    
    private int randomSoundId() {
        return SOUNDS[_random.nextInt(SOUNDS.length)];
    }
    
    private int randomDelay() {
        return _random.nextInt(MAX_SOUND_DELAY - MIN_SOUND_DELAY) + MIN_SOUND_DELAY;
    }
    
    private class PlaySoundTask
        implements Runnable
    {
        /** Id of the sound to play next */
        private int _resId;

        public PlaySoundTask(int resId) {
            _resId = resId;
        }

        public void run() {
            synchronized (SoundTimer.this) {
                _soundManager.playSound(_resId);
                scheduleNextSound();
            }
        }
    }
}
