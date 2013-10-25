package com.octagonsoftware.appolantern.android.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * An API 11+ implementation of {@link SystemUiHider}. Uses APIs available in
 * Honeycomb and later (specifically {@link View#setSystemUiVisibility(int)}) to
 * show and hide the system UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SystemUiHiderHoneycomb extends SystemUiHiderBase
{
    /**
     * Flags for {@link View#setSystemUiVisibility(int)} to use when showing the
     * system UI.
     */
    private int _showFlags;

    /**
     * Flags for {@link View#setSystemUiVisibility(int)} to use when hiding the
     * system UI.
     */
    private int _hideFlags;

    /**
     * Flags to test against the first parameter in
     * {@link android.view.View.OnSystemUiVisibilityChangeListener#onSystemUiVisibilityChange(int)}
     * to determine the system UI visibility state.
     */
    private int _testFlags;

    /**
     * Whether or not the system UI is currently visible. This is cached from
     * {@link android.view.View.OnSystemUiVisibilityChangeListener}.
     */
    private boolean _visible = true;

    /**
     * Constructor not intended to be called by clients. Use
     * {@link SystemUiHider#getInstance} to obtain an instance.
     */
    protected SystemUiHiderHoneycomb(Activity activity, View anchorView, int flags) {
        super(activity, anchorView, flags);

        _showFlags = View.SYSTEM_UI_FLAG_VISIBLE;
        _hideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        _testFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE;

        if ((_flags & FLAG_FULLSCREEN) != 0) {
            // If the client requested fullscreen, add flags relevant to hiding
            // the status bar. Note that some of these constants are new as of
            // API 16 (Jelly Bean). It is safe to use them, as they are inlined
            // at compile-time and do nothing on pre-Jelly Bean devices.
            _showFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            _hideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if ((_flags & FLAG_HIDE_NAVIGATION) != 0) {
            // If the client requested hiding navigation, add relevant flags.
            _showFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            _hideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            _testFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setup()
    {
        _anchorView.setOnSystemUiVisibilityChangeListener(_systemUiVisibilityChangeListener);
    }

    /** {@inheritDoc} */
    @Override
    public void hide()
    {
        _anchorView.setSystemUiVisibility(_hideFlags);
    }

    /** {@inheritDoc} */
    @Override
    public void show()
    {
        _anchorView.setSystemUiVisibility(_showFlags);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVisible()
    {
        return _visible;
    }

    private View.OnSystemUiVisibilityChangeListener _systemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int vis)
        {
            // Test against _testFlags to see if the system UI is visible.
            if ((vis & _testFlags) != 0) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    // Pre-Jelly Bean, we must manually hide the action bar
                    // and use the old window flags API.
                    _activity.getActionBar().hide();
                    _activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                // Trigger the registered listener and cache the visibility
                // state.
                _onVisibilityChangeListener.onVisibilityChange(false);
                _visible = false;

            } else {
                _anchorView.setSystemUiVisibility(_showFlags);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    // Pre-Jelly Bean, we must manually show the action bar
                    // and use the old window flags API.
                    _activity.getActionBar().show();
                    _activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                // Trigger the registered listener and cache the visibility
                // state.
                _onVisibilityChangeListener.onVisibilityChange(true);
                _visible = true;
            }
        }
    };
}
