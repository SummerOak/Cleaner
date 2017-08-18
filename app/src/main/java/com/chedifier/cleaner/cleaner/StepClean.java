package com.chedifier.cleaner.cleaner;

import android.content.Intent;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.steps.Step;

/**
 * Created by Administrator on 2017/8/18.
 */

public class StepClean extends Step {

    private static final String TAG = "StepClean";

    private BaseActivity mActivity;

    public StepClean(BaseActivity activity){
        mActivity = activity;
    }

    @Override
    public void doAction() {
        try{
            Intent i = new Intent(mActivity,CleanMasterAccessbilityService.class);
            i.setAction(CleanMasterAccessbilityService.ACT_CLEAN);
            mActivity.startService(i);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }


}
