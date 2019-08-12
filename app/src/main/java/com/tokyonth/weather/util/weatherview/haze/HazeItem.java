package com.tokyonth.weather.util.weatherview.haze;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.tokyonth.weather.R;
import com.tokyonth.weather.util.SvgResources;
import com.tokyonth.weather.util.weatherview.basic.BaseItem;

import java.util.Random;

/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class HazeItem extends BaseItem {

    private Bitmap hazeBitmap;
    private Paint paint;
    private Random random;
    private int posX , posY;
    private float opt;
    private int dx;
    private int distance = 0;
    private int finalDx = 0;

    public HazeItem(int width, int height) {
        super(width, height);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        random = new Random();
        hazeBitmap = SvgResources.getBitmapFromDrawable(R.drawable.weatherview_haze);
        loopInit();
    }

    private void loopInit() {
        posX = random.nextInt(width);
        posY = random.nextInt(height/2);
        opt = 0.5f + random.nextFloat();
        dx = 5 + random.nextInt(10);
        distance = random.nextInt(10);
        int dxRand = 4;
        switch (distance){
            case 1:
            case 2:
            case 3:
                finalDx += -random.nextInt(dxRand);
                break;
            case 4:
            case 5:
                /** no change */
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                finalDx += random.nextInt(dxRand);
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(hazeBitmap,posX,posY,paint);
    }

    @Override
    public void move() {
        posX += finalDx;
        posY += dx * opt;
        if(posY > height){
            loopInit();
        }
    }
}
