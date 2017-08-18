package com.chedifier.cleaner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chedifier.cleaner.base.BaseActivity;
import com.chedifier.cleaner.cleaner.Cleaner;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cleaner.start(MainActivity.this);
            }
        });
    }

}
