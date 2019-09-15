package com.tokyonth.weather.blur;

import android.Manifest;

/**
 * Created by GaoCh on 2018/7/24.
 */

public class ConstValue {
    public static final String hasDB="hasDB";
    public static final String LocalDatabaseName = "simweather.db";
    public static String githubUrl="https://github.com/GaoChrishao/SimWeather";
    public static String myMail="gaochrishao@outlook.com";
    public static String key = "此处请使用自己的key";  //请使用自己的key
    public static String colorText = "#ffffff";
    public static String colorPrimary = "#67b4c7";
    public static String colorPrimaryDark = "#497d8b";
    public static String configDataName = "configData";
    public static String updateHours = "updateHours";
    public static int defaultUpdateHours = 4;
    public static String isBackGroundService="isBackGroundService";
    public static String isBackGroundPNG="isBackBroundPNG";
    public static String isBlur="isBlur";

    public static String sp_key="key";
    public static String sp_location="location";
    public static String sp_responseText="responseText6";
    public static String sp_weatherid="weatherId";

    //定位
    public static double currentLatitude;
    public static double currentLongitude;

    //毛玻璃效果参数
    public static String sp_radius="sp_radius";
    public static int RoundCorner=50;
    public static int radius=10;
    public static int scaleFactor=26;

    //选色
    public static int colorRange=64;

    public  static String getKey(){
        return key;
    }
    public static String getColorText(){
        return colorText;
    }
    public static String getColorPrimary(){
        return colorPrimary;
    }
    public static String getConfigDataName(){
        return configDataName;
    }

    public static void setColorText(String colorText) {
        ConstValue.colorText = colorText;
    }
    public static void setColorPrimary(String colorPrimary) {
        ConstValue.colorPrimary = colorPrimary;
    }
    public static String getColorPrimaryDark() {
        return colorPrimaryDark;
    }
    public static void setColorPrimaryDark(String colorPrimaryDark) {
        ConstValue.colorPrimaryDark = colorPrimaryDark;
    }
    public static int getDefaultUpdateHours() {
        return defaultUpdateHours;
    }
    public static String getUpdateHours() {
        return updateHours;
    }
    public static String getIsBackGroundPNG() {
        return isBackGroundPNG;
    }

    public static final String[] LOCATIONGPS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

}
