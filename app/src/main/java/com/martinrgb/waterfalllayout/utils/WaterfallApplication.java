package com.martinrgb.waterfalllayout.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanary;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


/**
 * Created by lcodecore on 2016/12/4.
 */

public class WaterfallApplication extends Application {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        // Normal app init code...

//        Takt.stock(this)
//                .hide()
//                .listener(new Audience() {
//                    @Override public void heartbeat(double fps) {
//                        Log.d("Excellent!", fps + " fps");
//                    }
//                })
//                .play();


        setupLeakCanary();
    }

    protected RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    @Override public void onTerminate() {
        //Takt.finish();
        super.onTerminate();
    }

}
