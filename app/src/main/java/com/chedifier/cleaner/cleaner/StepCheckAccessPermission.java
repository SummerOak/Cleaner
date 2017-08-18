package com.chedifier.cleaner.cleaner;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.steps.Step;

/**
 * Created by Administrator on 2017/8/18.
 */

public class StepCheckAccessPermission extends Step implements BaseActivity.IPActivityListener{
    private static final String TAG = "StepCheckAccessPermission";

    private BaseActivity mActivity;

    public StepCheckAccessPermission(BaseActivity activity){
        mActivity = activity;
    }

    @Override
    public void doAction() {
        if(isAccessibilitySettingsOn(mActivity)){
            doNext();
            return;
        }

        Log.i(TAG,"we have not permission yet,guide user to grant it.");

        mActivity.addResultListener(this);

        Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptions options = ActivityOptions.makeCustomAnimation(mActivity,0,0);
            mActivity.startActivityForResult(i,BaseActivity.CODE_ACCESSIBILITY_PERMISSION,options.toBundle());
        }else{
            mActivity.startActivityForResult(i,BaseActivity.CODE_ACCESSIBILITY_PERMISSION);
        }
    }

    private void afterSetting(){
        if(isAccessibilitySettingsOn(mActivity)){
            Log.i(TAG,"we grant to accessbility check next");
            doNext();
            return;
        }

        Log.i(TAG,"permission not grant yet,we can't step forward,finish it.");
        mActivity.finish();
    }

    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + CleanMasterAccessbilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "Our accessibility is switched on!");
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onRestart() {
        afterSetting();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BaseActivity.CODE_ACCESSIBILITY_PERMISSION){
            Log.i(TAG,"onActivityResult");
            afterSetting();
            return true;
        }
        return false;
    }

}
