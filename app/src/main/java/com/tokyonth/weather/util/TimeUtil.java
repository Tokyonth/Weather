package com.tokyonth.weather.util;

import android.content.ContentResolver;
import android.util.Log;

import java.util.Calendar;

import static com.tokyonth.weather.BaseApplication.getContext;

public class TimeUtil {

    public static String getN_D() {
        ContentResolver cv = getContext().getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
        String amPmValues = null;
        if(strTimeFormat!=null && strTimeFormat.equals("24")) {
            Log.i("Debug","24小时制");
        } else {
            Calendar c = Calendar.getInstance();
            if(c.get(Calendar.AM_PM) == 0) {
                amPmValues = "AM";
            } else {
                amPmValues = "PM";
            }
            Log.i("Debug","12小时制现在是：" + amPmValues);
        }
        return amPmValues;
    }


}
