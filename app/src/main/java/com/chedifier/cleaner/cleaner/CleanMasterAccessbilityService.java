package com.chedifier.cleaner.cleaner;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.chedifier.cleaner.base.StringUtils;
import com.chedifier.cleaner.base.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */

public class CleanMasterAccessbilityService extends AccessibilityService {

    private static final String TAG = "CleanMasterAccessbilityService";

    private CleanUI mView;

    public static final String ACT_CLEAN = "1";
    public static final String ACT_STOP = "2";
    public static final String ACT_EXIT = "3";

    private static boolean sRunning = false;

    private List<String> mTasks = new ArrayList<>();
    private String mCurrentTask;
    private List<String> mSticky = new ArrayList<>();

    private TASK_STATE mState = TASK_STATE.INIT;
    public enum TASK_STATE{
        INIT,
        FOUND,
        FORCE_STOPED,
        ENSUREED,
        SUCCESS,
        FAILED,
    }

    private Action mAction = new Action(Looper.getMainLooper());

    private final int MAX_RETRY_TIME = 3;
    private int mCurrentRetryTime = 0;

    private class Action extends Handler{

        public static final int S_INIT = 0;
        public static final int S_START = 1;
        public static final int S_NEXT = 2;
        public static final int S_HIDE_PANEL = 3;


        public Action(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case S_START:{
                    startClean();
                    break;
                }

                case S_INIT:
                case S_NEXT:{
                    if(sRunning){
                        takeNextTask();
                    }
                    break;
                }
                case S_HIDE_PANEL:{
                    hideOverlayAndExit();
                    break;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mView = new CleanUI(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand " + intent + " flags: " + flags + " startId: " + startId);

        if(intent != null){
            if(ACT_CLEAN.equals(intent.getAction())){
                mAction.sendEmptyMessage(Action.S_START);
            }else if(ACT_STOP.equals(intent.getAction())){
                onStop();
            }else if(ACT_EXIT.equals(intent.getAction())){
                onStop();

                mAction.sendEmptyMessageDelayed(Action.S_HIDE_PANEL,1000);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!sRunning){
            return;
        }

        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if(mCurrentRetryTime <= MAX_RETRY_TIME && !tryClickStop(nodeInfo)){
                    if(tryEnsure(nodeInfo)){
                        Log.i(TAG,"retried " + mCurrentRetryTime);
                        if(++mCurrentRetryTime > MAX_RETRY_TIME){
                            mSticky.add(mCurrentTask);
                            setState(TASK_STATE.FAILED);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            }

                            takeNextTask();
                        }else{
                            mAction.removeMessages(Action.S_NEXT);
                            mAction.sendEmptyMessageDelayed(Action.S_NEXT,500);
                        }
                    }
                }else{
                    mAction.removeMessages(Action.S_NEXT);
                    mAction.sendEmptyMessageDelayed(Action.S_NEXT,1000);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void setState(TASK_STATE state){
        if(state != null && mState.ordinal() < state.ordinal()){
            mState = state;

            Log.i(TAG,"state = " + state.name());
        }
    }

    private void reset(){
        mSticky.clear();
        mTasks.clear();
        mCurrentTask = null;
    }

    private void initTasks(){
        reset();
        mTasks.addAll(TargetTaskFetcher.getPackagesCanbeStop(this));

        Log.i(TAG,"installed: " + mTasks.size());
    }


    private boolean takeNextTask(){
        Log.i(TAG,"takeNextTask");
        mCurrentRetryTime = 0;

        if(mTasks.size() > 0){
            String packageName = mTasks.remove(0);
            if(packageName != null){

                notifyTaskStateIfNeeded();

                mAction.removeMessages(Action.S_NEXT);
                mAction.sendEmptyMessageDelayed(Action.S_NEXT,2000);

                try {
                    mCurrentTask = packageName;
                    mView.onTaskStart(packageName);

                    mState = TASK_STATE.INIT;
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.setData(Uri.fromParts("package", packageName, null));

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityOptions options = ActivityOptions.makeCustomAnimation(CleanMasterAccessbilityService.this,0,0);
                        CleanMasterAccessbilityService.this.startActivity(i,options.toBundle());
                    }else{
                        CleanMasterAccessbilityService.this.startActivity(i);
                    }
                }catch (Throwable t){
                    t.printStackTrace();
                    mAction.removeMessages(Action.S_NEXT);
                    mAction.sendEmptyMessage(Action.S_NEXT);
                }
            }else{
                notifyTaskStateIfNeeded();
            }
        }else{
            onStop();
            mAction.sendEmptyMessageDelayed(Action.S_HIDE_PANEL,2000);
        }

        return false;
    }

    private void notifyTaskStateIfNeeded(){
        if(mCurrentTask != null){
            mView.onResult(mState);
        }
    }

    private void hideOverlayAndExit(){
        mView.show(false);
        SystemUtils.killProcess();
    }

    private void onStop(){
        if(!sRunning){
            return;
        }

        sRunning = false;

        mAction.removeMessages(Action.S_NEXT);
        mAction.removeMessages(Action.S_INIT);

        notifyTaskStateIfNeeded();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            SystemUtils.backToHome(this.getApplicationContext());
        }

        mView.onStop(mSticky);

        reset();
    }

    public void startClean(){
        sRunning = true;
        mView.show(true);
        initTasks();

        mView.onStart(mTasks.size());
        mAction.sendEmptyMessage(Action.S_INIT);
    }

    //遍历节点，模拟点击安装按钮
    private boolean tryClickStop(final AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();
            if(nodeInfo.getText() != null){
                String nodeCotent = nodeInfo.getText().toString();
                if ((StringUtils.contains(nodeCotent,"停止")
                        || StringUtils.containsIgnoreCase(nodeCotent,"stop"))

                        && nodeInfo.isClickable()) {
                    if(nodeInfo.isEnabled()){
                        Log.i(TAG, "try stop success");
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        setState(TASK_STATE.FORCE_STOPED);
                        return true;
                    }else{
                        if(mState.ordinal() >= TASK_STATE.FOUND.ordinal()){
                            setState(TASK_STATE.SUCCESS);
                        }else{
                            setState(TASK_STATE.FOUND);
                        }
                    }
                }
            }

            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (tryClickStop(childNodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryEnsure(final AccessibilityNodeInfo nodeInfo){
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();
            if(nodeInfo.getText() != null){
                String nodeCotent = nodeInfo.getText().toString();
                if ((StringUtils.contains(nodeCotent,"确定")
                        || StringUtils.containsIgnoreCase(nodeCotent,"ok"))

                        && nodeInfo.isClickable() && nodeInfo.isEnabled()) {
                    Log.i(TAG, "try ensure success");
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    setState(TASK_STATE.ENSUREED);
                    return true;
                }
            }

            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (tryEnsure(childNodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }
}
