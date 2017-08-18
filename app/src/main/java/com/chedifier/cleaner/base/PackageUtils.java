package com.chedifier.cleaner.base;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */

public class PackageUtils {

    private static final String TAG = "PackageUtils";

    public static List<String> getAllRunningPackages(Context context,List<String> excepts) {
        List<String> packages = new ArrayList<>();
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                UsageStatsManager usm = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 60*1000, time);
                Log.i(TAG," " + appList.size());
                if (appList != null && appList.size() > 0) {
                    for (UsageStats ust : appList) {
                        if(excepts != null && excepts.contains(ust.getPackageName())){
                            continue;
                        }
                        packages.add(ust.getPackageName());
                    }
                }
            }catch (Throwable t){
                t.printStackTrace();
            }
        } else {
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo t:tasks){
                if(excepts != null && excepts.contains(t.processName)){
                    continue;
                }

                packages.add(t.processName);
            }
        }

        return packages;
    }



    public static List<String> getAllInstalledPackages(Context context,List<String> excepts){
        List<String> packages = new ArrayList<>();
        List<PackageInfo> apps = context.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<apps.size();i++) {
            PackageInfo p = apps.get(i);

            if(excepts != null && excepts.contains(p.packageName)){
                continue;
            }

            if(context.getPackageName().equals(p.packageName)){
                continue;
            }

            packages.add(p.packageName);
        }

        return packages;
    }


}
