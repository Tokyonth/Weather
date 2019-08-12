package com.tokyonth.weather.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tokyonth.weather.BaseApplication;

/**
 * Created by Administrator on 2017/4/7.
 */

public class PreferencesLoader {

    public static final String IMPORT_DATA = "import_data";
    public static final String WEATHER_COLOR = "weather_color";
    public static final String DEFAULT_CITY_TEMP = "temp";
    public static final String DEFAULT_CITY_IMG = "img";

    public static void putBoolean(String name , boolean flag){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext()).edit();
        editor.putBoolean(name,flag);
        editor.apply();
    }

    public static Boolean getBoolean( String name , boolean strData){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext());
        return sp.getBoolean(name,strData);
    }

    public static void putInt( String name , int flag){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext()).edit();
        editor.putInt(name,flag);
        editor.apply();
    }

    public static int getInt(String name , int strData){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext());
        return sp.getInt(name,strData);
    }


}
