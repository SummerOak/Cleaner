package com.chedifier.cleaner.base;

import android.view.View;

/**
 * Created by Administrator on 2017/8/20.
 */

public class ClickHandler {

    private static final int INTERVAL = 300;
    private static final int MAX_CLICK = 5;

    private int mClick = 0;
    private boolean mWaiting = false;
    private Runnable mPerformMultiClick = new Runnable() {
        @Override
        public void run() {
            mWaiting = false;
            performClick(mClick);
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!mWaiting){
                mClick = 1;
                mWaiting = true;
                mView.postDelayed(mPerformMultiClick,INTERVAL);
            }else if(mClick >= MAX_CLICK){
                mView.removeCallbacks(mPerformMultiClick);
                mWaiting = false;
                performClick(mClick);
                mClick = 0;
            }else{
                ++mClick;
                mView.removeCallbacks(mPerformMultiClick);
                mView.postDelayed(mPerformMultiClick,INTERVAL);
            }
        }
    };

    private View mView;
    private IOnMuliClickListener mListener;
    public ClickHandler(View v,IOnMuliClickListener l){
        mListener = l;
        mView = v;
        mView.setOnClickListener(mOnClickListener);
    }

    private void performClick(int click){
        mListener.onMultiClick(mView,click);
    }

    public View.OnClickListener getOnClickListener(){
        return mOnClickListener;
    }

    public interface IOnMuliClickListener{
        void onMultiClick(View v,int times);
    }

}
