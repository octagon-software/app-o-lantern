package com.octagonsoftware.appolantern.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.octagonsoftware.appolantern.android.util.SystemUiHider;
import com.octagonsoftware.appolantern.android.R;

/**
 * Main activity for the App-O-Lantern Android app.
 * Shows a glowing screen and plays sound effects.
 * 
 * @author markroth8
 * @see SystemUiHider
 */
public class AppOLanternActivity 
    extends Activity 
{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private SystemUiHider _systemUiHider;
    private AppOLanternView _appOLanternView;
    private SoundManager _soundManager;
    private SoundTimer _soundTimer;
    private boolean _paused = true;
    private boolean _eulaAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        Eula eula = new Eula(this);
        if (!eula.hasBeenShown()) {
            eula.show();
        } else {
            onEulaAccepted();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        _paused = true;
        updatePauseStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _paused = false;
        updatePauseStatus();
    }
    
    protected void onEulaAccepted() {
        setContentView(R.layout.activity_app_o_lantern);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        _appOLanternView = (AppOLanternView) contentView;

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        _systemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        _systemUiHider.setup();
        _systemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
            // Cached values.
            int _controlsHeight;
            int _shortAnimTime;

            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
            public void onVisibilityChange(boolean visible)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    // If the ViewPropertyAnimator API is available
                    // (Honeycomb MR2 and later), use it to animate the
                    // in-layout UI controls at the bottom of the
                    // screen.
                    if (_controlsHeight == 0) {
                        _controlsHeight = controlsView.getHeight();
                    }
                    if (_shortAnimTime == 0) {
                        _shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                    }
                    controlsView.animate().translationY(visible ? 0 : _controlsHeight).setDuration(_shortAnimTime);
                } else {
                    // If the ViewPropertyAnimator APIs aren't
                    // available, simply show or hide the in-layout UI
                    // controls.
                    controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                }

                if (visible && AUTO_HIDE) {
                    // Schedule a hide().
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (TOGGLE_ON_CLICK) {
                    _systemUiHider.toggle();
                } else {
                    _systemUiHider.show();
                }
            }
        });

        // Keep screen on while this activity is running
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Set up audio
        _soundManager = new SoundManager(this);
        _soundTimer = new SoundTimer(_soundManager);
        
        // Whether the app is paused or resumed, update the current status (this starts or stops timers, etc.)
        _eulaAccepted = true;
        updatePauseStatus();

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener _delayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler _hideHandler = new Handler();
    Runnable _hideRunnable = new Runnable() {
        @Override
        public void run()
        {
            if (_eulaAccepted) {
                _systemUiHider.hide();
            }
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis)
    {
        _hideHandler.removeCallbacks(_hideRunnable);
        _hideHandler.postDelayed(_hideRunnable, delayMillis);
    }
    
    private void updatePauseStatus() {
        if (_eulaAccepted) {
            if (_paused) {
                _appOLanternView.onPause();
                _soundTimer.stop();
                _soundManager.stop();
            } else {
                _soundTimer.start();
                _appOLanternView.onResume();
            }
        }
    }
}
