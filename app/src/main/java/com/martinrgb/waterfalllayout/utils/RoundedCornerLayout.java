package com.martinrgb.waterfalllayout.utils;

/**
 * Created by MartinRGB on 2017/9/14.
 */

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

public class RoundedCornerLayout extends FrameLayout {
    //Init Radius in DP
    public float CORNER_RADIUS = 18f;
    //Draw Round Radius Boolean
    public boolean shouldRedraw = true;
    //Now Value for torlerance
    public float nowValue = 0;
    //Animation Time
    public float animTime = 400;
    private float cornerRadius;
    public long startTime;
    int framesPerSecond = 60;
    Paint paint;



    public RoundedCornerLayout(Context context) {
        super(context);
        init(context, null, 0);

    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private DisplayMetrics mMetrics;
    private void init(Context context, AttributeSet attrs, int defStyle) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        mMetrics = metrics;


        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, metrics);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas){

    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = canvas.save();


        if(shouldRedraw){


            final Path reDrawPath = new Path();
            cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, mMetrics);
////            Log.e("TAG",Float.toString(cornerRadius));
//            nowValue += cornerRadius /30;
            //18 200
            reDrawPath.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), cornerRadius, cornerRadius, Path.Direction.CW);
            canvas.clipPath(reDrawPath, Region.Op.REPLACE);

            canvas.clipPath(reDrawPath);
            super.dispatchDraw(canvas);
            canvas.restoreToCount(count);

//            if(nowValue < cornerRadius)
//                this.postInvalidateDelayed( 1000 / framesPerSecond);

//            Log.e("nowValue",Float.toString(nowValue));
        }
        else{


            final Path path = new Path();

            if(nowValue >0){
                cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, mMetrics);
                nowValue -= cornerRadius / 20;
            }
            else{
                nowValue = 0;
            }
            path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), Math.max(0,nowValue), Math.max(0,nowValue), Path.Direction.CW);
            canvas.clipPath(path, Region.Op.REPLACE);

            canvas.clipPath(path);
            super.dispatchDraw(canvas);
            canvas.restoreToCount(count);

            if(nowValue > 0)
                this.postInvalidateDelayed( 1000 / framesPerSecond);

        }


    }


}