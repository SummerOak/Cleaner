package com.chedifier.cleaner.cleaner;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.steps.Step;

import static android.app.Activity.RESULT_OK;
import static com.chedifier.cleaner.base.BaseActivity.CODE_APP_USAGE_PERMISSION;

/**
 * Created by Administrator on 2017/8/18.
 */

public class StepCheckReadProcPermission extends Step {

    private static final String TAG = "StepCheckReadProcPermission";

    private BaseActivity mActivity;

    public StepCheckReadProcPermission(BaseActivity activity){
        mActivity = activity;
    }

    @Override
    public void doAction() {
        if(hasPermissionForBlocking()){
            doNext();
        }else{
            Log.i(TAG,"we are not grant to access running stats.");

            mActivity.addResultListener(new BaseActivity.IActirityResultListener() {
                @Override
                public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                    if(CODE_APP_USAGE_PERMISSION == requestCode){
                        if(resultCode == RESULT_OK){
                            doNext();
                        }
                        return true;
                    }
                    return false;
                }
            });
            Intent i =new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            mActivity.startActivityForResult(i, CODE_APP_USAGE_PERMISSION);
        }
    }

    public boolean hasPermissionForBlocking(){
        try {
            PackageManager packageManager = mActivity.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(mActivity.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) mActivity.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            }
            return  (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
