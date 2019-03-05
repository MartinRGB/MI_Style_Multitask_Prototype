package com.martinrgb.waterfalllayout.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.eightbitlab.supportrenderscriptblur.SupportRenderScriptBlur;
import com.facebook.rebound.SpringConfigRegistry;
import com.facebook.rebound.ui.SpringConfiguratorView;
import com.martinrgb.waterfalllayout.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import github.nisrulz.recyclerviewhelper.RVHItemClickListener;
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.martinrgb.waterfalllayout.utils.AnimUtil;
import com.martinrgb.waterfalllayout.utils.MultiGestureDetector;
import com.martinrgb.waterfalllayout.utils.VibratorUtil;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";
    private ImageView mStatsBar;
    private ImageView mCloseBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_list);

        setupBlurView();
        setupSpring();
        setupRecyclerView();
        setDragListener();
        setCloseFunction();

        mBlurSpring.setCurrentValue(25);
        mIconSpring.setEndValue(1);




        Log.e("RV",String.valueOf(recyclerView.getPaddingTop()));

        recyclerView.setAlpha(0);
        //mAttachSpringS.setCurrentValue(13.5);
        mAttachSpringA.setCurrentValue(1);

        mRVYSpring.setCurrentValue(1800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAlpha(1);
                mAttachSpringA.setEndValue(0);

            }
        },300 );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRVYSpring.setSpringConfig(mClickScaleBack);
                mRVYSpring.setEndValue(0);
            }
        },400 );


    }

    @Override
    protected void onDestroy() {
        homeScreenGestureDetector.removeHandler();
        mMultiGestureDetector.removeHandler();
        timerHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    //################### Setup Blur View ###################
    private BlurView blurView;
    private BlurView.ControllerSettings topViewSettings;

    private void setupBlurView() {
        final float maxBlurradius = 25f;
        final float minBlurRadius = 0.00001f;
        final float step = 4f;

        final View decorView = getWindow().getDecorView();
        //Activity's root View. Can also be root View of your layout (preferably)
        final ViewGroup rootView = (ViewGroup) decorView.findViewById(R.id.backgroundview);
        //set background, if your root layout doesn't have one
        //final Drawable windowBackground = decorView.getBackground();
        blurView = (BlurView) findViewById(R.id.blurView);

        //set background, if your root layout doesn't have one
        final Drawable windowBackground = getWindow().getDecorView().getBackground();

        topViewSettings = blurView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new SupportRenderScriptBlur(this))
                .blurRadius(maxBlurradius);

        blurView.setAlpha(1);

    }


    //################### Setup Spring Animation ###################
    private SpringSystem mSpringSystem;
    private Spring mSpringX;
    private Spring mSpringS;
    private Spring mAttachSpringS;
    private Spring mAttachSpringA;
    private Spring mMaskSpringA;
    private Spring mSwipeUpSpringScale;
    private Spring mSwipeUpSpringPosition;
    private Spring mGestureSpringX;
    private Spring mGestureSpringY;
    private Spring mLayoutSpringA;
    private Spring mBlurSpring;
    private Spring mStatsSpring;
    private Spring mCloseSpringX;
    private Spring mCloseSpringS;
    private Spring mCloseBlurSpring;
    private static final SpringConfig mconfigXGo = SpringConfig.fromOrigamiTensionAndFriction(177, 20);
    private static final SpringConfig mconfigSGo = SpringConfig.fromOrigamiTensionAndFriction(177, 20);
    private static final SpringConfig mconfig2XGo = SpringConfig.fromOrigamiTensionAndFriction(150, 15);
    private static final SpringConfig mconfig2SGo = SpringConfig.fromOrigamiTensionAndFriction(150, 15);
    private static final SpringConfig mconfigXBack = SpringConfig.fromOrigamiTensionAndFriction(90, 12);
    private static final SpringConfig mconfigSBack = SpringConfig.fromOrigamiTensionAndFriction(90, 12);
    private static final SpringConfig mconfig2XBack = SpringConfig.fromOrigamiTensionAndFriction(90, 12);
    private static final SpringConfig mconfig2SBack = SpringConfig.fromOrigamiTensionAndFriction(90, 12);
    private static final SpringConfig mClickScaleBack = SpringConfig.fromOrigamiTensionAndFriction(60, 13);
    private static SpringConfig mMultiAnim = SpringConfig.fromOrigamiTensionAndFriction(160 , 18);
    private static SpringConfig mSwipeScaleGo = SpringConfig.fromOrigamiTensionAndFriction(100, 18);
    private static SpringConfig mSwipePositionGo = SpringConfig.fromOrigamiTensionAndFriction(100, 18);
    private static SpringConfig mStatsRapid = SpringConfig.fromOrigamiTensionAndFriction(200, 18);
    private static SpringConfig mStatsSlow = SpringConfig.fromOrigamiTensionAndFriction(100, 28);
    private static SpringConfig mLongPressConfig = SpringConfig.fromOrigamiTensionAndFriction(120, 20);
    private static SpringConfig mLongPressSpringConfig = SpringConfig.fromOrigamiTensionAndFriction(200, 8);
    private static SpringConfig mLongPressConfig2 = SpringConfig.fromOrigamiTensionAndFriction(150, 15);
    private float bgScaleValue = 1.5f;
    private float paddingFix = 394 + 144 - 12 - 3 - 80 - 83;  // 264dp 702  152dp 394   |  3 is icon 56-64 fix
    private float rvPosFix = 0;
    private float rvScaleX,rvScaleY,rvTransX,rvTransY;
    private float cdScaleX,cdScaleY,cdTransX,cdTransY;
    private float nowScaleX,nowScaleY;
//    private Spring mLayoutDownSpring;
//    private Spring mLayoutUpSpring;
//    private Spring mLayoutRemoveSpring;
    private Spring mRVYSpring;
    private Spring mIconSpring;
    private Spring mLongPressSpringX,mLongPressSpringY,mLongPressSpringT;
    private Spring mLongPressCardSpring;
    private float rvRapidPosFix = 0;
    private boolean canSwap = false;

    private void setupSpring(){

        mCloseBtn = (ImageView) findViewById(R.id.closebtn);
        mStatsBar = (ImageView) findViewById(R.id.statsbar);

        mSpringSystem = SpringSystem.create();
        mSpringX = mSpringSystem.createSpring();
        mSpringX.setSpringConfig(mconfigXGo);
        mSpringS = mSpringSystem.createSpring();
        mSpringS.setSpringConfig(mconfigSGo);

        mAttachSpringA = mSpringSystem.createSpring();
        mAttachSpringA.setSpringConfig(mconfig2XGo);
        mAttachSpringS = mSpringSystem.createSpring();
        mAttachSpringS.setSpringConfig(mconfig2SGo);

        mMaskSpringA = mSpringSystem.createSpring();
        mMaskSpringA.setSpringConfig(mconfig2XGo);

        mCloseSpringX = mSpringSystem.createSpring();
        mCloseSpringX.setSpringConfig(mMultiAnim);

        mCloseSpringS = mSpringSystem.createSpring();
        mCloseSpringS.setSpringConfig(mClickScaleBack);

        mCloseBlurSpring = mSpringSystem.createSpring();
        mCloseBlurSpring.setSpringConfig(mClickScaleBack);

        mBlurSpring = mSpringSystem.createSpring();
        mBlurSpring.setSpringConfig(mClickScaleBack);

        mIconSpring = mSpringSystem.createSpring();
        mIconSpring.setSpringConfig(mClickScaleBack);

        mStatsSpring = mSpringSystem.createSpring();
        mStatsSpring.setSpringConfig(mStatsRapid);

        mLongPressSpringX = mSpringSystem.createSpring();
        mLongPressSpringX.setSpringConfig(mMultiAnim);

        mLongPressSpringY = mSpringSystem.createSpring();
        mLongPressSpringY.setSpringConfig(mClickScaleBack);

        mLongPressSpringT = mSpringSystem.createSpring();
        mLongPressSpringT.setSpringConfig(mClickScaleBack);

        mLongPressCardSpring = mSpringSystem.createSpring();
        mLongPressCardSpring.setSpringConfig(mLongPressConfig);


        mRVYSpring = mSpringSystem.createSpring();
        mRVYSpring.setSpringConfig(mMultiAnim);

        mLayoutSpringA = mSpringSystem.createSpring();
        mLayoutSpringA.setSpringConfig(mSwipeScaleGo);

        setSpringConfig();

        findViewById(R.id.gestureimgview).setPivotX(540);
        findViewById(R.id.gestureimgview).setPivotY(0);


        mRVYSpring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float progress = (float) mSpring.getCurrentValue();

                //##LayouHeight
                recyclerView.setTranslationY(progress);

            }

            public void onSpringAtRest(Spring mSpring) {

                if(mSpring.getCurrentValue() == 0){

                    eventAfterClose();
                }
                else{
                    eventOnOpen();
                }
            }
        });

        mLongPressSpringX.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float progress = (float) mSpring.getCurrentValue();

                mCloseBtn.setTranslationY(mCloseBtn.getHeight()*progress);
                findViewById(R.id.longpressmenuimg).setScaleX(progress);


            }
        });

        mLongPressSpringY.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float progress = (float) mSpring.getCurrentValue();

                float mScale = (float) SpringUtil.mapValueFromRangeToRange(progress, 0, 1, 0.25, 1);

                findViewById(R.id.longpressmenuimg).setScaleY(mScale);


            }
        });

        mLongPressSpringT.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float progress = (float) mSpring.getCurrentValue();

                float transY = (float) SpringUtil.mapValueFromRangeToRange(progress, 0, 1, 0, 64);

                findViewById(R.id.longpressmenutext).setTranslationY(transY);
                findViewById(R.id.longpressmenutext).setAlpha(progress);


            }
        });

        mLongPressCardSpring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float progress = (float) mSpring.getCurrentValue();

                float mScale = (float) SpringUtil.mapValueFromRangeToRange(progress, 0, 1, 1, 1.1);
                if(recyclerView.getLayoutManager().findViewByPosition(downCardIndex) !=null){

                    recyclerView.getLayoutManager().findViewByPosition(downCardIndex).findViewById(R.id.iv_card).setScaleX(mScale);
                    recyclerView.getLayoutManager().findViewByPosition(downCardIndex).findViewById(R.id.iv_card).setScaleY(mScale);

                }


            }
        });


        mStatsSpring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();
                mStatsBar.setAlpha(value);



            }
        });

        mLayoutSpringA.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();
                findViewById(R.id.gestureLayout).setAlpha(value);
                findViewById(R.id.shadow).setAlpha(value);



            }
        });


        mCloseSpringX.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();
                mCloseBtn.setTranslationY(mCloseBtn.getHeight()*value);



            }

        });

        mCloseSpringS.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();
                if(value<0.5){

                    mCloseBtn.setScaleX(1 - value*0.6f);
                    mCloseBtn.setScaleY(1 - value*0.6f);
                }
                else{

                    mCloseBtn.setScaleX(0.7f + (value-0.5f)*0.6f);
                    mCloseBtn.setScaleY(0.7f + (value-0.5f)*0.6f);
                }



            }

        });

        mCloseBlurSpring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();

                float mBlurValueRange = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 25, 0.01);
                float mBlurRadius =(float) Math.max(0.01,Math.min(25,mBlurValueRange));
                topViewSettings.blurRadius(mBlurValueRange);


            }

            public void onSpringAtRest(Spring mSpring) {

            }
        });



        mBlurSpring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();

                if(value < 0.02 && !shoudAnimateBlurView){
                    blurView.setAlpha(0);
                }
                else if(value>=0.02 && !shoudAnimateBlurView){
                    blurView.setAlpha(Math.max(1,value));
                }


//                float iconScale = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 25, 1, 0.8);
//                findViewById(R.id.iconlist).setScaleX(iconScale);
//                findViewById(R.id.iconlist).setScaleY(iconScale);
//
                float iconScale2 = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 25, 1, 1.1);


                float mBlurRadius =(float) Math.max(0.01,Math.min(25,value));
                topViewSettings.blurRadius(mBlurRadius);
                blurView.setScaleX(iconScale2);
                blurView.setScaleY(iconScale2);

            }
        });

        mIconSpring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();

                float iconScale = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, 0.88);
                findViewById(R.id.iconlist).setScaleX(iconScale);
                findViewById(R.id.iconlist).setScaleY(iconScale);
                findViewById(R.id.iconlist).setAlpha(1.0f - value *0.5f);


            }
        });

        mSpringS.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();

                if(recyclerView.getLayoutManager().findViewByPosition(clickCount) !=null){

                    if(hasTriggered){
                        float valueSX = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, nowScaleX);
                        float valueSY = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, nowScaleY);

                        recyclerView.getLayoutManager().findViewByPosition(clickCount).setScaleX(valueSX);
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).setScaleY(valueSY);

                    }
                    else{


                        float valueSX = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, 2.3076f); // 2.3076f/bgScaleValue
                        float valueSY = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, 2.5);

                        cdScaleX= valueSX;
                        cdScaleY= valueSY;
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).setScaleX(valueSX);
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).setScaleY(valueSY);

                    }
                }


            }
        });


        mSpringX.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                float value = (float) mSpring.getCurrentValue();


                if(recyclerView.getLayoutManager().findViewByPosition(clickCount) !=null){


//                    if(clickCount == 0){
//                        float valueTY = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 0, 612-nowPositionInScreen[1] + 24 + 466 + rvPosFix/3.0);
//                        recyclerView.getLayoutManager().findViewByPosition(clickCount).setTranslationY(valueTY);
//                    }
//                    else{
//
//                        float valueTY = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 0, 612-nowPositionInScreen[1] + 24  + 32); // +24 + rvPosFix/3.0
//                        recyclerView.getLayoutManager().findViewByPosition(clickCount).setTranslationY(valueTY);
//                    }

                    float valueTY = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 0, 612-nowPositionInScreen[1] + 156); // +24 + rvPosFix/3.0 //24  + 32
                    recyclerView.getLayoutManager().findViewByPosition(clickCount).setTranslationY(valueTY);

                    float valueTX = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 0, 306-nowPositionInScreen[0] - 23); // -20
                    recyclerView.getLayoutManager().findViewByPosition(clickCount).setTranslationX(valueTX);

                    if(value <0.05){
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.tv_title).setAlpha(1-value*20);
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.iv_icon).setAlpha(1-value*20);
                    }
                    else{
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.tv_title).setAlpha(0);
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.iv_icon).setAlpha(0);
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.iv_card).setElevation(0);
                    }

                }

            }

            @Override
            public void onSpringAtRest(Spring mSpring){

                double mSwapValue = mSpring.getCurrentValue();
                if(mSwapValue == 0){
                    //Trans to mSwipeScale
                    if(recyclerView.getLayoutManager().findViewByPosition(clickCount) != null){

                        Log.e("Trigger0",String.valueOf(clickCount));
                        mAdapter.canSwap = false;
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.iv_card).setAlpha(1.0f);
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).setTranslationZ(0);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.gestureContainer).setVisibility(View.INVISIBLE);


                                //Fake



                                ValueAnimator anim = ValueAnimator.ofFloat(0f, 18f);
                                anim.setDuration(300);
                                anim.setInterpolator(new AnimUtil.QuadEaseOutInterpolater());
                                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        float currentValue = (float) animation.getAnimatedValue();
                                        if(recyclerView.getLayoutManager().findViewByPosition(clickCount) !=null && recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.iv_card) != null){

                                            recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.iv_card).setElevation(currentValue);

                                        }
                                    }


                                });
                                anim.addListener(new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        eventAfterClose();

                                    }
                                });
                                anim.start();

                            }
                        },150 );

                    }
                    hasTriggered = false;
                }
                else if(mSwapValue == 1){
                    if(recyclerView.getLayoutManager().findViewByPosition(clickCount) != null) {

                        Log.e("Trigger1","Trigger1");
                        mAdapter.canSwap = true;
                        eventAfterOpen();
                        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), mSrc[mOrignCount]);
                        //Try Resolove GC Problem
                        Bitmap bitmapNew = mBitmap.copy(Bitmap.Config.ARGB_4444, true);

                        mBitmap.recycle();
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        //Try Resolove GC Problem - 30
                        bitmapNew.compress(Bitmap.CompressFormat.JPEG, 40, out);
                        Bitmap decodedBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        BitmapDrawable mDrawable = new BitmapDrawable(decodedBitmap);
                        findViewById(R.id.gestureimgview).setBackgroundDrawable(mDrawable);
                        bitmapNew.recycle();


                        recyclerView.scrollToPosition(0);
                        scrollValue = 0;

                        mBlurSpring.setCurrentValue(0);
                        mIconSpring.setCurrentValue(0);
                        //Fake
                        mLayoutSpringA.setCurrentValue(1);
                        findViewById(R.id.gestureContainer).setVisibility(View.VISIBLE);
                        recyclerView.getLayoutManager().findViewByPosition(clickCount).findViewById(R.id.iv_card).setAlpha(0.0f);
                        mAttachSpringS.setCurrentValue(0);

                    }
                }
            }
        });

        mAttachSpringS.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                float value = (float) mSpring.getCurrentValue();
                float valueSX2 = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, bgScaleValue);
                float valueSY2 = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, bgScaleValue);
                //recyclerView.setScaleX(valueSX2);
                //recyclerView.setScaleY(valueSY2);

                for(int i=0;i<cardList.size();i++){


                    if(recyclerView.getLayoutManager().findViewByPosition(i) !=null){

                        if(canInHomeScreen){

                            float valueTY = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 0, 800); // +24 + rvPosFix/3.0
                            recyclerView.getLayoutManager().findViewByPosition(i).setTranslationY(valueTY);
                        }
                        else{
                            if(i != clickCount){
                                float valueTY = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 0, 800); // +24 + rvPosFix/3.0
                                recyclerView.getLayoutManager().findViewByPosition(i).setTranslationY(valueTY);
                            }
                        }



                    }
                }


            }

        });

        mMaskSpringA.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();
                findViewById(R.id.blackmask).setAlpha(value);

            }

        });

        mAttachSpringA.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();

                for(int i = 0;i<cardList.size();i++){
                    if(recyclerView.getLayoutManager().findViewByPosition(i) !=null && i != clickCount){


                        float valueAlpha = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, 0);

                        recyclerView.getLayoutManager().findViewByPosition(i).setAlpha(valueAlpha);
                        if(recyclerView.getLayoutManager().findViewByPosition(0) !=null){

                            recyclerView.getLayoutManager().findViewByPosition(0).setAlpha(valueAlpha);
                        }

                        if(hasInHomeScreen && recyclerView.getLayoutManager().findViewByPosition(clickCount) !=null){
                            recyclerView.getLayoutManager().findViewByPosition(clickCount).setAlpha(valueAlpha);
                        }

                    }

                }
            }


        });


        mSwipeUpSpringScale = mSpringSystem.createSpring();
        mSwipeUpSpringScale.setSpringConfig(mSwipeScaleGo);
        mSwipeUpSpringPosition = mSpringSystem.createSpring();
        mSwipeUpSpringPosition.setSpringConfig(mSwipePositionGo);

        mGestureSpringX = mSpringSystem.createSpring();
        mGestureSpringX.setSpringConfig(mMultiAnim);
        mGestureSpringY = mSpringSystem.createSpring();
        mGestureSpringY.setSpringConfig(mMultiAnim);

        mGestureSpringX.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                float progress = (float) mSpring.getCurrentValue();
                findViewById(R.id.gestureLayout).setTranslationX(progress);
                findViewById(R.id.shadow).setTranslationX(progress);
            }


        });

        mGestureSpringY.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                float progress = (float) mSpring.getCurrentValue();
                findViewById(R.id.gestureLayout).setTranslationY(progress);
                findViewById(R.id.shadow).setTranslationY(progress+30);
            }


        });

        mSwipeUpSpringScale.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float progress = (float) mSpring.getCurrentValue();

                float containerScaleX = Math.min(1f,(float) SpringUtil.mapValueFromRangeToRange(progress, 0, 1+0.05, 1., finalScale));
                float containerScaleY = Math.min(1f,(float) SpringUtil.mapValueFromRangeToRange(progress, 0, 1 - 0.002, 1., finalScale));

                findViewById(R.id.gestureLayout).setScaleX(containerScaleX);
                findViewById(R.id.gestureLayout).setScaleY(containerScaleY);

                findViewById(R.id.shadow).setScaleX(containerScaleX*1.11f);
                findViewById(R.id.shadow).setScaleY(containerScaleY*1.05f);

            }

            public void onSpringAtRest(Spring mSpring) {


            }

        });
    }

    //################### Setup HomseScren Transition InOut & Data Remove ###################
    private boolean hasInHomeScreen = false;
    private boolean canInHomeScreen = true;
    private View.OnClickListener closeBtnListener;
    private View.OnClickListener mBlurClickListener;
    private TouchRecyclerView.OnNoChildClickListener noChildClickListener;
    //private TouchRecyclerView.OnScrollUpCancelListener scrollUpCancelListener;
    private MultiGestureDetector homeScreenGestureDetector;
    private boolean hsHasVibrate = false;
    private boolean  shoudAnimateBlurView = false;
    private float hsEndY,prevProgressY;
    private void setCloseFunction(){

        //In Transition
        mBlurClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        };

        //Remove All
        closeBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!hasInHomeScreen && canInHomeScreen){

                    Log.e("remove","Here");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            shoudAnimateBlurView = true;
                            //mBlurSpring.setCurrentValue(25);
                            mBlurSpring.setEndValue(0);
//                            mCloseBlurSpring.setEndValue(1);
                            blurView.animate().alpha(0).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                            blurView.setTranslationZ(20000);
                            //blurView.setOnClickListener(mBlurClickListener);
                            blurView.setOnTouchListener(homeScreenGestureDetector);
                            clickCount = 0;


                        }
                    },700 );

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mIconSpring.setEndValue(0);
                        }
                    },800 );


                    mAttachSpringA.setEndValue(0);

                    hasInHomeScreen = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            cardList.clear();
                            mCloseSpringX.setEndValue(1);
                            //recyclerView.destroyDrawingCache();
                        }
                    },450 );

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mCloseBtn.setImageResource(R.drawable.closebtnp);
                        }
                    },150 );

                    mCloseSpringS.setEndValue(1);

                    //findViewById(R.id.iconlist).animate().alpha(1).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                    if(findViewById(R.id.tinttext).getAlpha() == 0){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.tinttext).animate().alpha(1).setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                            }
                        },100 );


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.tinttext).animate().alpha(0).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                            }
                        },500 );
                    }
                    else{
                        findViewById(R.id.tinttext).animate().alpha(0).setDuration(250).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    }


                    mAdapter.removeAll();
                }

            }
        };

        mCloseBtn.setOnClickListener(closeBtnListener);




        //Out Transition
        noChildClickListener = new TouchRecyclerView.OnNoChildClickListener() {
            @Override
            public void onNoChildClick(float posX,float posY) {
                if(posY < 500+rvPosFix){

                    if(!longPressEnabled){

                        outTransitionTop(false);
                    }

                }

                if(longPressEnabled) {
                    unLongPressEffect();
                }
            }
        };
        recyclerView.setOnNoChildClickListener(noChildClickListener);


        //Gesture Detector
        homeScreenGestureDetector = new MultiGestureDetector(new MultiGestureDetector.SimpleGestureListener() {

            private long hsDownTime;
            private long hsUpTime;
            private float hsStartY;
            private float hsDistanceY;

            @Override
            public void onDown(MotionEvent event){
                hsDownTime = System.currentTimeMillis();
                hsStartY = event.getY();
                hsEndY = 0;
                shouldSwipeFix = false;

            }


            @Override
            public void onVelocityStop(boolean boo){}


            @Override
            public void onTriggerJudge(boolean boo) {}
            @Override
            public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float velocityX, float velocityY) {


                // Scroll Up Condition
                if (hasInHomeScreen && hsStartY > screenHeight - bottomScrollHeight*3) {



                    shoudAnimateBlurView = false;
                    hsDistanceY = hsStartY - e2.getY();
                    float totalDistanceY = 2800;

                    float distanceProgressY = hsDistanceY/totalDistanceY;
                    float expDistProgressY = (float) Math.max(0, Math.min(1, Math.exp(1 / (-distanceProgressY / (1. + distanceProgressY) - 0.226))));

                    //Log.e("exp",String.valueOf(expDistProgressY*3));

                    mIconSpring.setEndValue(Math.min(0.7,expDistProgressY*6));

                    if(!canTriggeredStart){
                        //mBlurSpring.setEndValue(25);
                        //Log.e("BlurValue",String.valueOf(Math.min(0.7,(expDistProgressY)*6)*25 + 0.02));
                        mBlurSpring.setEndValue(Math.min(0.7,(expDistProgressY)*6)*25 + 0.02 - 3);
                        mIconSpring.setEndValue(1);
                    }
                    else{
                    }


                    if(e2.getY()>screenHeight - bottomScrollHeight*3){
                        if(!hsHasVibrate){

                            VibratorUtil.Vibrate(getApplicationContext(),20);
                            hsHasVibrate = true;
                        }
                    }

                    if(e2.getY()<screenHeight - bottomScrollHeight*3){
                        if(hsHasVibrate){

                            VibratorUtil.Vibrate(getApplicationContext(),20);
                            hsHasVibrate = false;
                        }

                        if(canTriggeredStart){


                            setupRecyclerView();


                            mAttachSpringS.setCurrentValue(0);
                            //mAttachSpringA.setCurrentValue(1);
                            //mAttachSpringA.setEndValue(0.2);
                            mAttachSpringA.setCurrentValue(0);
                            recyclerView.setVisibility(View.VISIBLE);

                            mCloseBtn.setVisibility(View.INVISIBLE);


                            mRVYSpring.setSpringConfig(mClickScaleBack);
                            mRVYSpring.setCurrentValue(3400);
                            mRVYSpring.setEndValue(1600);

//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    recyclerView.setAlpha(1);
//                                    //mAttachSpringS.setSpringConfig(mconfigXGo);
//                                    //mAttachSpringS.setEndValue(0);
//
//                                    mRVYSpring.setSpringConfig(mClickScaleBack);
//                                    mRVYSpring.setCurrentValue(2400);
//                                    mRVYSpring.setEndValue(1600);
//
//                                }
//                            },300 );

                            canTriggeredStart = false;

                            blurView.setTranslationZ(0);
                        }

                        if(!canTriggeredStart){
                            mRVYSpring.setSpringConfig(mMultiAnim);
                            mRVYSpring.setEndValue(1600 - (screenHeight - bottomScrollHeight*3 - e2.getY()));
                        }
                    }


                }


            }

            @Override
            public void onLongPress(MotionEvent event) {

            }

            @Override
            public void onSwipeTop(float velocity) {


                if (hsStartY > screenHeight - bottomScrollHeight*3 && hasInHomeScreen) {

                    recyclerView.setVisibility(View.INVISIBLE);

                    shoudAnimateBlurView = true;
                    blurView.animate().alpha(0).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    mBlurSpring.setEndValue(0);
                    mIconSpring.setEndValue(0);
                    hsHasVibrate = false;


                    hasInHomeScreen = true;
                    canTriggeredStart = true;
                    Log.e(TAG, "HS SwipeTop");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cardList.clear();
                            recyclerView.destroyDrawingCache();
                        }
                    },350 );

                    mAdapter.outTransition();
                }

            }

            @Override
            public void onSwipeTopFix() {
                Log.e("out","HS SwipeTopFIX");
                if(hasInHomeScreen){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                    },400 );
                    shoudAnimateBlurView = true;
                    blurView.animate().alpha(0).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    mBlurSpring.setEndValue(0);
                    mIconSpring.setEndValue(0);
                    hsHasVibrate = false;


                    hasInHomeScreen = true;
                    canTriggeredStart = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cardList.clear();
                            recyclerView.destroyDrawingCache();
                        }
                    },350 );

                    mAdapter.outTransition();
                }



            }


            @Override
            public void onUp(MotionEvent event) {
                hsUpTime = System.currentTimeMillis();
                hsEndY = event.getY();

                //Click Event
                if (hsUpTime - hsDownTime <= 200) {

                    //inTransition();

                }

                //Up Event
                else {
                    // 1.Scroll Up Do not Triggered ( Not worked yet);
                    if (event.getY() > screenHeight - bottomScrollHeight*3 && hasInHomeScreen && hsStartY > screenHeight - bottomScrollHeight*3) {


                        mRVYSpring.setEndValue(2600);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setVisibility(View.INVISIBLE);
                            }
                        },400 );

                        shoudAnimateBlurView = true;
                        blurView.animate().alpha(0).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                        mBlurSpring.setEndValue(0);
                        mIconSpring.setEndValue(0);
                        hsHasVibrate = false;
                        hasInHomeScreen = true;
                        canTriggeredStart = true;
                        Log.e(TAG, "HS Error Up");


                    }
                    //Drag 后进 HomeScreen
                    else if (event.getY() < screenHeight - bottomScrollHeight*3 && hasInHomeScreen && hsStartY > screenHeight - bottomScrollHeight*3) {
                        hasInHomeScreen = false;
                        hsHasVibrate = false;
                        canTriggeredStart = true;
                        inTransition();
                        mAttachSpringA.setEndValue(0);
                        //findViewById(R.id.blackmask).setVisibility(View.VISIBLE);
                        mRVYSpring.setSpringConfig(mLongPressConfig);
                        mRVYSpring.setEndValue(0);
                        Log.e(TAG, "HS Up");


                    }
                    else {


                    }

                }
            }

        });
    }

    private void outTransition(){
        if(!hasInHomeScreen && canInHomeScreen){


            Log.e("out","Here");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    shoudAnimateBlurView = true;
                    mBlurSpring.setEndValue(0);
//                    mCloseBlurSpring.setEndValue(1);
                    blurView.animate().alpha(0).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    blurView.setTranslationZ(20000);
                    //blurView.setOnClickListener(mBlurClickListener);
                    blurView.setOnTouchListener(homeScreenGestureDetector);
                    //mAttachSpringA.setCurrentValue(1);
                    clickCount = 0;
                }
            },250 );


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mIconSpring.setEndValue(0);
                }
            },350 );

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    cardList.clear();
                    recyclerView.destroyDrawingCache();
                }
            },350 );


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //mAttachSpringS.setSpringConfig(mconfigXGo);
                    mAttachSpringA.setSpringConfig(mconfigXGo);
                    //mAttachSpringS.setEndValue(1);
                    mAttachSpringA.setEndValue(1);
                }
            },150 );

            hasInHomeScreen = true;
            mCloseSpringX.setEndValue(1);
            mCloseSpringS.setEndValue(1);
            findViewById(R.id.tinttext).animate().alpha(0).setDuration(250).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            mAdapter.outTransition();
        }
    }

    private void outTransitionTop( boolean boo){
        if(!hasInHomeScreen && canInHomeScreen){


            Log.e("out","Here");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    shoudAnimateBlurView = true;
                    mBlurSpring.setEndValue(0);
//                    mCloseBlurSpring.setEndValue(1);
                    blurView.animate().alpha(0).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    blurView.setTranslationZ(20000);
                    //blurView.setOnClickListener(mBlurClickListener);
                    blurView.setOnTouchListener(homeScreenGestureDetector);
                    //mAttachSpringA.setCurrentValue(1);
                    clickCount = 0;
                }
            },250 );


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mIconSpring.setEndValue(0);
                }
            },350 );

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    cardList.clear();
                    recyclerView.destroyDrawingCache();
                }
            },350 );


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //mAttachSpringS.setSpringConfig(mconfigXGo);
                    mAttachSpringA.setSpringConfig(mconfigXGo);
                    //mAttachSpringS.setEndValue(1);
                    mAttachSpringA.setEndValue(1);
                }
            },150 );

            hasInHomeScreen = true;
            mCloseSpringX.setEndValue(1);
            mCloseSpringS.setEndValue(1);
            findViewById(R.id.tinttext).animate().alpha(0).setDuration(250).setInterpolator(new AccelerateDecelerateInterpolator()).start();

            if(boo){

                mAdapter.outTransition2();
            }
            else{

                mAdapter.outTransition3();
            }
        }
    }

    private boolean canTriggeredStart = true;
    private boolean shouldSwipeFix = false;
    private void inTransition(){

            if(shouldSwipeFix){

            }
            else{

                blurView.setOnTouchListener(null);
                mBlurSpring.setEndValue(25);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCloseBtn.setVisibility(View.VISIBLE);
                        mCloseSpringX.setEndValue(0);
                    }
                },250 );


                hasInHomeScreen = false;

                mCloseSpringS.setCurrentValue(0);
            }


    }



    //################### Setup Adapter Event Listener ###################
    private RVHItemTouchHelperCallback mTouchCallBack;
    private Adapter.onItemEventListener mAdapterEventListener;
    private int removedPos;
    private boolean hasRemoved = false;
    private boolean verfiyLongPressed = false;
    private boolean longPressEnabled = false;
    private int[] longPressPos = new int[2];
    private int clickCount;
    private int mOrignCount;
    private boolean isScaled = true;
    private int[] nowPositionInScreen = new int[2];
    private int nowItemSize;
    private int LongPressIndex,downCardIndex;
    private void setupAdapterEventListener(){
        mAdapterEventListener = new Adapter.onItemEventListener() {
            @Override
            public void onItemSelected(int position) {


                // itemAnimator会先remove，再move，所以速度会影响move

                if(!longPressEnabled){

                    recyclerView.getItemAnimator().setRemoveDuration(200);
                    mTouchCallBack.orginIndex = position;
                    verfiyLongPressed = true;

                }

            }

            @Override
            public void onItemLongPressed(boolean boo,int position) {


                if(boo && verfiyLongPressed){
                    longPressEnabled = true;
                    LongPressIndex = position;


                    if(recyclerView.getLayoutManager().findViewByPosition(position) !=null){

                        recyclerView.getLayoutManager().findViewByPosition(position).getLocationInWindow(longPressPos);

//                        Log.e("P0",String.valueOf(longPressPos[0]));
//                        Log.e("P1",String.valueOf(longPressPos[1]));
                    }

                    int heightPos = (longPressPos[1] + (recyclerView.getLayoutManager().findViewByPosition(position).getHeight() - findViewById(R.id.longpressmenu).getHeight())/2);

                    heightPos = Math.min((screenHeight-76- findViewById(R.id.longpressmenu).getHeight()) ,Math.max(76,heightPos));

                    mStaggeredGridLayoutManager.setScrollEnabled(false);
                    mTouchCallBack.disableSwipe = true;
                    //recyclerView.setOnNoChildClickListener(null);
                    //mAdapter.disableItemEventListener = true;
                    canInHomeScreen = false;
                    mDecor.detach();

                    //findViewById(R.id.blackmask).setAlpha(0.4f);
                    for(int i =0;i < cardList.size();i++){
                        if(i!=position && i!=0 ){
                            if(recyclerView.getLayoutManager().findViewByPosition(i) !=null){
                                recyclerView.getLayoutManager().findViewByPosition(i).setAlpha(0.2f);

                            }
                            if(recyclerView.getLayoutManager().findViewByPosition(0) !=null){
                                recyclerView.getLayoutManager().findViewByPosition(0).setAlpha(0.2f);
                            }
                        }
                        else {
                            if(recyclerView.getLayoutManager().findViewByPosition(i) !=null && i !=0) {
                                LongPressIndex = i;
                                recyclerView.getLayoutManager().findViewByPosition(i).findViewById(R.id.cardcontainer).setAlpha(1.0f);
                                recyclerView.getLayoutManager().findViewByPosition(i).findViewById(R.id.iv_container).setAlpha(0);


                              }
                        }
                    }


                    mLongPressSpringX.setSpringConfig(mMultiAnim);
                    mLongPressSpringY.setSpringConfig(mClickScaleBack);
                    mLongPressSpringT.setSpringConfig(mClickScaleBack);
                    mLongPressSpringX.setEndValue(1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLongPressSpringY.setEndValue(1);
                        }
                    },100 );

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLongPressSpringT.setEndValue(1);
                        }
                    },250 );

                    mLongPressCardSpring.setSpringConfig(mLongPressSpringConfig);
                    mLongPressCardSpring.setEndValue(1);


                    findViewById(R.id.longpressmenu).setVisibility(View.VISIBLE);
                    findViewById(R.id.longpressmenuimg).setPivotY(findViewById(R.id.longpressmenu).getHeight()/2);
                    if(longPressDX > 540){

                        findViewById(R.id.longpressmenuimg).setPivotX(findViewById(R.id.longpressmenu).getWidth());
                        findViewById(R.id.longpressmenu).setTranslationX(540 - 28 -findViewById(R.id.longpressmenu).getWidth());


                    }
                    else{
                        findViewById(R.id.longpressmenuimg).setPivotX(0);
                        findViewById(R.id.longpressmenu).setTranslationX(540 + 22);

                    }


                    if(longPressDY > 1170){
                        //findViewById(R.id.longpressmenuimg).setPivotY(findViewById(R.id.longpressmenu).getHeight());
                        findViewById(R.id.longpressmenu).setTranslationY(heightPos); //longPressDY-findViewById(R.id.longpressmenu).getHeight()

                    }
                    else{
                        //findViewById(R.id.longpressmenuimg).setPivotY(0);
                        findViewById(R.id.longpressmenu).setTranslationY(heightPos);
                    }

                    // Only Run Once
                    verfiyLongPressed =false;
                }
                else {

                }

            }

            @Override
            public void onItemOnDragging(boolean boo) {
                if(boo){
                    isShowPress = false;
                    timerHandler.removeCallbacks(timerRunnable);

                    mLongPressCardSpring.setEndValue(0.4);
                }
                else{

                }

            }

            @Override
            public void onItemRemove(int position,int itemSize) {


                if(position !=0){

                    mLongPressCardSpring.setCurrentValue(0);

                    removedPos = position;
                    hasRemoved = true;
                    nowItemSize = itemSize;


                    Log.e("itemSize",String.valueOf(itemSize));
                    Log.e("padding",String.valueOf(recyclerView.getPaddingTop()));
                    Log.e("padding",String.valueOf(recyclerView.getPaddingBottom()));
                    Log.e("padding",String.valueOf(recyclerView.getPaddingLeft()));
                    Log.e("padding",String.valueOf(recyclerView.getPaddingRight()));

                    if(nowItemSize == 2){


                        ValueAnimator animator = ValueAnimator.ofInt(recyclerView.getPaddingTop(), 401+471);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator){
                                recyclerView.setPadding(22, (Integer) valueAnimator.getAnimatedValue(), 22, 264);
                            }
                        });
                        animator.setDuration(250);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator() );
                        animator.start();


                        rvPosFix = 401;
                        //recyclerView.animate().translationY(501).setDuration(250).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    }
                    if(nowItemSize == 1){
                        rvPosFix = 660;
//                        recyclerView.animate().translationY(800).setDuration(250).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                        ValueAnimator animator = ValueAnimator.ofInt(recyclerView.getPaddingTop(), 660+471);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator){
                                recyclerView.setPadding(22, (Integer) valueAnimator.getAnimatedValue(), 22, 264);
                            }
                        });
                        animator.setDuration(250);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator() );
                        animator.start();
                        //findViewById(R.id.tinttext).animate().alpha(1).setDuration(250).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    }



                }

            }

            @Override
            public void onItemSwap(int firstPostion,int secondPosition) {}

                @Override
            public void onItemUnselected(int position) {
                    //unLongPressEffect();

                    if(!longPressEnabled){

                        verfiyLongPressed = false;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(mLongPressCardSpring.getCurrentValue() !=0){
                                    mLongPressCardSpring.setEndValue(0);
                                }

                                longPressEnabled = false;

                            }
                        },20 );
                    }

            }



            @Override
            public void onItemClick(View v, Card item,int position,int orginPos) {

                if(longPressEnabled){
                    unLongPressEffect();

                }

                if(position != 0 && !longPressEnabled){


                    if(isScaled && mAdapter.CardList.size()>1){

                        if (recyclerView.getLayoutManager().findViewByPosition(position) !=null){


                            recyclerView.getLayoutManager().findViewByPosition(position).getLocationInWindow(nowPositionInScreen);

                            Log.e("now X",String.valueOf(nowPositionInScreen[0]));
                            Log.e("now Y",String.valueOf(nowPositionInScreen[1]));
                            clickCount = position;
                            mOrignCount = orginPos;


                            Log.e("RVY",String.valueOf(recyclerView.getTranslationY()));

                            if(orginPos == 1){
                                mStatsBar.setImageResource(R.drawable.statswhite);
                            }
                            else{
                                mStatsBar.setImageResource(R.drawable.statsdark);
                            }


//                            if(position == 0){
//                                mLayoutUpSpring.setEndValue(1);
//                            }

                            mSpringX.setSpringConfig(mconfigXGo);
                            mSpringS.setSpringConfig(mconfigSGo);
                            mSpringX.setEndValue(1);
                            mSpringS.setEndValue(1);
                            mCloseSpringX.setEndValue(1);
                            mAttachSpringS.setSpringConfig(mconfig2SBack);

                            //## Question Here
                            mAttachSpringS.setEndValue(1);
                            mAttachSpringA.setEndValue(1);
                            //mMaskSpringA.setEndValue(0.45f);
                            mSwipeUpSpringScale.setCurrentValue(0);
                            mGestureSpringX.setCurrentValue(0);
                            mGestureSpringY.setCurrentValue(0);


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mStatsSpring.setSpringConfig(mStatsSlow);
                                    mStatsSpring.setEndValue(1);

                                }
                            },200 );


                            recyclerView.getLayoutManager().findViewByPosition(position).setTranslationZ(10000);
                            recyclerView.getLayoutManager().findViewByPosition(position).findViewById(R.id.tv_title).animate().alpha(0).setDuration(50).setInterpolator(new DecelerateInterpolator()).start();
                            recyclerView.getLayoutManager().findViewByPosition(position).findViewById(R.id.iv_icon).animate().alpha(0).setDuration(50).setInterpolator(new DecelerateInterpolator()).start();
                            eventOnOpen();
                            isScaled = !isScaled;
                        }



                    }
                    else {

                    }

                }
            }
        };
    }

    //################### UnLongPress Effect ###################

    private void unLongPressEffect(){
        mStaggeredGridLayoutManager.setScrollEnabled(true);
        mTouchCallBack.disableSwipe = false;
        setRVOverScroll();
        recyclerView.setOnNoChildClickListener(noChildClickListener);
        mAdapter.disableItemEventListener = false;
        canInHomeScreen = true;

        //findViewById(R.id.blackmask).setAlpha(0.f);


        mLongPressSpringT.setSpringConfig(mStatsRapid);
        mLongPressSpringT.setEndValue(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLongPressSpringY.setSpringConfig(mMultiAnim);
                mLongPressSpringY.setEndValue(0);
            }
        },150 );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                mLongPressSpringX.setEndValue(0);

            }
        },150 );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                findViewById(R.id.longpressmenu).setVisibility(View.INVISIBLE);



                for(int i =0;i < cardList.size();i++){
                    if(i!=LongPressIndex) {
                        if (recyclerView.getLayoutManager().findViewByPosition(i) != null) {
                            recyclerView.getLayoutManager().findViewByPosition(i).setAlpha(1.0f);

                        }
                    }
                    else {
                        if (recyclerView.getLayoutManager().findViewByPosition(i) != null) {

                            isShowPress = false;
                            timerHandler.removeCallbacks(timerRunnable);
                            mLongPressCardSpring.setSpringConfig(mLongPressConfig);
                            mLongPressCardSpring.setEndValue(0);

                            recyclerView.getLayoutManager().findViewByPosition(i).findViewById(R.id.iv_container).setAlpha(1.0f);
                        }
                    }
                }


                verfiyLongPressed = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(mLongPressCardSpring.getCurrentValue() !=0){
                            mLongPressCardSpring.setSpringConfig(mLongPressConfig);
                            mLongPressCardSpring.setEndValue(0);
                        }

                        longPressEnabled = false;

                    }
                },20 );

            }
        },275 );




    }

    //################### Setup RecyclerView###################
    private boolean mInitialized = false;
    private float longPressDX,longPressDY;

    List<Card> cardList = new ArrayList<>();
    private TouchRecyclerView recyclerView;
    private Adapter mAdapter;
    private String[] mName= {"Application","Application","Application","Application","Application","Application"};
    private int[] mImg= {R.drawable.image0,R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image5};
    private int[] mIcon= {R.drawable.icon0,R.drawable.icon1,R.drawable.icon2,R.drawable.icon3,R.drawable.icon4,R.drawable.icon5};
    private int[] mSrc= {R.drawable.as0,R.drawable.as1,R.drawable.as2,R.drawable.as3,R.drawable.as4,R.drawable.as5};
    private int scrollValue;
    private SmoothScrollStaggerGridLayoutManager mStaggeredGridLayoutManager;
    private boolean isShowPress = false;

    private void setupRecyclerView() {
        makeList();

        hasRemoved = false;

        recyclerView = (TouchRecyclerView) findViewById(R.id.recycler_view);
        mStaggeredGridLayoutManager = new  SmoothScrollStaggerGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredGridLayoutManager );
        mStaggeredGridLayoutManager.isItemPrefetchEnabled();
        recyclerView.setAlpha(1.0f);
        recyclerView.setTranslationY(0);
        recyclerView.setPadding(22, 471, 22,264);
        mCloseBtn.setImageResource(R.drawable.closebtn);

        mLongPressSpringX.setCurrentValue(0);
        mLongPressSpringY.setCurrentValue(0);
        mLongPressSpringT.setCurrentValue(0);

        scrollValue = 0;
        rvPosFix = 0;


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollValue= scrollValue + dy;
                //Log.e("IfRemoved",String.valueOf(recyclerView.computeVerticalScrollRange()));


                // TODO : Change this Strange Function
                int i = mAdapter.CardList.size() - 1;
                if(recyclerView.getLayoutManager().findViewByPosition(i) != null && recyclerView.getLayoutManager().findViewByPosition(i).getAlpha() !=1 ){
                    recyclerView.getLayoutManager().findViewByPosition(i).setAlpha(1);
                }

                if(recyclerView.getLayoutManager().findViewByPosition(i-1) != null && recyclerView.getLayoutManager().findViewByPosition(i-1).getAlpha() !=1 ){
                    recyclerView.getLayoutManager().findViewByPosition(i-1).setAlpha(1);
                }

                //Log.i("check","scroll is->" + scrollValue);


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                Log.e("Changed",String.valueOf(newState));

                if(newState ==0){

                    //mScrollIsStop = true;
                    Log.e("Scroll","Stoped");

                    timerHandler.removeCallbacks(timerRunnable);

                }
                else {
                    //mScrollIsStop = false;
                    isShowPress = false;
                    //Not Remove for Recover
                    //timerHandler.removeCallbacks(timerRunnable);
                    Log.e("ShowPress","FailedStop");
                }

            }
        });

        setRVOverScroll();


        if (!mInitialized) {
            setupAdapterEventListener();
        }
        mAdapter = new Adapter(cardList, recyclerView, mAdapterEventListener);
        mAdapter.notifyData(cardList);
        recyclerView.setAdapter(mAdapter);

        if (!mInitialized) {
            recyclerView.setHapticFeedbackEnabled(false);

            mTouchCallBack = new RVHItemTouchHelperCallback(mAdapter, true, true, true);
            mTouchCallBack.referenceRV = recyclerView;
            //mTouchCallBack.disableSwipe = true;
            ItemTouchHelper helper = new ItemTouchHelper(mTouchCallBack);
            helper.attachToRecyclerView(recyclerView);

            recyclerView.addOnItemTouchListener(new RVHItemClickListener(this, new RVHItemClickListener.OnItemClickListener() {
                @Override
                public void onItemTouchUp(View view, int position,float dX,float dY){

                }
                @Override
                public void onItemShowPress(View view, int position,float dX,float dY){
                }
                @Override
                public void onOutsideTouch(float dX,float dY){}
                @Override
                public void onItemTouchMove(View view, int position,float dX,float dY){}
                @Override
                public void onItemTouchDown(View view, final int position, float dX, float dY){}
                @Override
                public void detectShowPress(boolean boo,int position){

                    if(position != 0 && !longPressEnabled){
                        downCardIndex = position;
                        Log.e("ShowPress",String.valueOf(downCardIndex));
                        if(boo){
                            isShowPress = true;
                            Log.e("ShowPress","ShowPressing");
                            startTime = System.currentTimeMillis();
                            timerHandler.postDelayed(timerRunnable, 0);
                        }
                        else{
                            Log.e("ShowPress","Failed");
                            isShowPress = false;
                            timerHandler.removeCallbacks(timerRunnable);
                        }
                    }


                }
                @Override
                public void onItemLongPress(View view,int position,float dX,float dY){
                    longPressDX = dX;
                    longPressDY = dY;

                    isShowPress = false;
                    //这里 Remove 让卡片得以扩大
                    timerHandler.removeCallbacks(timerRunnable);
                }
            }));
        }

        //IMP 一次行记载所有 Cell 当paddingFix改变，这里也要改变，以便刷出所有Cell
        recyclerView.scrollBy(0,600);
        recyclerView.smoothScrollBy(0,-600,new AnimUtil.CustomJellyInterpolator());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                initItemEvent();

            }
        },300 );

        mInitialized = true;


        eventAfterClose();
        //eventAfterClose();

    }

    private IOverScrollDecor mDecor;

    private void setRVOverScroll(){
        mDecor = OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        IOverScrollUpdateListener mOSUpdateListener = new IOverScrollUpdateListener() {
            int translationThreshold = 100;
            int ON_OVER_SCROLL = 1;
            int RELEASE = 3;
            @Override
            public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {

                if(state == RELEASE && Math.abs(offset)>translationThreshold){
                    decor.setOverScrollUpdateListener(null);
                    //outTransition();

                    if(offset>0){
                        outTransitionTop(false);
                    }
                    else{
                        //outTransitionTop(true);
                    }

                }
            }
        };

        mDecor.setOverScrollUpdateListener(mOSUpdateListener);

    }

    private void makeList(){


        for (int i = 0; i <6 ; i++) {
            int count = i;
//            cardList.add(new Card(mName[i],mImg[i],mIcon[i],i));

            if(i == 0){

                cardList.add(new Card(null,mImg[0],0,0));

            }
            else{
                cardList.add(new Card(mName[i],mImg[i],mIcon[i],i));
            }

        }

    }


    //################### Time Handler ###################
    Handler timerHandler = new Handler();
    long startTime = 0;
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            float msTime = (float) millis/1000;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;


            if(isShowPress){

                //Log.e("Time",String.valueOf(millis));
                mLongPressCardSpring.setEndValue(msTime);
            }
            else{
                mLongPressCardSpring.setEndValue(0);

            }

            timerHandler.postDelayed(this, 16);
        }
    };



    //################### Setup Drag GestureDector ###################
    private MultiGestureDetector mMultiGestureDetector;
    private float startX,startY,mDistanceY;

    private int screenHeight,screenWidth;
    private int bottomScrollHeight = 200;
    private float decidedHeight = 2064;

    private DisplayMetrics dm;

    private final float finalScale = 151/1170;
    private final float limitedRange = 0.28f;
    private final float limitScale = 0.52f;
    private float absDistProgressY = 0;
    private int multiAreaHeight = 1000;
    private long downTime, upTime;
    private float mPrevY,mPrevProgressY,mPrevX;
    private float nowScrollY;
    private  boolean hasTriggered;
    private boolean hasVibrate = false;
    private boolean hasClickedZero = false;
    private int prevClickCount;


    private void setDragListener() {

        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        if (dm.heightPixels >= 2060) {
            screenHeight = 2340;
        } else {
            screenHeight = dm.heightPixels;
            screenWidth = dm.widthPixels;
        }

        mMultiGestureDetector = new MultiGestureDetector(new MultiGestureDetector.SimpleGestureListener() {


            @Override
            public void onDown(MotionEvent event){
                downTime = System.currentTimeMillis();
                startX = event.getX();
                startY = event.getY();
                mPrevX = 0;


                prevClickCount = clickCount;
                recyclerView.scrollToPosition(0);
                scrollValue = 0;


                if(clickCount == 0){
                }
                else{


                    mAdapter.onItemMove(clickCount,1);
                    clickCount = 1;


                }


                recyclerView.setAlpha(0.0f);


            }


            @Override
            public void onVelocityStop(boolean boo){
            }


            @Override
            public void onTriggerJudge(boolean boo) {

                //Log.e("nowscrolly", String.valueOf(nowScrollY));

                if (boo == true && nowScrollY < screenHeight - bottomScrollHeight && !isScaled) {

                    if(!hasVibrate){

                        VibratorUtil.Vibrate(getApplicationContext(),20);
                        hasVibrate = true;


                        hasTriggered = true;
                        mAttachSpringS.setSpringConfig(mconfig2SBack);

                        mAttachSpringA.setEndValue(0.8);
                        mBlurSpring.setEndValue(25);
                        mIconSpring.setEndValue(1);
                        //mLayoutUpSpring.setCurrentValue(0);
                        recyclerView.setAlpha(1.0f);

                        recyclerView.scrollToPosition(0);
                        scrollValue = 0;


                        mAttachSpringS.setCurrentValue(1);
                        mAttachSpringS.setEndValue(0);
                    }


                }
                else{
                }
            }
            @Override
            public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float velocityX, float velocityY) {


                // Scroll Up Condition
                if (!isScaled && startY > screenHeight - bottomScrollHeight) {



                    mDistanceY = startY - e2.getY();
                    float PointStart = startY;
                    float PointEnd = (float) 0.5 * decidedHeight + (float) (startY / decidedHeight - 0.5) * finalScale * decidedHeight;
                    float totalDistanceY = PointStart - PointEnd;

                    absDistProgressY = Math.max(0, Math.min(1.0f, mDistanceY / totalDistanceY));
                    float finalProgress = Math.min(limitedRange, absDistProgressY);
                    mSwipeUpSpringScale.setSpringConfig(mMultiAnim);
                    mSwipeUpSpringScale.setEndValue(finalProgress); ///(limitedRange/0.46)

                    nowScaleX =  1 - (1 - finalScale) * finalProgress;; //1 - (1 - finalScale) * finalProgress
                    nowScrollY = e2.getY();

                    if(!hasTriggered){
                        mBlurSpring.setEndValue(absDistProgressY*5.0f);
                        mIconSpring.setEndValue(absDistProgressY*5.0f/25);
                        //Log.e("the Value is ",String.valueOf(absDistProgressY*5f));

                        mPrevProgressY = absDistProgressY*5.0f;
                    }
                    else{
                        mBlurSpring.setEndValue(25);
                        mIconSpring.setEndValue(1);
                    }
                    //Log.e("1", String.valueOf(e2.getY()));

                    //Spring Driven
                    if(e2.getY()>PointEnd + (1 - limitedRange) * (startY - PointEnd) - multiAreaHeight) {
                        //黄线区域内
                        if(e2.getY()<PointEnd + (1 - limitedRange) * (startY - PointEnd)){

                            if(e2.getY()>1450){

                                mGestureSpringY.setEndValue(totalDistanceY * Math.min(limitScale,absDistProgressY) - mDistanceY);

                                mPrevY = totalDistanceY * Math.min(limitScale,absDistProgressY) - (float)mDistanceY;
                            }
                            else{


                                float distanceProgressY = (1450-e2.getY())/400.0f;

                                float expDistProgressY = (float) Math.max(0, Math.min(1, Math.exp(1 / (-distanceProgressY / (1. + distanceProgressY) - 0.226))));

                                mGestureSpringY.setEndValue(mPrevY - (1400-e2.getY())*expDistProgressY);

                            }

                            mSwipeUpSpringScale.setEndValue(Math.min(limitScale,absDistProgressY));
                            nowScaleX = 1 - (1 - finalScale) * Math.min(limitScale,absDistProgressY); //1 - (1 - finalScale) * finalProgress
                            //Log.e("1", "下");
                        }
                        //黄线区域下
                        else if (e2.getY() > PointEnd + (1 - limitedRange) * (startY - PointEnd)){
                            mGestureSpringY.setEndValue(0);
                            //Log.e("1", "Scale");
                        }
                    }
                    //黄线区域上
                    else{
                        float distanceProgressY = (1450-e2.getY())/400.0f;

                        float expDistProgressY = (float) Math.max(0, Math.min(1, Math.exp(1 / (-distanceProgressY / (1. + distanceProgressY) - 0.226))));

                        mGestureSpringY.setEndValue(mPrevY - (1450-e2.getY())*expDistProgressY);

                        mSwipeUpSpringScale.setEndValue(Math.min(limitScale,absDistProgressY));
                        nowScaleX = 1 - (1 - finalScale) * Math.min(limitScale,absDistProgressY);
                        //Log.e("1", "上");
                    }


                    //Log.e("XValue",String.valueOf(e2.getX()));

                    float origTransX = distanceX - (1 - nowScaleX) * (1170 / 2 - (startX));
                    float lJudgeLine = 640*nowScaleX; //540*nowScaleX 440
                    float rJudgeLine = 1170 -  640*nowScaleX; //1080 - 540*nowScaleX
                    float nowCenterX = distanceX - (1 - nowScaleX) * (1170 / 2 - (startX)) + 1170/2 ;


                    if(lJudgeLine > rJudgeLine){


                        mGestureSpringX.setEndValue(origTransX);
                        mPrevX = origTransX;
                    }

                    else{


                        if(nowCenterX<lJudgeLine){

                            float distanceProgressX = (lJudgeLine-nowCenterX)/400.0f;

                            float expDistProgressX = (float) Math.max(0, Math.min(1, Math.exp(1 / (-distanceProgressX / (1. + distanceProgressX) - 0.226))));
                            mGestureSpringX.setEndValue(mPrevX - (lJudgeLine-nowCenterX)*expDistProgressX);

                        }
                        else if (nowCenterX > rJudgeLine){

                            float distanceProgressX = (nowCenterX-rJudgeLine)/400.0f;

                            float expDistProgressX = (float) Math.max(0, Math.min(1, Math.exp(1 / (-distanceProgressX / (1. + distanceProgressX) - 0.226))));
                            mGestureSpringX.setEndValue(mPrevX + (nowCenterX-rJudgeLine)*expDistProgressX);
                        }
                        else{

                            mGestureSpringX.setEndValue(origTransX);
                            mPrevX = origTransX;
                        }

                    }



                    mStatsSpring.setSpringConfig(mStatsRapid);
                    mStatsSpring.setEndValue(0);

                    // Scope Function
//                    findViewById(R.id.xguideline).setTranslationX(nowCenterX);
//                    findViewById(R.id.guidelinel).setTranslationX(lJudgeLine);
//                    findViewById(R.id.guideliner).setTranslationX(rJudgeLine);
//                    float nowLeftX = distanceX - (1 - nowScaleX) * (1080 / 2 - (startX)) + 1080/2 *(1.f-nowScaleX);
//                    float nowRightX = distanceX - (1 - nowScaleX) * (1080 / 2 - (startX)) + 1080/2 *(1.f-nowScaleX) + 1080*nowScaleX ;
//                    findViewById(R.id.xguidelinel).setTranslationX(nowLeftX);
//                    findViewById(R.id.xguideliner).setTranslationX(nowRightX);




                }

            }

            @Override
            public void onLongPress(MotionEvent event) {

            }

            @Override
            public void onSwipeTop(float velocity) {


                if (startY > screenHeight - bottomScrollHeight && !isScaled) {

                    eventOnClose();
                    canInHomeScreen = true;
                    recyclerView.scrollToPosition(0);
                    scrollValue = 0;

                    mGestureSpringX.setSpringConfig(mconfigXBack);
                    mGestureSpringY.setSpringConfig(mconfigXBack);

                    // Fake way of Tranisition
                    mGestureSpringX.setEndValue(0);
                    mGestureSpringY.setEndValue(0);


                    mSwipeUpSpringScale.setSpringConfig(mSwipeScaleGo);
                    mSwipeUpSpringScale.setEndValue(1 - 1/2.5);
                    mLayoutSpringA.setEndValue(0);

                    if(!hasVibrate){

                        VibratorUtil.Vibrate(getApplicationContext(),20);
                        hasVibrate = true;
                    }

                    mSpringX.setSpringConfig(mconfigXBack);
                    mSpringS.setSpringConfig(mconfigSBack);
                    mSpringX.setEndValue(0);
                    mSpringS.setEndValue(0);
                    mAttachSpringS.setSpringConfig(mconfig2SBack);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //recyclerView.setAlpha(1.0f);

                        }
                    },0 );


                    outTransition();

                    mAttachSpringS.setEndValue(0);
                    mAttachSpringA.setEndValue(0);
                    //mMaskSpringA.setEndValue(0);
                    //mBlurSpring.setEndValue(25);
                    isScaled = !isScaled;
                    nowScrollY = screenHeight;
                    hasTriggered = false;
                    Log.e(TAG, "Up Close 2");


                    mStatsSpring.setSpringConfig(mStatsRapid);
                    mStatsSpring.setEndValue(0);

                }

            }

            @Override
            public void onSwipeTopFix() {

                if(!isScaled){

                    mSwipeUpSpringScale.setSpringConfig(mSwipeScaleGo);
                    mSwipeUpSpringScale.setEndValue(0);
                    hasVibrate = false;
                    mGestureSpringX.setEndValue(0);
                    mGestureSpringY.setEndValue(0);
                    mBlurSpring.setEndValue(0);
                    mIconSpring.setEndValue(0);
                    mAttachSpringS.setEndValue(1);
                    mAttachSpringA.setEndValue(1);
                    mSpringX.setSpringConfig(mconfigXBack);
                    mSpringS.setSpringConfig(mconfigSBack);
                    mSpringX.setEndValue(1);
                    mSpringS.setEndValue(1);
                    mCloseSpringX.setEndValue(1);
                    nowScaleY = screenHeight;
                    hasTriggered = false;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mStatsSpring.setSpringConfig(mStatsSlow);
                            mStatsSpring.setEndValue(1);

                        }
                    },200 );
                    Log.e(TAG, "Error Open 1");
                }
            }


            @Override
            public void onUp(MotionEvent event) {
                upTime = System.currentTimeMillis();

                //Click Event
                if (upTime - downTime <= 200) {

                    mSwipeUpSpringScale.setSpringConfig(mSwipeScaleGo);
                    mSwipeUpSpringScale.setEndValue(0);
                    hasVibrate = false;
                    mGestureSpringX.setEndValue(0);
                    mGestureSpringY.setEndValue(0);
                    Log.e(TAG, "Error Close 3");
                }

                //Up Event
                else {
                    // 1.Scroll Up Do not Triggered;
                    if (event.getY() > screenHeight - bottomScrollHeight && !isScaled && startY > screenHeight - bottomScrollHeight) {
                        mSwipeUpSpringScale.setSpringConfig(mSwipeScaleGo);
                        mSwipeUpSpringScale.setEndValue(0);
                        hasVibrate = false;
                        mGestureSpringX.setEndValue(0);
                        mGestureSpringY.setEndValue(0);
                        mAttachSpringS.setEndValue(1);
                        mAttachSpringA.setEndValue(1);
                        mSpringX.setSpringConfig(mconfigXBack);
                        mSpringS.setSpringConfig(mconfigSBack);
                        mSpringX.setEndValue(1);
                        mSpringS.setEndValue(1);
                        mCloseSpringX.setEndValue(1);
                        nowScrollY = screenHeight;
                        hasTriggered = false;
                        mBlurSpring.setEndValue(0);
                        mIconSpring.setEndValue(0);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                mStatsSpring.setSpringConfig(mStatsSlow);
                                mStatsSpring.setEndValue(1);

                            }
                        },200 );
                        Log.e(TAG, "Error Open 2");
//

                    }
                    //Drag Triggered HomeScreen
                    else if (event.getY() < screenHeight - bottomScrollHeight && !isScaled && startY > screenHeight - bottomScrollHeight) {

                        eventOnClose();
                        recyclerView.scrollToPosition(0);
                        scrollValue = 0;


                        if(hasTriggered){

                        }
                        else{

                            mAttachSpringS.setCurrentValue(1);

                            mAttachSpringS.setSpringConfig(mconfig2SBack);
                            mAttachSpringS.setEndValue(0);
                        }

                        mGestureSpringX.setSpringConfig(mconfigXBack);
                        mGestureSpringY.setSpringConfig(mconfigXBack);

                        // Fake way of Tranisition
                        mGestureSpringX.setEndValue(258); //-258-4
                        mGestureSpringY.setEndValue( -612 + paddingFix + 66);

                        mSwipeUpSpringScale.setSpringConfig(mSwipeScaleGo);
                        mSwipeUpSpringScale.setEndValue(1 - 0.4);

                        if(!hasVibrate){

                            VibratorUtil.Vibrate(getApplicationContext(),20);
                            hasVibrate = true;
                        }

                        mSpringX.setSpringConfig(mconfigXBack);
                        mSpringS.setSpringConfig(mconfigSBack);
                        mSpringX.setEndValue(0);
                        mSpringS.setEndValue(0);
                        mCloseSpringX.setEndValue(0);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                recyclerView.setAlpha(1.0f);

                            }
                        },0 );

                        mAttachSpringA.setEndValue(0);
                        //mMaskSpringA.setEndValue(0);
                        mBlurSpring.setEndValue(25);
                        mIconSpring.setEndValue(1);
                        isScaled = !isScaled;
                        nowScrollY = screenHeight;
                        hasTriggered = false;
                        Log.e(TAG, "Up Close 2");


                        mStatsSpring.setSpringConfig(mStatsRapid);
                        mStatsSpring.setEndValue(0);

                    }
                    else {

                        //Log.e(TAG,"HERE");

                    }

                }
            }

        });



    }

    //################### Event Controller ###################
    //点击瞬间禁止掉 Item | Remove，完成点击动画后开始 gesture ，关闭瞬间关闭gesture 完成关闭动画后开启 item | remove
    private void eventOnOpen(){

        //mLongPressSpring.setEndValue(0);
        mLongPressCardSpring.setSpringConfig(mLongPressConfig);
        mLongPressCardSpring.setEndValue(0);
        mStaggeredGridLayoutManager.setScrollEnabled(false);
        mTouchCallBack.disableSwipe = true;
        mCloseBtn.setOnClickListener(null);
        recyclerView.setOnNoChildClickListener(null);
        //recyclerView.setOnScrollUpCancelListener(null);
        mAdapter.disableItemEventListener = true;
        canInHomeScreen = false;
        recyclerView.getItemAnimator().setMoveDuration(0);
//        mLayoutDownSpring.setCurrentValue(0);
    }

    private void eventAfterOpen(){
        findViewById(R.id.gestureContainer).setOnTouchListener(mMultiGestureDetector);
        hasVibrate = false;
    }

    private void eventOnClose(){

        findViewById(R.id.gestureContainer).setOnTouchListener(null);
    }

    private void eventAfterClose(){
        hasVibrate = false;
        mStaggeredGridLayoutManager.setScrollEnabled(true);
        mTouchCallBack.disableSwipe = false;
        mCloseBtn.setOnClickListener(closeBtnListener);
        recyclerView.setOnNoChildClickListener(noChildClickListener);
        //recyclerView.setOnScrollUpCancelListener(scrollUpCancelListener);
        mAdapter.disableItemEventListener = false;
        canInHomeScreen = true;
        recyclerView.getItemAnimator().setMoveDuration(250);
    }

    private void initItemEvent(){
        // Origin is gestureView
        mAdapter.disableItemEventListener = false;
    }

    //################### Utils Init ###################
    public void onBackPressed(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void deleteBars(){
        //Delete Title Bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {
            if(mSpringConfiguratorView.getVisibility() == View.VISIBLE){
                mSpringConfiguratorView.setVisibility(View.INVISIBLE);
            }
            else{
                mSpringConfiguratorView.setVisibility(View.VISIBLE);
            }

        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ) {
            //mAdapter.disableItemEventListener = !mAdapter.disableItemEventListener;
        }
        return super.onKeyDown(keyCode, event);
    }

    private SpringConfiguratorView mSpringConfiguratorView;
    private void setSpringConfig(){
        mSpringConfiguratorView = (SpringConfiguratorView) findViewById(R.id.spring_configurator);


        SpringConfigRegistry.getInstance().removeAllSpringConfig();
        SpringConfigRegistry.getInstance().addSpringConfig(mconfigXGo, "打开卡片位移");
        SpringConfigRegistry.getInstance().addSpringConfig(mconfigSGo, "打开卡片缩放");
        SpringConfigRegistry.getInstance().addSpringConfig(mconfig2XGo, "打开背景位移");
        SpringConfigRegistry.getInstance().addSpringConfig(mconfig2SGo, "打开背景缩放");
        SpringConfigRegistry.getInstance().addSpringConfig(mconfigXBack, "关闭卡片位移");
        SpringConfigRegistry.getInstance().addSpringConfig(mconfigSBack, "关闭卡片缩放");
        SpringConfigRegistry.getInstance().addSpringConfig(mconfig2XBack, "关闭背景位移动");
        SpringConfigRegistry.getInstance().addSpringConfig(mconfig2SBack, "关闭背景缩放");
        mSpringConfiguratorView.refreshSpringConfigurations();
        mSpringConfiguratorView.bringToFront();
    }


}
