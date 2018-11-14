package com.example.apple.myaccessibilityservice.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.apple.myaccessibilityservice.MyApplication;
import com.example.apple.myaccessibilityservice.util.Config;
import com.example.apple.myaccessibilityservice.util.DateTimeUtil;
import com.example.apple.myaccessibilityservice.util.ShareUtil;

import java.util.HashSet;


public class AccessibilityServiceMonitor extends AccessibilityService {

    private static final String TAG = AccessibilityServiceMonitor.class.getSimpleName();

    private static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING_VALUES =
            "ACTION_ARGUMENT_HTML_ELEMENT_STRING_VALUES";

    public static final String ACTION_UPDATE_SWITCH = "action_update_switch";
    public static final String ACTION_ALAM_TIMER = "action_alarm_timer";

    private boolean isNewday;

    /**
     * Keep App 辅助功能
     */
    private boolean isKeepEnable = true;

    /**
     * 支付宝 App 辅助功能
     */
    private boolean isAlipayForest = true;

    /**
     * 联通手机营业厅 辅助功能
     */
    private boolean isLiangTongEnable = true;

    /**
     * 微信运动的自动点赞器
     */
    private boolean isWeChatMotionEnable = true;

    private H mHandle = new H();
    private static final int MSG_DELAY_ENTER_FOREST = 0;
    private static final int MSG_DELAY_ENTER_LIANGTONG = 1;
    private static final int DEFAULT_DELAY_TIME = 1 * 1000;

    private MyBroadCast myBroadCast;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        myBroadCast = new MyBroadCast();
        myBroadCast.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return super.onStartCommand(intent, flags, startId);
        }

        String action = intent.getAction();
        Log.d(TAG, "onStartCommand Aciton: " + action);

        if (ACTION_UPDATE_SWITCH.equals(action)) {
            updateSwitchStatus();
        } else if (ACTION_ALAM_TIMER.equals(action)) {
            MyApplication.startAlarmTask(this);
            startUI();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType |= AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        info.feedbackType |= AccessibilityServiceInfo.FEEDBACK_AUDIBLE;
        info.feedbackType |= AccessibilityServiceInfo.FEEDBACK_HAPTIC;
        info.feedbackType |= AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags |= AccessibilityServiceInfo.DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
            info.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            info.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        }
        info.notificationTimeout = 0;

        // Ensure the initial touch exploration request mode is correct.
        info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;

        info.packageNames = new String[]{"com.example.apple.myaccessibilityservice", "com.sulian.bitstar"};// 监控的app

//        info.packageNames = new String[]{"com.android.talkbacktests"};
        setServiceInfo(info);


//        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
////        serviceInfo.packageNames = new String[]{"com.gotokeep.keep", "com.eg.android.AlipayGphone", "com.sinovatech.unicom.ui", "com.tencent.mm"};// 监控的app
//        serviceInfo.packageNames = new String[]{"com.example.apple.myaccessibilityservice", "com.sulian.bitstar"};// 监控的app
//        serviceInfo.notificationTimeout = 100;
//        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
//        setServiceInfo(serviceInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String packageName = event.getPackageName().toString();
        String className = event.getClassName().toString();
        Log.d(Config.TAG, "eventType = " + eventType);
        Log.d(Config.TAG, "===============classType == "+className);
        if (className.contains("EditText")) {
            final AccessibilityNodeInfo node = event.getSource();
            if (node == null) {
                Log.w(Config.TAG, "TYPE_VIEW_ACCESSIBILITY_FOCUSED event without a source.");
                return;
            }

            // When a new view gets focus, clear the state of the granularity
            // manager if this event came from a different node than the locked
            // node but from the same window.
            final AccessibilityNodeInfoCompat nodeCompat = new AccessibilityNodeInfoCompat(node);
            String[] supportedHtmlElements = getSupportedHtmlElements(nodeCompat);
            if (supportedHtmlElements == null) {
                return;
            }

            Log.i(Config.TAG, "===============action == "+event.getEventType());
            for (int i = 0; i < supportedHtmlElements.length; i++) {

                Log.i(Config.TAG, supportedHtmlElements[i]);
            }

            final AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(event);
            final AccessibilityNodeInfoCompat sourceNode = record.getSource();
            CharSequence text = sourceNode.getText();
            Log.e("lijing", "webText == "+ text);
        }

//        switch (eventType) {
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                if (isKeepEnable) {
//                    KeepAppMonitor.policy(getRootInActiveWindow(), packageName, className);
//                }
//
//                if (isAlipayForest) {
//                    Log.d(Config.TAG,"packageName = " + packageName + ", className = " + className);
//
//                    AlipayForestMonitor.enterForestUI(getRootInActiveWindow());
//
//                    AlipayForestMonitor.policy(getRootInActiveWindow(), packageName, className);
//                }
//
//                if (isLiangTongEnable) {
//                    if (isNewday) {
//                        LiangTongMonitor.startLiangTongQianDaoUI(getRootInActiveWindow(), packageName, className);
//                    }
//
//                    LiangTongMonitor.policy(getRootInActiveWindow(), packageName, className);
//                }
//
//                if (isWeChatMotionEnable) {
//                    WeChatMotionMonitor.policy(getRootInActiveWindow(), packageName, className);
//                }
//                break;
//
//        }
    }

    @Override
    protected boolean onGesture(int gestureId) {
        return true;
    }

    public static String[] getSupportedHtmlElements(AccessibilityNodeInfoCompat node) {
        HashSet<AccessibilityNodeInfoCompat> visitedNodes =
                new HashSet<AccessibilityNodeInfoCompat>();

        while (node != null) {
            if (visitedNodes.contains(node)) {
                return null;
            }

            visitedNodes.add(node);

            Bundle bundle = node.getExtras();
            CharSequence supportedHtmlElements =
                    bundle.getCharSequence(ACTION_ARGUMENT_HTML_ELEMENT_STRING_VALUES);

            if (supportedHtmlElements != null) {
                return supportedHtmlElements.toString().split(",");
            }

            node = node.getParent();
        }

        return null;
    }


    @Override
    public void onInterrupt() {

    }

    private class H extends Handler {

        public H() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DELAY_ENTER_FOREST:
                    break;
                case MSG_DELAY_ENTER_LIANGTONG:
                    startLiangTongUI();
                    break;
            }
        }
    }

    class MyBroadCast extends BroadcastReceiver {

        public void init(Context mContext) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(this, intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }

            String action = intent.getAction();
            Log.d(Config.TAG, "action = " + action);

            if (Intent.ACTION_USER_PRESENT.equals(action)) {
                isNewday = isNewDay();
                if (isNewday) {
                    startUI();
                }
            }
        }

    }


    /**
     * 更新开关状态
     */
    private void updateSwitchStatus() {
        ShareUtil mShareUtil = new ShareUtil(this);
        isKeepEnable = mShareUtil.getBoolean(Config.APP_KEEP, true);
        isAlipayForest = mShareUtil.getBoolean(Config.APP_ALIPAY_FOREST, true);
        isLiangTongEnable = mShareUtil.getBoolean(Config.APP_LIANG_TONG, true);
        isWeChatMotionEnable = mShareUtil.getBoolean(Config.APP_WECHART_MOTHION, true);
    }

    /**
     * 判断是否新的一天
     */
    private boolean isNewDay() {
        boolean result = false;

        ShareUtil mShareUtil = new ShareUtil(this);
        int saveDay = mShareUtil.getInt(Config.KEY_NEW_DAY, -1);
        int curDay = DateTimeUtil.getDayOfYear();

        if (saveDay != curDay) {
            result = true;
            mShareUtil.setShare(Config.KEY_NEW_DAY, curDay);
        }

        Log.d(Config.TAG, "isNewDay = " + result);
        return result;
    }


    /**
     * 启动UI界面
     */
    private void startUI() {
        startAlipayUI();
    }

    private void startAlipayUI() {
        AlipayForestMonitor.startAlipay(this);
        mHandle.sendEmptyMessageDelayed(MSG_DELAY_ENTER_LIANGTONG, DEFAULT_DELAY_TIME * 10);
    }

    private void startLiangTongUI() {
//        LiangTongMonitor.startLiangTongUI(this);
    }
}
