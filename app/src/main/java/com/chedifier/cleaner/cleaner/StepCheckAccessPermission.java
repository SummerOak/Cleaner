package com.chedifier.cleaner.cleaner;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.base.StringUtils;
import com.chedifier.cleaner.steps.Step;

import java.util.List;

/**
 * Created by Administrator on 2017/8/18.
 */

public class StepCheckAccessPermission extends Step {
    private static final String TAG = "StepCheckAccessPermission";

    private BaseActivity mActivity;

    public StepCheckAccessPermission(BaseActivity activity){
        mActivity = activity;
    }

    @Override
    public void doAction() {
        AccessibilityManager am = (AccessibilityManager) mActivity.getSystemService(Context.ACCESSIBILITY_SERVICE);

//        List<AccessibilityServiceInfo> runningServices = am
//                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
//        for (AccessibilityServiceInfo service : runningServices) {
//            if (StringUtils.contains(service.getId(),CleanMasterAccessbilityService.class.getSimpleName())) {
//                doNext();
//                return;
//            }
//        }

        if(isAccessibilitySettingsOn(mActivity)){
            doNext();
            return;
        }

        Log.i(TAG,"we have not permission yet,guide user to grant it.");

        mActivity.addResultListener(new BaseActivity.IActirityResultListener() {
            @Override
            public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                if(requestCode == BaseActivity.CODE_ACCESSIBILITY_PERMISSION){
                    doNext();
                    return true;
                }
                return false;
            }
        });

        Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivityForResult(i,BaseActivity.CODE_ACCESSIBILITY_PERMISSION);
    }

    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + CleanMasterAccessbilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

}
