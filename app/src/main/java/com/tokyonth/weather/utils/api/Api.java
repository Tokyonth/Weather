package com.tokyonth.weather.utils.api;

import com.tokyonth.weather.utils.tools.SPUtils;

public class Api {

    public static final String JISU_URL = "http://api.jisuapi.com/weather/";
    public static final String APP_KEY = "2ef904b468102964";

    public static String getJisuAppKey() {
        if ((boolean)SPUtils.getData("cust_key", false)) {
            return (String) SPUtils.getData("weather_key", "");
        } else {
            return APP_KEY;
        }
    }

}
