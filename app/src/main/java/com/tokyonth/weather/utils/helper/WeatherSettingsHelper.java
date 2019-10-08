package com.tokyonth.weather.utils.helper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tokyonth.weather.notification.NotificationBrodcaseRecever;
import com.tokyonth.weather.notification.NotificationTools;
import com.tokyonth.weather.utils.tools.SPUtils;

public class WeatherSettingsHelper {

    private static String actionStr = "com.tokyonth.weather.receiver";
    private static NotificationBrodcaseRecever receiver;

    public static NotificationBrodcaseRecever setWeatherNotification(Context context, boolean bool) {
        Intent intent = new Intent();
        intent.setAction(actionStr);
        IntentFilter filter = new IntentFilter();
        filter.addAction(actionStr);
        receiver = new NotificationBrodcaseRecever();
        context.registerReceiver(receiver, filter);

        if (bool) {
            intent.putExtra("key", NotificationTools.OPEN_WEATHER_NOTIFICATION);
            context.sendBroadcast(intent);
            SPUtils.putData("switch_notification_weather", true);
        } else {
            intent.putExtra("key", NotificationTools.CLOSE_WEATHER_NOTIFICATION);
            context.sendBroadcast(intent);
            SPUtils.putData("switch_notification_weather", false);
        }
        return receiver;
    }

}
