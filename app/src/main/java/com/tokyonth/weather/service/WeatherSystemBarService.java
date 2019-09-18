package com.tokyonth.weather.service;

import android.app.Notification;
import android.os.Parcel;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;

public class WeatherSystemBarService extends StatusBarNotification {

    public WeatherSystemBarService(String pkg, String opPkg, int id, String tag, int uid, int initialPid, int score, Notification notification, UserHandle user, long postTime) {
        super(pkg, opPkg, id, tag, uid, initialPid, score, notification, user, postTime);
    }

    public WeatherSystemBarService(Parcel in) {
        super(in);
    }

}
