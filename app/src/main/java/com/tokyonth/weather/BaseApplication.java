package com.tokyonth.weather;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.tokyonth.weather.util.data.FileUtil;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2017/8/20 0020.
 */

public class BaseApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        context = getApplicationContext();
        LeakCanary.install(this);

        if (!FileUtil.isFile("save_weather.json")) {
            FileUtil.saveFile("", "save_weather.json");
        }
    }

    public static Context getContext(){
        return context;
    }

}
