package com.chedifier.cleaner.base;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/8/18.
 */

public class ScreenUtils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int[] getScreenSize(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        return new int[]{width,height};
    }

}
