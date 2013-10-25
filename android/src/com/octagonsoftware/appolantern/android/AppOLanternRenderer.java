package com.octagonsoftware.appolantern.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

/**
 * OpenGL renderer for AppOLanternView.
 * 
 * @author markroth8
 * @see AppOLanternView
 */
public class AppOLanternRenderer
    implements GLSurfaceView.Renderer
{
    /** Total amount of time it takes to complete the glow animation */
    private static final int GLOW_CYCLE_MS = 4000;
    
    /** Total amount of time it takes to fade to one color */
    private static final int GLOW_CYCLE_PHASE_MS = GLOW_CYCLE_MS / 2;
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        long now = System.currentTimeMillis();
        int frame = (int) (now % GLOW_CYCLE_MS);
        int a = (frame < GLOW_CYCLE_PHASE_MS) ? frame : (GLOW_CYCLE_MS - 1) - frame;
        // Fade from red to yellow
        GLES20.glClearColor(1.0f, (float)a / GLOW_CYCLE_PHASE_MS, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }
}
