package com.jessica.myapplication;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by jessica on 7/14/2016.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    //background image size
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    private MainThread thread;
    private Background bg;

    public GamePanel(Context context) {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        bg.setVector(-5);
        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void update() {
        bg.update();
    }

    @Override
    public void draw(Canvas canvas)
    {
        //gets width of the entire surface screen
        final float scaleFactorX = getWidth()/WIDTH;
        //gets height of entire surface screen
        final float scaleFactorY = getHeight()/HEIGHT;
        if (canvas != null) {
            final int savedState = canvas.save();
            //scales screen
            canvas.scale(scaleFactorX, scaleFactorY);
            //draws screen
            bg.draw(canvas);
            //stops infinite loop of scaling
            canvas.restoreToCount(savedState);
        }
    }
}
