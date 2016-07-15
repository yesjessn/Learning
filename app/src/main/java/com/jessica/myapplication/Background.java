package com.jessica.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by jessica on 7/14/2016.
 */
public class Background {

    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap res)
    {
        image = res;
        dx = GamePanel.MOVESPEED;
    }
    public void update()
    {
        //background moves across screen
        x += dx;
        //resets background to original spot
        if(x<-GamePanel.WIDTH){
            x = 0;
        }
    }
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
        //when x is not in starting position...
        if(x<0)
        {
            //...puts new background right after the old one
            canvas.drawBitmap(image, x + GamePanel.WIDTH, y, null);
        }
    }

}
