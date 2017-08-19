package com.chedifier.cleaner.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import java.util.List;


/**
 * Created by Administrator on 2017/8/19.
 */

public class SystemUtils {

    private static final String TAG = "SystemUtils";

    public static ActivityManager.MemoryInfo getMemoryInfo(Context context){

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        return mi;
    }

    public static void backToHome(Context context){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(intent);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public static List<String> getRunningPackage(){
        String[] args = {"dumpsys"};
        String result = CommandUtils.runCommand(args, null);

        Log.i(TAG,"getRunning packages: " + result);

        return null;
    }

    public static void killProcess(){
        System.exit(0);
        Process.killProcess(Process.myPid());
    }

}
