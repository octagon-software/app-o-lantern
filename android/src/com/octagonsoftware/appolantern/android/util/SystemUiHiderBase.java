package com.octagonsoftware.appolantern.android.util;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

/**
 * A base implementation of {@link SystemUiHider}. Uses APIs available in all
 * API levels to show and hide the status bar.
 */
public class SystemUiHiderBase extends SystemUiHider
{
    /**
     * Whether or not the system UI is currently visible. This is a cached value
     * from calls to {@link #hide()} and {@link #show()}.
     */
    private boolean _visible = true;

    /**
     * Constructor not intended to be called by clients. Use
     * {@link SystemUiHider#getInstance} to obtain an instance.
     */
    protected SystemUiHiderBase(Activity activity, View anchorView, int flags) {
        super(activity, anchorView, flags);
    }

    @Override
    public void setup()
    {
        if ((_flags & FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES) == 0) {
            _activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public boolean isVisible()
    {
        return _visible;
    }

    @Override
    public void hide()
    {
        if ((_flags & FLAG_FULLSCREEN) != 0) {
            _activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        _onVisibilityChangeListener.onVisibilityChange(false);
        _visible = false;
    }

    @Override
    public void show()
    {
        if ((_flags & FLAG_FULLSCREEN) != 0) {
            _activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        _onVisibilityChangeListener.onVisibilityChange(true);
        _visible = true;
    }
}
