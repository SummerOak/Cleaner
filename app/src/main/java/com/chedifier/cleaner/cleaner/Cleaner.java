package com.chedifier.cleaner.cleaner;

import android.content.Context;
import android.content.Intent;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.steps.Step;

/**
 * Created by Administrator on 2017/8/17.
 */

public class Cleaner {

    private static final String TAG = "Cleaner";

    private static Step sHead;

    public static void start(final BaseActivity context){
        sHead = new StepCheckAccessPermission(context)
                .setNext(new StepClean(context));

        sHead.doAction();
    }

    public static void stop(Context context){
        try{
            Intent i = new Intent(context,CleanMasterAccessbilityService.class);
            i.setAction(CleanMasterAccessbilityService.ACT_STOP);
            context.startService(i);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    protected static void exit(Context context){
        try{
            Intent i = new Intent(context,CleanMasterAccessbilityService.class);
            i.setAction(CleanMasterAccessbilityService.ACT_EXIT);
            context.startService(i);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

}
