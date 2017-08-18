package com.chedifier.cleaner.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";

    public static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 1001;
    public static final int CODE_ACCESSIBILITY_PERMISSION = 1002;
    public static final int CODE_APP_USAGE_PERMISSION = 1003;

    private boolean mFirstShow = true;

    private List<WeakReference<IPActivityListener>> mActivityResultListeners = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate " + this.getClass().getSimpleName());
        super.onCreate(savedInstanceState);
        mFirstShow = true;
    }

    @Override
    protected void onRestart() {
        Log.i(TAG,"onRestart " + this.getClass().getSimpleName());
        super.onRestart();

        List<WeakReference<IPActivityListener>> deads = new ArrayList<>();
        for(WeakReference<IPActivityListener> wrf:mActivityResultListeners){
            if(wrf.get() == null){
                deads.add(wrf);
            }
        }

        mActivityResultListeners.removeAll(deads);

        List<WeakReference<IPActivityListener>> t = new ArrayList<>(mActivityResultListeners);
        for(WeakReference<IPActivityListener> wrf:t){
            IPActivityListener l = wrf.get();
            if(l != null){
                l.onRestart();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<WeakReference<IPActivityListener>> deads = new ArrayList<>();
        for(WeakReference<IPActivityListener> wrf:mActivityResultListeners){
            if(wrf.get() == null){
                deads.add(wrf);
            }
        }
        mActivityResultListeners.removeAll(deads);
        List<WeakReference<IPActivityListener>> t = new ArrayList<>(mActivityResultListeners);
        for(WeakReference<IPActivityListener> wrf:t){
            IPActivityListener l = wrf.get();
            if(l != null && l.onActivityResult(resultCode,resultCode,data)){
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addResultListener(IPActivityListener l){
        List<WeakReference<IPActivityListener>> deads = new ArrayList<>();
        for(WeakReference<IPActivityListener> wrf:mActivityResultListeners){
            if(wrf.get() == null){
                deads.add(wrf);
                continue;
            }
            if(wrf.get() == l){
                return;
            }
        }

        deads.removeAll(deads);
        mActivityResultListeners.add(new WeakReference<>(l));
    }

    public interface IPActivityListener {
        void onRestart();
        boolean onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
