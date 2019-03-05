package com.martinrgb.waterfalllayout.utils;

import android.widget.Toast;


/**
 * Created by lcodecore on 2017/2/28.
 */

public class ToastUtil {
    public static void show(String msg){
        Toast.makeText(WaterfallApplication.appContext, msg, Toast.LENGTH_SHORT).show();
    }
}
