package com.zach.salman.springylib.springyRecyclerView;


import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Zach on 6/30/2017.
 */

public class SpringyAdapterAnimator {

    private static final int INIT_DELAY = 100;

    private static final int INIT_TENSION = 200;
    private static final int INIT_FRICTION = 20;
    private static final int INIT_INDEX = 0;

    private int tension;
    private int fraction ;


    private static final int PER_ITEM_GAPE = 10; //100
    private static final int PER_ITEM_INDEX = 1;
    public int DELETE_PER_DELAY = 10;

    private int parentHeight;
    private int parentWidth;
    private RecyclerView parent;
    private SpringSystem mSpringSystem;
    private SpringyAdapterAnimationType animationType;
    private boolean mFirstViewInit = true;
    private int mLastPosition = -1;
    private int mStartDelay;
    private int indexNum = 0;
    private int mReverseStartDelay;
    private int mReverseIndexNum = 0;

    public SpringyAdapterAnimator(RecyclerView recyclerView) {
        parent = recyclerView;
        mSpringSystem = SpringSystem.create();
        animationType = SpringyAdapterAnimationType.SLIDE_FROM_BOTTOM;
        parentHeight = parent.getResources().getDisplayMetrics().heightPixels;
        parentWidth = parent.getResources().getDisplayMetrics().widthPixels;
        mStartDelay = INIT_DELAY;
        tension = INIT_TENSION;
        fraction= INIT_FRICTION;
        indexNum = INIT_INDEX;
        mReverseStartDelay = INIT_DELAY;
        mReverseIndexNum = INIT_INDEX;
        //mSpringList = new ArrayList<Spring>();
    }
/*
* setInitDelay @param initDelay for set delay at screen creation
* */
    public void setInitDelay(int initDelay){
        mStartDelay = initDelay;
    }

    public void setSpringAnimationType(SpringyAdapterAnimationType type){
        animationType = type;
    }


    public void addConfig(int tension,int fraction){
        this.tension = tension;
        this.fraction = fraction;
    }

    /**
     * onSpringyItemCreate call in Adapter's Constructor method
     * @param item itemView instance from RecyclerView's OnCreateView method
     * **/
    public void onSpringItemCreate(View item,int position) {

        if (mFirstViewInit) {
            setAnimation(item, mStartDelay, tension, fraction,position);
            mStartDelay += PER_ITEM_GAPE;
            indexNum += PER_ITEM_INDEX;
        }
    }

    public void onSpringItemContinue(View item,int position){
        setContinueAnimation(item, 0, tension, fraction,position);

    }

    public void onSpringItemDelete(View item,int position) {


        setReverseAnimation(item, mReverseStartDelay, tension, fraction,position);
        mReverseStartDelay += DELETE_PER_DELAY;
        mReverseIndexNum += PER_ITEM_INDEX;
    }


    /**
     * * onSpringyItemBind call in RecyclerView's onBind for scroll effects
     * @param  item itemView instance from RecyclerView's onBind method
     * @param  position from RecyclerView's onBind method
     * **/
    public void onSpringItemBind(View item, int position) {

        if (!mFirstViewInit && position > mLastPosition) {
            setAnimation(item, 0, tension - tension/4, fraction,position);
            mLastPosition = position;
        }
    }

    public ArrayList<Spring> mSpringListArray = new ArrayList<>();
    public boolean rougeTriggered = false;
    private void setAnimation(final View item, final int delay,
                              final int tension, final int friction,final int position) {
        setInitValue(item,position);
        Runnable startAnimation = new Runnable() {
            @Override
            public void run() {
                SpringConfig config = new SpringConfig(150, 28);
                //mSpring = mSpringSystem.createSpring();
                //mSpring.setSpringConfig(config);
                Spring spring = mSpringSystem.createSpring();
                //mSpringList.add(position,spring);
//                if(mSpringListArray.get(position) != null){
//                    mSpringListArray.remove(position);
//                }
                mSpringListArray.add(position,spring);
                spring.setSpringConfig(config);
                //mSpringListArray.add();
                spring.addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        switch (animationType) {
                            case SLIDE_FROM_TOP:
                                item.setTranslationY(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_BOTTOM:
                                item.setTranslationY(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_LEFT:
                                item.setTranslationX(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_RIGHT:
                                item.setTranslationX(getMappedValue(spring,position));
                                break;
                            case SPREAD:
                                // Very Dirty Way of Animation
                                if(position == 0){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -700, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 1){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -700, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 2){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 3){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 4){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);
                                }
                                else if(position == 5){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                break;
                            case DIVE:
                                // Very Dirty Way of Animation
                                if(position == 0){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 350, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 1){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 350, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 2){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -1000, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 350, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 3){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1000, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 350, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 4){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*3, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);
                                }
                                else if(position == 5){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*3, 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                break;
                            case SCALE:
                                item.setScaleX(getMappedValue(spring,position));
                                item.setScaleY(getMappedValue(spring,position));
                                break;
                            case NULL:
                                break;
                        }

                    }

                    @Override
                    public void onSpringEndStateChange(Spring spring) {
                        mFirstViewInit = false;
                    }

                    @Override
                    public void onSpringAtRest(Spring spring) {
                        if(spring.getCurrentValue() == 1){

                        }
                    }
                });
                spring.setEndValue(1); //0.3
                //itemSpringStateSwitchTo(1,false);


                final Spring mSpring = spring;

            }
        };

        parent.postDelayed(startAnimation, delay);
    }

    private void setContinueAnimation(final View item, final int delay,
                                     final int tension, final int friction,final int position) {
        Runnable startAnimation = new Runnable() {
            @Override
            public void run() {
                SpringConfig config = new SpringConfig(240, 28);
                //mSpring = mSpringSystem.createSpring();
                //mSpring.setSpringConfig(config);
                Spring spring = mSpringSystem.createSpring();
                //mSpringList.add(position,spring);
                spring.setSpringConfig(config);
                spring.setCurrentValue(1);
                spring.addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        switch (animationType) {
                            case SLIDE_FROM_TOP:
                                item.setTranslationY(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_BOTTOM:
                                item.setTranslationY(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_LEFT:
                                item.setTranslationX(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_RIGHT:
                                item.setTranslationX(getMappedValue(spring,position));
                                break;
                            case SPREAD:
                                // Very Dirty Way of Animation
                                if(position == 0){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 1){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 2){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 3){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 4){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);
                                }
                                else if(position == 5){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                break;
                            case SCALE:
                                item.setScaleX(getMappedValue(spring,position));
                                item.setScaleY(getMappedValue(spring,position));
                                break;
                            case NULL:
                                break;
                        }

                    }

                    @Override
                    public void onSpringEndStateChange(Spring spring) {
                        mFirstViewInit = false;
                    }

                    @Override
                    public void onSpringAtRest(Spring spring) {
                        if(spring.getCurrentValue() == 1){

                        }
                    }
                });
                spring.setEndValue(1);
                //itemSpringStateSwitchTo(1,false);

            }
        };

        parent.postDelayed(startAnimation, delay);
    }

    private void setReverseAnimation(final View item, final int delay,
                              final int tension, final int friction,final int position) {
        //setReverseInitValue(item,position);
        Runnable startAnimation = new Runnable() {
            @Override
            public void run() {
                SpringConfig config = new SpringConfig(240, 28);
                //mSpring = mSpringSystem.createSpring();
                //mSpring.setSpringConfig(config);
                Spring spring = mSpringSystem.createSpring();
                //mSpringList.add(position,spring);
                spring.setSpringConfig(config);
                spring.setCurrentValue(1);
                spring.addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        switch (animationType) {
                            case SLIDE_FROM_TOP:
                                item.setTranslationY(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_BOTTOM:
                                item.setTranslationY(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_LEFT:
                                item.setTranslationX(getMappedValue(spring,position));
                                break;
                            case SLIDE_FROM_RIGHT:
                                item.setTranslationX(getMappedValue(spring,position));
                                break;
                            case SPREAD:
                                // Very Dirty Way of Animation
                                if(position == 0){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 1){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 2){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 3){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 4){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);
                                }
                                else if(position == 5){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                break;
                            case DIVE:
                                // Very Dirty Way of Animation
                                if(position == 0){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 1){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 700*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 2){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1400*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 3){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1400*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                else if(position == 4){

                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 2100*2. , 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);
                                }
                                else if(position == 5){
                                    float valueX = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 600*2., 0);
                                    float valueY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 2100*2., 0);
                                    item.setTranslationX(valueX);
                                    item.setTranslationY(valueY);

                                }
                                break;
                            case SCALE:
                                item.setScaleX(getMappedValue(spring,position));
                                item.setScaleY(getMappedValue(spring,position));
                                break;
                            case NULL:
                                break;
                        }

                    }

                    @Override
                    public void onSpringEndStateChange(Spring spring) {
                        mFirstViewInit = false;
                    }

                    @Override
                    public void onSpringAtRest(Spring spring) {
                        if(spring.getCurrentValue() == 1){

                        }
                    }
                });
                spring.setEndValue(0);
                //itemSpringStateSwitchTo(1,false);

            }
        };

        parent.postDelayed(startAnimation, delay);
    }

    private void setInitValue(View item,int position) {

        switch (animationType) {
            case SLIDE_FROM_TOP:
                item.setTranslationY(-parentHeight);
                break;
            case SLIDE_FROM_BOTTOM:
                item.setTranslationY(parentHeight*2.5f);
                break;
            case SLIDE_FROM_LEFT:
                item.setTranslationX(-parentWidth);
                break;
            case SLIDE_FROM_RIGHT:
                item.setTranslationX(parentWidth);
                break;
            case SCALE:
                item.setScaleX(0);
                item.setScaleY(0);
                break;
            case SPREAD:
                item.setTranslationX(-parentHeight);
                item.setTranslationY(-parentHeight);
                break;
            case DIVE:
                item.setTranslationX(-parentHeight*2);
                item.setTranslationY(parentHeight*2);
                break;
            case NULL:
                item.setTranslationX(0);
                item.setTranslationY(0);
                break;
            default:
                item.setTranslationY(parentHeight);
                break;
        }
    }

    private void setReverseInitValue(View item,int position) {
        item.setTranslationY(0);
        item.setTranslationX(0);
        item.setScaleX(0);
        item.setScaleY(0);
    }

    private float getMappedValue(Spring spring,int position) {

        float value;
        switch (animationType) {
            case SLIDE_FROM_TOP:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -parentHeight, 0);
                break;
            case SLIDE_FROM_BOTTOM:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, parentHeight, 0);
                break;
            case SLIDE_FROM_LEFT:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -parentWidth, 0);
                break;
            case SLIDE_FROM_RIGHT:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, parentWidth, 0);
                break;
            case SPREAD:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 1);
                break;
            case SCALE:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 1);
                break;
            case NULL:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 1);
                break;
            case DIVE:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 1);
                break;
            default:
                value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, parentHeight, 0);
                break;
        }
        return value;
    }


}
