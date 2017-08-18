package com.chedifier.cleaner.cleaner;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.chedifier.cleaner.base.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */

public class CleanMasterAccessbilityService extends AccessibilityService {

    private static final String TAG = "CleanMasterAccessbilityService";

    private static final String SETTING_PKG = "com.android.settings";
    private static final List<String> WHITE_LIST = new ArrayList<>();
    static {
        WHITE_LIST.add("android");
        WHITE_LIST.add("com.google.android.gsf");
        WHITE_LIST.add("com.google.android.gsf.login");
        WHITE_LIST.add("com.android.systemui");
        WHITE_LIST.add("com.google.android.packageinstaller");
        WHITE_LIST.add(SETTING_PKG);
    }

    private CleanUI mView;

    public static final String ACT_CLEAN = "1";

    private static boolean sRunning = false;

    private List<String> mTasks = new ArrayList<>();
    private String mCurrentTask;
    private List<String> mSticky = new ArrayList<>();

    private Action mAction = new Action(Looper.getMainLooper());

    private final int MAX_RETRY_TIME = 10;
    private int mCurrentRetryTime = 0;

    private class Action extends Handler{

        public static final int S_INIT = 0;
        public static final int S_START = 1;
        public static final int S_NEXT = 2;


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
                    takeNextTask();
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

        if(intent != null && ACT_CLEAN.equals(intent.getAction())){
            mAction.sendEmptyMessage(Action.S_START);
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
                if(!tryClickStop(nodeInfo)){
                    if(tryEnsure(nodeInfo)){
                        if(++mCurrentRetryTime > MAX_RETRY_TIME){
                            mSticky.add(mCurrentTask);
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

    private void initTasks(){
        mSticky.clear();
        mTasks.clear();
        List<String> excepts = new ArrayList<>();
        excepts.addAll(WHITE_LIST);
        excepts.add(getPackageName());
//        mTasks.addAll(PackageUtils.getAllRunningPackages(this,excepts));

        mTasks.add("com.tencent.mobileqq");
        mTasks.add("com.tencent.mobileqq");
        mTasks.add("com.tencent.mtt");
        mTasks.add("com.tencent.mtt");
        mTasks.add("com.tencent.mtt");
        mTasks.add("com.tencent.mm");
        mTasks.add("com.tencent.mm");
        mTasks.add("com.tencent.mtt");


        Log.i(TAG,"installed: " + mTasks.size());
    }


    private boolean takeNextTask(){
        mCurrentRetryTime = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }

        if(mTasks.size() > 0){
            String packageName = mTasks.remove(0);
            if(packageName != null){

                mView.updateResult("stop: " + packageName + " remain " + mTasks.size());

                mAction.removeMessages(Action.S_NEXT);
                mAction.sendEmptyMessageDelayed(Action.S_NEXT,2000);

                try {
                    mCurrentTask = packageName;
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setData(Uri.fromParts("package", packageName, null));
                    CleanMasterAccessbilityService.this.startActivity(i);
                }catch (Throwable t){
                    mAction.removeMessages(Action.S_NEXT);
                    mAction.sendEmptyMessage(Action.S_NEXT);
                }
            }
        }else{
            Log.i(TAG,"clean finished. and those " + mSticky.size() + " sticky packages cannot be stoped: ");
            mView.updateResult("clean finished. and those " + mSticky.size() + " sticky packages cannot be stoped: ");
            for(String s:mSticky){
                Log.i(TAG,s);
            }
            mView.show(false);
            sRunning = false;
            System.exit(0);
            Process.killProcess(Process.myPid());
        }

        return false;
    }

    public void startClean(){
        sRunning = true;
        mView.show(true);
        initTasks();

        mView.updateResult("start...");
        mAction.sendEmptyMessage(Action.S_INIT);
    }

    //遍历节点，模拟点击安装按钮
    private boolean tryClickStop(final AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();
            if(nodeInfo.getText() != null){
                String nodeCotent = nodeInfo.getText().toString();
                if (StringUtils.contains(nodeCotent,"停止") && nodeInfo.isClickable() && nodeInfo.isEnabled()) {
                    Log.d(TAG, "try stop success");
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
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
                if (StringUtils.contains(nodeCotent,"确定") && nodeInfo.isClickable() && nodeInfo.isEnabled()) {
                    Log.d(TAG, "try ensure success");
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
