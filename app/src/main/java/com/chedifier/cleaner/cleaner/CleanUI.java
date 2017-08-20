package com.chedifier.cleaner.cleaner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chedifier.cleaner.R;
import com.chedifier.cleaner.base.ClickHandler;
import com.chedifier.cleaner.base.ScreenUtils;
import com.chedifier.cleaner.base.SystemUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/18.
 */

public class CleanUI {

    private static final String TAG = "CleanUI";

    private WindowManager.LayoutParams mWLParams;
    private LinearLayout mLayout;
    private ScrollView mScroller;
    private TextView mTextView;
    private SeekBar mProgressBar;
    private TextView mProgressTips;
    private ImageButton mImageButton;
    private ClickHandler mClickHandler;
    private Context mAppContext;
    private boolean mShowing = false;

    private StringBuilder mContent = new StringBuilder(128);

    private int mTotal = 0;
    private int mProgress = 0;

    private long mOldAvaiableMem = 0;
    private long mNewAvaiableMen = 0;

    public CleanUI(Context context){
        mAppContext = context.getApplicationContext();
        initView();
    }

    private void initView(){
        mWLParams = new WindowManager.LayoutParams();
        mWLParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWLParams.format = PixelFormat.RGBA_8888;
        mWLParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                |WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mWLParams.gravity = Gravity.CENTER;

        mLayout = new LinearLayout(mAppContext);
        mLayout.setBackgroundResource(R.color.color_main);
        mLayout.setOrientation(LinearLayout.VERTICAL);

        int[] screenSize = ScreenUtils.getScreenSize(mAppContext);

        mTextView = new TextView(mAppContext);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        mTextView.setTextColor(0xff171717);
        mTextView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);

        mScroller = new ScrollView(mAppContext);
        mScroller.addView(mTextView);
        mScroller.setFillViewport(true);
        mScroller.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mScroller.setVerticalScrollBarEnabled(false);
        mScroller.setPadding(0,0,0,ScreenUtils.dip2px(mAppContext,20));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                screenSize[1]*5/7);
        lp.topMargin = ScreenUtils.dip2px(mAppContext,40);
        lp.gravity = Gravity.CENTER;
        mLayout.addView(mScroller,lp);

        mProgressBar = new SeekBar(mAppContext);
        mProgressBar.setBackgroundColor(Color.TRANSPARENT);
        mProgressBar.setPadding(0,0,0,0);
        mProgressBar.setProgressDrawable(mAppContext.getResources().getDrawable(R.drawable.progress_drawable));
        mProgressBar.setThumb(null);
        mProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(mAppContext,4));
        lp.topMargin =  ScreenUtils.dip2px(mAppContext,12);
        lp.leftMargin = screenSize[0]/6;
        lp.rightMargin = screenSize[0]/6;
        mLayout.addView(mProgressBar,lp);

        mProgressTips = new TextView(mAppContext);
        mProgressTips.setGravity(Gravity.CENTER);
        mProgressTips.setTextColor(0xFF303F9F);
        mProgressTips.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin =  ScreenUtils.dip2px(mAppContext,12);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mLayout.addView(mProgressTips,lp);

        mImageButton = new ImageButton(mAppContext);
        mImageButton.setBackgroundColor(Color.TRANSPARENT);
        mImageButton.setImageResource(R.drawable.sensor_btn);

        mClickHandler = new ClickHandler(mImageButton, new ClickHandler.IOnMuliClickListener() {
            @Override
            public void onMultiClick(View v, int times) {
                Log.i(TAG,"onMultiClick: " + times);
                if(times == 2){
                    Cleaner.stop(mAppContext);
                }else if(times >= 3){
                    Cleaner.exit(mAppContext);
                }else{
                    //onTaskStart("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                }
            }
        });

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin =  ScreenUtils.dip2px(mAppContext,2);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mLayout.addView(mImageButton,lp);
    }

    private void updateContent(){
        mTextView.setText(mContent.toString());

        mScroller.post(new Runnable() {
            @Override
            public void run() {
                mScroller.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void onStart(int total){
        mTotal = total;
        mContent = new StringBuilder(128);
        mContent.append("start clean...\n\n");
        updateContent();

        mProgress = 0;
        mProgressBar.setMax(mTotal);
        mProgressBar.setProgress(mProgress);
        mProgressBar.setVisibility(View.VISIBLE);

        mOldAvaiableMem = SystemUtils.getMemoryInfo(mAppContext).availMem;
    }

    public void onTaskStart(String pkgName){
        mProgress++;
        mProgressBar.setProgress(mProgress);
        mProgressTips.setText(mProgress + "/" + mTotal);
        mContent.append(pkgName).append("...");
        updateContent();
    }

    public void onResult(CleanMasterAccessbilityService.TASK_STATE state){
        if(state != null){
            mContent.append(state.name());
        }

        mContent.append("\n");
    }

    public void onStop(List<String> stubbornApps){

        mContent.append("\n");

        if(mProgress >= mTotal){
            mContent.append("clean successed!").append("\n");
        }else{
            mContent.append("clean stoped!").append("\n");
        }

        mNewAvaiableMen = SystemUtils.getMemoryInfo(mAppContext).availMem;
        String opt  = "opt " + Formatter.formatFileSize(mAppContext,mOldAvaiableMem)
                + " > " + Formatter.formatFileSize(mAppContext,mNewAvaiableMen)
                + " freed: " + Formatter.formatFileSize(mAppContext,(mNewAvaiableMen-mOldAvaiableMem));

        mContent.append("\n\n").append(opt).append("\n");
        mContent.append("total memory: " + Formatter.formatFileSize(mAppContext,SystemUtils.getMemoryInfo(mAppContext).totalMem)).append("\n");

        if(stubbornApps != null && stubbornApps.size() > 0){
            mContent.append("\n\nthose " + stubbornApps.size() +  " packages stop failed: \n\n");
            for(String s:stubbornApps){
                mContent.append(s).append("\n");
            }
        }

        updateContent();
    }

    public void show(boolean show){
        if(mShowing == show){
            return;
        }

        WindowManager wmgr = (WindowManager)mAppContext.getSystemService(Context.WINDOW_SERVICE);
        if(show){
            wmgr.addView(mLayout, mWLParams);
        }else{
            wmgr.removeView(mLayout);
        }

        mShowing = show;
    }

}
