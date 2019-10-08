package com.tokyonth.weather;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.tokyonth.weather.utils.tools.SPUtils;
import com.tokyonth.weather.utils.file.FileUtil;

import org.litepal.LitePal;

public class BaseApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        context = getApplicationContext();

        SPUtils.getInstance(context, "config");
        if (!FileUtil.isFile("save_weather.json")) {
            FileUtil.saveFile("", "save_weather.json");
        }

    }

    public static Context getContext(){
        return context;
    }

}
