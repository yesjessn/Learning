package com.jessica.myapplication;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jessica on 7/14/2016.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    //background image size
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    //background scroll speed
    public static final int MOVESPEED = -5;
    private long smokeStartTime;
    private long missileStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Smokepuff> smoke;
    private ArrayList<Missile> missiles;
    private Random rand = new Random();

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
        int counter = 0;
        while (retry && counter<1000)
        {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        smoke = new ArrayList<Smokepuff>();
        missiles = new ArrayList<Missile>();
        smokeStartTime = System.nanoTime();
        missileStartTime = System.nanoTime();



        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //if press down...
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //and if just starting (not playing yet)
            if(!player.getPlaying())
            {
                //...game starts
                player.setPlaying(true);
            }
            else
            {
                //...moves up
                player.setUp(true);
            }
            return true;
        }
        //if let go...
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            //...moves down (default)
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update()
    {
        if(player.getPlaying()) {
            bg.update();
            player.update();

            //add missiles on timer
            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed>(2000-player.getScore()/4)){

                //first missile always goes down the middle
                if(missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), WIDTH+10, HEIGHT/2, 45, 15, player.getScore(), 13));
                }
                else
                {

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), WIDTH+10, (int)(rand.nextDouble()*(HEIGHT)), 45, 15, player.getScore(), 13));
                }

                //reset timer
                missileStartTime = System.nanoTime();
            }
            //loop through every missile and check collision and remove
            for(int i = 0; i<missiles.size(); i++)
            {
                //update missile
                missiles.get(i).update();

                if(collision(missiles.get(i), player))
                {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                //remove missile if it is way off the screen
                if(missiles.get(i).getX()<-100)
                {
                    missiles.remove(i);
                    break;
                }
            }



            // add smoke puffs on timer
            long elapsed = (System.nanoTime()-smokeStartTime)/1000000;
            if(elapsed>120){
                smoke.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();
            }

            for (int i = 0; i <smoke.size(); i++)
            {
                smoke.get(i).update();
                //if off the screen...
                if(smoke.get(i).getX()<-10)
                {
                    //...removes it
                    smoke.remove(i);
                }
            }
        }
    }
    public boolean collision(GameObject a, GameObject b)
    {
        if(Rect.intersects(a.getRectangle(), b.getRectangle()))
        {
            return true;
        }
        return false;

    }
    @Override
    public void draw(Canvas canvas)
    {
        //gets width of the entire surface screen
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        //gets height of entire surface screen
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);
        if (canvas != null) {
            final int savedState = canvas.save();




            //scales screen
            canvas.scale(scaleFactorX, scaleFactorY);
            //draws scaled background
            bg.draw(canvas);
            //draws player
            player.draw(canvas);
            //draw smokepuff
            for(Smokepuff sp: smoke)
            {
                sp.draw(canvas);
            }
            //draw missile
            for(Missile m: missiles)
            {
                m.draw(canvas);
            }





            //stops infinite loop of scaling
            canvas.restoreToCount(savedState);
        }
    }
}
