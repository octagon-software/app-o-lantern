package com.octagonsoftware.appolantern.android;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * Encapsulates the details of playing audio sound effects.
 *
 * @author markroth8
 */
public class SoundManager
    implements OnAudioFocusChangeListener
{
    private Context _context;
    private MediaPlayer _mediaPlayer;
    private AudioManager _audioManager;
    private boolean _audioFocus = false;

    public SoundManager(Context context) {
        _context = context;
        _audioManager = (AudioManager) _context.getSystemService(Context.AUDIO_SERVICE);
        int result = _audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            _audioFocus = true;
        }
    }
    
    public synchronized void playSound(int resId) {
        stop();
        if (_audioFocus) {
            _mediaPlayer = MediaPlayer.create(_context, resId);
            _mediaPlayer.start();
            _mediaPlayer.setOnCompletionListener(
                new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stop();
                    }
                }
            );
        }
    }
    
    public synchronized void stop() {
        if (_mediaPlayer != null) {
            if (_mediaPlayer.isPlaying()) {
                _mediaPlayer.stop();
            }
            _mediaPlayer.release();
            _mediaPlayer = null;
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // we're only playing short sounds, so just stop playing sounds if something else is going on. We'll pick up at
                // the next sound.
                stop();
                _audioFocus = false;
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                _audioFocus = true;
                break;
            default:
                // unknown event - just ignore.
                break;
        }
    }
}
