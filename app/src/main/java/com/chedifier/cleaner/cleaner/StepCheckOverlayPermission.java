package com.chedifier.cleaner.cleaner;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.steps.Step;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/8/18.
 */

public class StepCheckOverlayPermission extends Step {

    private static final String TAG = "StepCheckOverlayPermission";

    private BaseActivity mActivity;

    public StepCheckOverlayPermission(BaseActivity activity){
        mActivity = activity;
    }

    @Override
    public void doAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mActivity)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            mActivity.addResultListener(new BaseActivity.IActirityResultListener() {
                @Override
                public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                    if(requestCode == BaseActivity.CODE_DRAW_OVER_OTHER_APP_PERMISSION){
                        if(resultCode == RESULT_OK){
                            doNext();
                        }

                        return true;
                    }
                    return false;
                }
            });
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mActivity.getPackageName()));
            mActivity.startActivityForResult(intent, BaseActivity.CODE_DRAW_OVER_OTHER_APP_PERMISSION);

        }else{
            doNext();
        }
    }

}
