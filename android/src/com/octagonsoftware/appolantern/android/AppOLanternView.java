package com.octagonsoftware.appolantern.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * View that renders full-screen glow, fading between yellow and red.
 *
 * @author markroth8
 */
public class AppOLanternView
    extends GLSurfaceView
{
    public AppOLanternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppOLanternView(Context context) {
        super(context);
        init();
    }
    
    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(new AppOLanternRenderer());
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }
}
