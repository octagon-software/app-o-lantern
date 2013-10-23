package com.octagonsoftware.pumpkinglow.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * View that renders full-screen glow, fading between yellow and red.
 *
 * @author markroth8
 */
public class PumpkinGlowView
    extends GLSurfaceView
{
    public PumpkinGlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PumpkinGlowView(Context context) {
        super(context);
        init();
    }
    
    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(new PumpkinGlowRenderer());
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }
}
