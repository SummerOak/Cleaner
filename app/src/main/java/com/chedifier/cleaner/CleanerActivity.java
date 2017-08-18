package com.chedifier.cleaner;

import android.os.Bundle;
import android.util.Log;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.base.SystemUtils;
import com.chedifier.cleaner.cleaner.Cleaner;

public class CleanerActivity extends BaseActivity {

    private static final String TAG = "CleanerActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        Cleaner.start(this);

//        CleanUI ui = new CleanUI(this);
//        ui.show(true);
    }

}
