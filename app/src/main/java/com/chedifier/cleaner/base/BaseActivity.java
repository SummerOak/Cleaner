package com.chedifier.cleaner.base;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseActivity extends Activity {

    public static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 1001;
    public static final int CODE_ACCESSIBILITY_PERMISSION = 1002;
    public static final int CODE_APP_USAGE_PERMISSION = 1003;

    private List<WeakReference<IActirityResultListener>> mActivityResultListeners = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<WeakReference<IActirityResultListener>> deads = new ArrayList<>();
        for(WeakReference<IActirityResultListener> wrf:mActivityResultListeners){
            if(wrf.get() == null){
                deads.add(wrf);
            }
        }
        deads.removeAll(deads);
        for(WeakReference<IActirityResultListener> wrf:mActivityResultListeners){
            IActirityResultListener l = wrf.get();
            if(l != null && l.onActivityResult(resultCode,resultCode,data)){
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addResultListener(IActirityResultListener l){
        List<WeakReference<IActirityResultListener>> deads = new ArrayList<>();
        for(WeakReference<IActirityResultListener> wrf:mActivityResultListeners){
            if(wrf.get() == null){
                deads.add(wrf);
                continue;
            }
            if(wrf.get() == l){
                return;
            }
        }

        deads.removeAll(deads);
        mActivityResultListeners.add(new WeakReference<IActirityResultListener>(l));
    }

    public interface IActirityResultListener{
        boolean onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
