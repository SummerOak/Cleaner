package com.chedifier.cleaner.cleaner;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/8/18.
 */

public class CleanUI {

    private static final String TAG = "CleanUI";

    private WindowManager.LayoutParams mWLParams;
    private FrameLayout mLayout;
    private TextView mTextView;
    private Context mAppContext;
    private boolean mShowing = false;

    public CleanUI(Context context){
        mAppContext = context.getApplicationContext();
        initView();
    }

    private void initView(){
        mWLParams = new WindowManager.LayoutParams();
        mWLParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mWLParams.format = PixelFormat.RGBA_8888;
        mWLParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWLParams.gravity = Gravity.CENTER;
        mWLParams.x = 0;
        mWLParams.y = 0;

//        mWLParams.width = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        mWLParams.height = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        mWLParams.width = 100;
        mWLParams.height = 100;

        mLayout = new FrameLayout(mAppContext);
        mLayout.setBackgroundColor(0xff13b3e9);

        mTextView = new TextView(mAppContext);
        mLayout.addView(mTextView);

    }


    public void updateResult(String content){
        mTextView.setText(content);
    }

    public void show(boolean show){
        if(mShowing == show){
            return;
        }

//        WindowManager wmgr = (WindowManager)mAppContext.getSystemService(Context.WINDOW_SERVICE);
//        if(show){
//            wmgr.addView(mLayout, mWLParams);
//        }else{
//            wmgr.removeView(mLayout);
//        }

        mShowing = show;
    }

}
