package com.tokyonth.weather.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ListenerNotificationBrodcaseRecever extends BroadcastReceiver {

    private static ICallBack mCallBack;

    public static void setmCallBack(ICallBack iCallBack) {
        mCallBack = iCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int key = intent.getIntExtra("key", -1);
        NotificationTools.getInstance(context);

        mCallBack.callBack(key);
    }

    interface ICallBack {
        void callBack(int key);
    }
}
