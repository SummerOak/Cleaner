package com.chedifier.cleaner.cleaner;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */

public class TargetTaskFetcher {

    private static final String TAG = "TargetTaskFetcher";

    private static final boolean DUMP_INFO = false;

    private static final List<String> WHITE_LIST = new ArrayList<>();
    static {
        WHITE_LIST.add("android");
        WHITE_LIST.add("com.google.android.gsf");
        WHITE_LIST.add("com.google.android.gsf.login");
        WHITE_LIST.add("com.android.systemui");
        WHITE_LIST.add("com.google.android.packageinstaller");
        WHITE_LIST.add("com.android.settings");
    }

    public static List<String> getPackagesCanbeStop(Context context){
        return getFromInstalledPackages(context,WHITE_LIST);
//        return getRecentRunningPackages(context,WHITE_LIST);
    }

    private static List<String> getRecentRunningPackages(Context context, List<String> excepts) {
        List<String> packages = new ArrayList<>();
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                UsageStatsManager usm = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
                PackageManager pm = context.getPackageManager();
                long time = System.currentTimeMillis();
                List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST,  time - 24*60*60*1000, time);
                Log.i(TAG," " + appList.size());
                if (appList != null && appList.size() > 0) {
                    for (UsageStats ust : appList) {

                        if(DUMP_INFO){
                            ApplicationInfo ai = pm.getApplicationInfo(ust.getPackageName(),PackageManager.GET_META_DATA);
                            boolean system = (ai.flags&ai.FLAG_SYSTEM) > 0;
                            boolean stoped = (ai.flags&ai.FLAG_STOPPED) > 0;
                            boolean persisited = (ai.flags&ai.FLAG_PERSISTENT) > 0;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String s =format.format(new Date(ust.getFirstTimeStamp()));
                            String t =format.format(new Date(ust.getLastTimeStamp()));
                            Log.i("app_usage: ",  String.format("%80s system = %-6b stop = %-6b persisted = %-6b ",ust.getPackageName(),system,stoped,persisited) + " " + s + " " + t);
                        }

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



    private static List<String> getFromInstalledPackages(Context context, List<String> excepts){
        List<String> packages = new ArrayList<>();
        List<PackageInfo> apps = context.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<apps.size();i++) {
            PackageInfo p = apps.get(i);

            ApplicationInfo ai = p.applicationInfo;
            boolean system = (ai.flags&ai.FLAG_SYSTEM) > 0;
            boolean stoped = (ai.flags&ai.FLAG_STOPPED) > 0;
            boolean persisited = (ai.flags&ai.FLAG_PERSISTENT) > 0;
            if(DUMP_INFO){
                Log.i("app_usage: ",  String.format("%80s system = %-6b stop = %-6b persisted = %-6b ",ai.packageName,system,stoped,persisited));
            }

            if(stoped){
                continue;
            }

            if(system && persisited){
                continue;
            }

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
