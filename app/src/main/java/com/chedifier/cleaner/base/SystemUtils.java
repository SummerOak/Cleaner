package com.chedifier.cleaner.base;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Administrator on 2017/8/19.
 */

public class SystemUtils {

    public static ActivityManager.MemoryInfo getMemoryInfo(Context context){

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        return mi;
    }

}
