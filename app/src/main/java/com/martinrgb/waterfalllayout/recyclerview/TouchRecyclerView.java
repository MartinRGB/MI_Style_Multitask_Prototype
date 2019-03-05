package com.martinrgb.waterfalllayout.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


public class TouchRecyclerView extends RecyclerView
{
    // Depending on how you're creating this View,
    // you might need to specify additional constructors.
    public TouchRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private OnNoChildClickListener listener;
    public interface OnNoChildClickListener
    {
        public void onNoChildClick(float posX,float posY);
    }


    public void setOnNoChildClickListener(OnNoChildClickListener listener)
    {
        this.listener = listener;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        // The findChildViewUnder() method returns null if the touch event
        // occurs outside of a child View.
        // Change the MotionEvent action as needed. Here we use ACTION_DOWN
        // as a simple, naive indication of a click.
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && findChildViewUnder(event.getX(), event.getY()) == null)
        {
            if (listener != null)
            {
                listener.onNoChildClick(event.getX(),event.getY());
            }


        }
        return super.dispatchTouchEvent(event);
    }
}