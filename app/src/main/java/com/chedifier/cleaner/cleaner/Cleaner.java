package com.chedifier.cleaner.cleaner;

import com.chedifier.cleaner.base.BaseActivity;

/**
 * Created by Administrator on 2017/8/17.
 */

public class Cleaner {

    private static final String TAG = "Cleaner";

    public static void start(BaseActivity context){
        new StepCheckAccessPermission(context)
                .setNext(new StepCheckOverlayPermission(context)
                .setNext(new StepCheckReadProcPermission(context)
                .setNext(new StepClean(context))))
                .doAction();
    }

}
