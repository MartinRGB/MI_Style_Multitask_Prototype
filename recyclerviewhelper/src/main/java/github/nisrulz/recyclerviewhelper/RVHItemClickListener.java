/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.recyclerviewhelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * The type Rvh item click listener.
 */
public class RVHItemClickListener implements RecyclerView.OnItemTouchListener {
  private final OnItemClickListener mListener;
  /**
   * The M gesture detector.
   */
  private final GestureDetector mGestureDetector;

  /**
   * Instantiates a new Rvh item click listener.
   *
   * @param context
   *     the context
   * @param listener
   *     the listener
   */
  public RVHItemClickListener(Context context, OnItemClickListener listener) {
    mListener = listener;
    mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        return true;
      }

      @Override
      public boolean onDown(MotionEvent e) {
        return true;
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        return true;
      }

      @Override
      public void onShowPress(MotionEvent e) {
        //Log.e("On Show Press","ShowPress");
        mListener.onItemShowPress(longPressView,longPressPos,e.getX(),e.getY());
        mListener.detectShowPress(true,longPressPos);
        //手指触摸屏幕，并且尚未松开或拖动。与onDown的区别是，onShowPress强调没用松开和没有拖动
      }

      @Override
      public void onLongPress(MotionEvent e){
        mListener.onItemLongPress(longPressView,longPressPos,e.getX(),e.getY());
      }


    });
  }

  private View longPressView;
  private int longPressPos;

  @Override
  public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
    View childView = view.findChildViewUnder(e.getX(), e.getY());
//    if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e) && childView !=view.getChildAt(0)) {
//
//
//      if(e.getAction() == MotionEvent.ACTION_UP){
//        Log.e("Here","Here");
//        mListener.onItemTouchUp(childView, view.getChildAdapterPosition(childView),e.getX(),e.getY());
//      }
//      else if (e.getAction() == MotionEvent.ACTION_DOWN){
//        longPressView = childView;
//        longPressPos =  view.getChildAdapterPosition(childView);
//        mListener.onItemTouchDown(childView, view.getChildAdapterPosition(childView),e.getX(),e.getY());
//      }
//      return true;
//
//    }

    if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e) ) {


      if(e.getAction() == MotionEvent.ACTION_UP){
        Log.e("RVHClickListenr","UP");
        mListener.detectShowPress(false,longPressPos);
        mListener.onItemTouchUp(childView, view.getChildAdapterPosition(childView),e.getX(),e.getY());
        return false;
      }
      else if (e.getAction() == MotionEvent.ACTION_DOWN){
        Log.e("RVHClickListenr","Down");
        longPressView = childView;
        longPressPos =  view.getChildAdapterPosition(childView);
        mListener.onItemTouchDown(childView, view.getChildAdapterPosition(childView),e.getX(),e.getY());
        //让事件继续传递，从 rv 到 adapter 的 item里面
        return false;
      }

      else if (e.getAction() == MotionEvent.ACTION_MOVE){
        //Log.e("RVHClickListenr","MOVE");
        //让事件继续传递，从 rv 到 adapter 的 item里面
        mListener.onItemTouchMove(childView, view.getChildAdapterPosition(childView),e.getX(),e.getY());
        return false;
      }
      else if (e.getAction() == MotionEvent.ACTION_CANCEL){
        //Log.e("RVHClickListenr","CANCEL");
        //让事件继续传递，从 rv 到 adapter 的 item里面
        return false;
      }

    }

    return false;
  }

  @Override
  public void onTouchEvent(RecyclerView view, MotionEvent e) {
    // Do nothing
  }

  @Override
  public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    // Do nothings
  }

  /**
   * The interface On item click listener.
   */
  public interface OnItemClickListener {
    /**
     * On item click.
     *
     * @param view
     *     the view
     * @param position
     *     the position
     */
    void onItemTouchUp(View view, int position,float dX,float dY);
    void onItemTouchDown(View view, int position,float dX,float dY);
    void onItemTouchMove(View view, int position,float dX,float dY);
    void onItemLongPress(View view,int position,float dX,float dY);
    void onItemShowPress(View view,int position,float dX,float dY);
    void onOutsideTouch(float dX,float dY);
    void detectShowPress(boolean boo,int position);
  }
}
