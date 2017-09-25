package com.chedifier.cleaner;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.base.SystemUtils;
import com.chedifier.cleaner.cleaner.CleanUI;
import com.chedifier.cleaner.cleaner.Cleaner;
import com.chedifier.cleaner.cleaner.StepCheckOverlayPermission;
import com.chedifier.cleaner.cleaner.TargetTaskFetcher;

public class CleanerActivity extends BaseActivity {

    private static final String TAG = "CleanerActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("ckdk","Settings.canDrawOverlays(this) "+ Settings.canDrawOverlays(this));
        }

        TextView view = new TextView(this);
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,33);
        view.setTextColor(getResources().getColor(R.color.color_main));
        view.setGravity(Gravity.CENTER);
        view.setText("Cleaner is working");
        setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.killProcess();
            }
        });

//        new CleanUI(this).show(true);

        Cleaner.start(this);
    }

}
