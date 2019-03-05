package com.martinrgb.waterfalllayout.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Toast;

/**
 * Created by MartinRGB on 2017/9/11.
 */

public class MultiGestureDetector implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener,View.OnTouchListener{

    private VelocityTracker mVelocityTracker;
    private static final int SWIPE_DISTANCE_THRESHOLD = 200;
    private static final int SWIPE_VELOCITY_THRESHOLD = 2000;
    private static final int LOG_TYPE_I = 0;
    private static final int LOG_TYPE_E = 1;
    private static final String TAG = "MultiGestureDetector";
    private GestureDetector mGestureDetector;
    private boolean firstJudge = false;

    //创建监听器借口
    public interface SimpleGestureListener
    {
        void onDown(MotionEvent event);
        void onLongPress(MotionEvent event);
        void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY,float velocityX,float velocityY);
//        void onSwipeLeft(float velocity);
//        void onSwipeRight(float velocity);
//        void onSwipeBottom(float velocity);
        void onSwipeTop(float velocity);
        void onUp(MotionEvent event);
//        void onDoubleTap(MotionEvent event);
        void onSwipeTopFix();
        void onVelocityStop(boolean boo);
        void onTriggerJudge(boolean boo);
    }
    //实例化一个监听器的数值为空
    private SimpleGestureListener mSimpleGestureListener = null;

    public MultiGestureDetector(SimpleGestureListener simpleGestureListener) {
        mGestureDetector = new GestureDetector(this);
        mSimpleGestureListener = simpleGestureListener;
    }

    public boolean onDown(MotionEvent event) {
        return true;
    }

    public void onShowPress(MotionEvent event) {
        printTag(TAG,true,LOG_TYPE_I,event);
        //Reset
        //mSimpleGestureListener.onTriggerJudge(false);
        latestTrackedVelocityX = 0;
        latestTrackedVelocityY = 0;
        scrollInProgress = false;
        firstJudge = false;

    }

    public void  onLongPress(MotionEvent event) {
        //mSimpleGestureListener.onLongPress(event);
    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mSimpleGestureListener.onScroll(e1,e2,e2.getX()-e1.getX(),e2.getY()-e1.getY(),xVelocity,yVelocity);
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        //mSimpleGestureListener.onSwipeRight(velocityX);
                        //Log.e("SFACT","Swipe Right,Velocity is" + velocityX);
                    } else {
                        //mSimpleGestureListener.onSwipeLeft(velocityX);
                        //Log.e("SFACT","Swipe Left,Velocity is" + velocityX);
                    }
                    result = true;
                }

            }

            else if (Math.abs(diffX) < Math.abs(diffY)){

                if (Math.abs(diffY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        //Log.e("SFACT","Swipe Bottom,Velocity is" + velocityY);
                       // mSimpleGestureListener.onSwipeBottom(velocityY);
                    } else {
                        //Log.e("SFACT","Swipe Top,Velocity is" + velocityY);
                        mSimpleGestureListener.onSwipeTop(velocityY);

                    }
                    result = true;
                }
                else{
                    Log.e("SFACT","Should Fix Error");
                    mSimpleGestureListener.onSwipeTopFix();
                }

            }
            else{
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        //printTag(TAG,true,LOG_TYPE_E,null);
        return result;
    }

    private boolean hasRecycled = false;
    private float xVelocity,yVelocity;
    private timeCheckHandler mTimeCheckHandler = new timeCheckHandler();

    public boolean onTouch(View v, MotionEvent event) {

        //Log.v("ADAM", "ontouch: " + event.getAction());

        boolean detectedUp = event.getAction() == MotionEvent.ACTION_UP;

        if(event.getAction() == 0){
            mSimpleGestureListener.onDown(event);
            mSimpleGestureListener.onTriggerJudge(false);

            if (mVelocityTracker == null) {
                // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                mVelocityTracker = VelocityTracker.obtain();

                //// the following instruction resets the Time Check clock // the clock is first started
                mTimeCheckHandler.sleep(timeCheckInterval);
                latestTrackedVelocityX = 0;
                latestTrackedVelocityY = 0;

            } else {
                // Reset the velocity tracker back to its initial state.
                mVelocityTracker.clear();
                hasRecycled = false;

            }

            //Log.v("ADAM", "Down");
        }
        else if (event.getAction() == 1) {
            //Log.v("ADAM", "Up");
            mSimpleGestureListener.onUp(event);
            // Return a VelocityTracker object back to be re-used by others.

            mSimpleGestureListener.onUp(event);
            // Return a VelocityTracker object back to be re-used by others.

            if(!hasRecycled){
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                hasRecycled = true;
                mSimpleGestureListener.onTriggerJudge(false);
                scrollInProgress = false;
            }

            //Add userMovement
            //mVelocityTracker.addMovement(event);
        }
        else if (event.getAction() == 2) {
            //Log.v("ADAM", "Scroll");
            mVelocityTracker.addMovement(event);
            // When you want to determine the velocity, call
            // computeCurrentVelocity(). Then call getXVelocity()
            // and getYVelocity() to retrieve the velocity for each pointer ID.
            mVelocityTracker.computeCurrentVelocity(16);
            // Log velocity of pixels per second
            xVelocity = mVelocityTracker.getXVelocity(0);
            yVelocity = mVelocityTracker.getYVelocity(0);

            if(Math.abs(Math.abs(xVelocity) - latestTrackedVelocityX) +  Math.abs(Math.abs(yVelocity) - latestTrackedVelocityY) > translationThereshold ){

                if(Math.abs(xVelocity) > 0.5 || Math.abs(yVelocity) > 0.5){
                    scrollInProgress = true;
                    mSimpleGestureListener.onVelocityStop(false);
                    mSimpleGestureListener.onTriggerJudge(false);
                }

            }

            if(!scrollInProgress && (latestTrackedVelocityX + latestTrackedVelocityY) > 1.4){

                if(Math.abs(System.currentTimeMillis() - stopTime) > 200){

                    scrollInProgress = true;

                }
            }


            long now = System.currentTimeMillis();
            latestScrollEventTime = now;
        }

        else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            Log.v("ADAM", "Cancel2");
            mSimpleGestureListener.onUp(event);

            // Return a VelocityTracker object back to be re-used by others.
            if(!hasRecycled){
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                hasRecycled = true;
            }

        }
        else{
           // Log.v("ADAM", "Else");
        }

        if (!mGestureDetector.onTouchEvent(event) && detectedUp) {
            Log.v("ADAM", "Cancel");
            mSimpleGestureListener.onUp(event);

            // Return a VelocityTracker object back to be re-used by others.
            if(!hasRecycled){
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                hasRecycled = true;
            }
            return true;
        }

        return true;

//        printTag(TAG,true,LOG_TYPE_I,event);
    }


    public boolean onSingleTapUp(MotionEvent event) {
        //printTag(TAG,true,LOG_TYPE_I,event);

        //mSimpleGestureListener.onUp(event);
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        //printTag(TAG,true,LOG_TYPE_I,event);
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        //printTag(TAG,true,LOG_TYPE_I,event);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        //printTag(TAG,true,LOG_TYPE_I,event);
        //mSimpleGestureListener.onDoubleTap(event);
        return false;
    }


    // ############ Time Check Handler ############
    //Changing 1 - Trending
    //private long timeCheckInterval = 100; // 检测间隔
    //Changing 2 - Trending + StopJudge
    private long timeCheckInterval = 16; // 检测间隔
    private long scrollEndInterval = 20; // 滚动结束后保留时间
    public long latestScrollEventTime;
    private float latestTrackedVelocityX;
    private float latestTrackedVelocityY;
    public boolean scrollInProgress = false;
    private float translationThereshold = 1.0f;
    private long stopTime;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            //Do Something
            long now = System.currentTimeMillis();
            if (scrollInProgress && (now>latestScrollEventTime+scrollEndInterval) && Math.abs(Math.abs(xVelocity) - latestTrackedVelocityX) +  Math.abs(Math.abs(yVelocity) - latestTrackedVelocityY) < translationThereshold ) {
                scrollInProgress = false;

                mSimpleGestureListener.onTriggerJudge(true);

                //Log.e("String2","trigger this;");
                stopTime = now;


            }

            latestTrackedVelocityX = Math.abs(xVelocity);
            latestTrackedVelocityY = Math.abs(yVelocity);


            if(scrollInProgress){
                mSimpleGestureListener.onVelocityStop(false);
            }else{
                mSimpleGestureListener.onVelocityStop(true);
            }
        }
    };

    //或者 使用 static Hanlder + 弱引用，或者在 Frag|Activity销毁时 remove
    public void removeHandler(){
        mTimeCheckHandler.removeCallbacksAndMessages(null);
    }


    class timeCheckHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            long now = System.currentTimeMillis();
            if (scrollInProgress && (now>latestScrollEventTime+scrollEndInterval) && Math.abs(Math.abs(xVelocity) - latestTrackedVelocityX) +  Math.abs(Math.abs(yVelocity) - latestTrackedVelocityY) < translationThereshold ) {
                scrollInProgress = false;

                mSimpleGestureListener.onTriggerJudge(true);

                //Log.e("String2","trigger this;");
                stopTime = now;


            }

            latestTrackedVelocityX = Math.abs(xVelocity);
            latestTrackedVelocityY = Math.abs(yVelocity);


            if(scrollInProgress){
                mSimpleGestureListener.onVelocityStop(false);
            }else{
                mSimpleGestureListener.onVelocityStop(true);
            }

            this.sleep(timeCheckInterval);
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    // ############ PrintTag ############
    private void printTag(String tag,boolean show,int LOGTYPE,MotionEvent event){

        final String suffix;

        if(event == null){
            suffix = "";
        }
        else{
            int X = (int) event.getX();
            int Y = (int) event.getY();
            suffix = " - X: "+X+" Y: "+Y;
        }

        if(show){

            if(LOGTYPE == 0){

                StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
                StackTraceElement e = stacktrace[3];//maybe this number needs to be corrected
                String methodName = e.getMethodName();
                Log.i(tag,methodName + suffix);
            }
            else if(LOGTYPE == 1){

                StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
                StackTraceElement e = stacktrace[3];//maybe this number needs to be corrected
                String methodName = e.getMethodName();
                Log.e(tag,methodName + suffix);
            }
        }
        else{

        }
    }


}
