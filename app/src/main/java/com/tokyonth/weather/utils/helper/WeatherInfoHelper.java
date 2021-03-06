package com.tokyonth.weather.utils.helper;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.tokyonth.weather.BaseApplication;
import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.model.bean.entity.Daily;
import com.tokyonth.weather.model.bean.entity.Hourly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeatherInfoHelper {

    public static String getHourlyWeatherTipsInfo(List<Hourly> hourlyList){
        String info = "24小时\t无雨雪天气";
        for (Hourly hourly : hourlyList){
            if(!hourly.getImg().isEmpty()){
                int imgCode = Integer.valueOf(hourly.getImg());
                if(imgCode>= 3){
                    info = hourly.getTime() + "\t" + hourly.getWeather();
                    return info;
                }
            }
        }
        return info;
    }

    public static String getDayWeatherTipsInfo(List<Daily> dailyList){
        String weatherInfo = "未来7天\t无雨雪天气";
        for(Daily daily : dailyList){
            if(!daily.getDay().getImg().isEmpty()){
                int dayImgCode = Integer.valueOf(daily.getDay().getImg());
                int nightImgCode = Integer.valueOf(daily.getNight().getImg());
                if(dayImgCode >= 3){
                    if(getDay(daily.getDate()).equals("今天")){
                        weatherInfo = getDay(daily.getDate()) + "白天\t" + daily.getDay().getWeather();
                    }else {
                        weatherInfo = getDay(daily.getDate()) + "日白天\t" + daily.getDay().getWeather();
                    }
                    return weatherInfo;
                }
                if(nightImgCode >= 3){
                    if(getDay(daily.getDate()).equals("今天")){
                        weatherInfo = getDay(daily.getDate()) + "夜\t" + daily.getDay().getWeather();
                    }else {
                        weatherInfo = getDay(daily.getDate()) + "日夜\t" + daily.getDay().getWeather();
                    }
                    return weatherInfo;
                }
            }
        }
        return weatherInfo;
    }

    public static String getDay(String date){
        String[] dates = date.split("-");
        String today = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        if(dates[2].equals(today)){
            return "今天";
        }
        return dates[2];
    }

    public static String getUpdateTime(String time){
        String[] times = time.split(" ");
        return times[1];
    }

    public static int getWeatherImagePath(String img){
        Resources resources = BaseApplication.getContext().getResources();
        String packageName = BaseApplication.getContext().getPackageName();
        String category = "drawable";
        int weatherImagePath = 0;
        if (!img.isEmpty()) {
            int imgCode = Integer.valueOf(img);
            if(imgCode == 0){
                weatherImagePath = resources.getIdentifier("weather_sunny",category,packageName);
            }else if(imgCode == 1){
                weatherImagePath = resources.getIdentifier("weather_cloudy",category,packageName);
            }else if(imgCode == 2){
                weatherImagePath = resources.getIdentifier("weather_overcast",category,packageName);
            }else if(imgCode == 3){
                weatherImagePath = resources.getIdentifier("weather_rain_shower",category,packageName);
            }else if(imgCode == 4){
                weatherImagePath = resources.getIdentifier("weather_shower",category,packageName);
            }else if(imgCode == 5){
                weatherImagePath = resources.getIdentifier("weather_hail",category,packageName);
            }else if(imgCode == 6 || imgCode == 7){
                weatherImagePath = resources.getIdentifier("weather_light_rain",category,packageName);
            }else if(imgCode == 8 || imgCode == 21 || imgCode == 22 ){
                weatherImagePath = resources.getIdentifier("weather_moderate_rain",category,packageName);
            }else if(imgCode == 9 || imgCode == 301|| imgCode == 23){
                weatherImagePath = resources.getIdentifier("weather_heavy_rain",category,packageName);
            }else if(imgCode == 10 || imgCode == 11 || imgCode == 12 || imgCode == 24 || imgCode == 25){
                weatherImagePath = resources.getIdentifier("weather_rainstorm",category,packageName);
            }else if(imgCode == 14 || imgCode == 15 || imgCode == 26){
                weatherImagePath = resources.getIdentifier("weather_light_snow",category,packageName);
            }else if(imgCode == 13 || imgCode == 16 || imgCode == 17 || imgCode == 27 || imgCode == 28 || imgCode == 302){
                weatherImagePath = resources.getIdentifier("weather_moderate_snow",category,packageName);
            }else if(imgCode == 18 || imgCode == 32 || imgCode == 49 || imgCode == 57 || imgCode == 58 ){
                weatherImagePath = resources.getIdentifier("weather_fog",category,packageName);
            }else if(imgCode == 19){
                weatherImagePath = resources.getIdentifier("weather_icerain",category,packageName);
            }else if(imgCode == 20 || imgCode == 29 || imgCode == 30 || imgCode == 31){
                weatherImagePath = resources.getIdentifier("weather_sand",category,packageName);
            }else if(imgCode >= 53 && imgCode <= 56){
                weatherImagePath = resources.getIdentifier("weather_haze",category,packageName);
            }
            return weatherImagePath;
        }
        return weatherImagePath;

    }

    public static Drawable getWeatherColor(String img){
        Resources resources = BaseApplication.getContext().getResources();
        Drawable weatherDrawable = null;
        if (!img.isEmpty()) {
            int imgCode = Integer.parseInt(img);
            if(imgCode == 0){
              //  weatherColor = resources.getColor(R.color.sunny);
                weatherDrawable = resources.getDrawable(R.drawable.city_other_sunny);
            }else if(imgCode == 1 || imgCode == 2){
                //weatherColor = resources.getColor(R.color.cloudy_overcast);
                weatherDrawable = resources.getDrawable(R.drawable.city_other_cloudy);
            }else if(imgCode >= 3 && imgCode <= 12 || imgCode == 19 || imgCode>= 21 && imgCode <= 25 || imgCode == 301){
                //weatherColor = resources.getColor(R.color.rain);
                weatherDrawable = resources.getDrawable(R.drawable.city_other_rainy);
            }else if(imgCode >= 13 && imgCode <= 17 || imgCode >= 26 && imgCode <= 28 || imgCode == 302){
               // weatherDrawable = resources.getDrawable(R.drawable.bkg_snow);
                weatherDrawable = resources.getDrawable(R.drawable.city_other_cloudy);
            }else if(imgCode == 18 || imgCode == 32 || imgCode == 49 || imgCode == 57 || imgCode == 58){
               // weatherDrawable = resources.getDrawable(R.drawable.bkg_fog);
                weatherDrawable = resources.getDrawable(R.drawable.city_other_cloudy);
            }else if(imgCode == 20 || imgCode == 29 || imgCode == 30 || imgCode == 31){
                //weatherDrawable = resources.getDrawable(R.drawable.bkg_sandstorm);
                weatherDrawable = resources.getDrawable(R.drawable.city_other_cloudy);
            }else if(imgCode >= 53 && imgCode <= 56){
               // weatherDrawable = resources.getDrawable(R.drawable.bkg_haze);
                weatherDrawable = resources.getDrawable(R.drawable.city_other_cloudy);
            }
            return weatherDrawable;
        }
        return weatherDrawable;
    }

    public static int getAirqualityColor(String airquality){
        Resources resources = BaseApplication.getContext().getResources();
        int airqualityColor = 0;
        if (!airquality.isEmpty()) {
            switch (airquality){
                case "优":
                    airqualityColor = resources.getColor(R.color.airquality_good);
                    break;
                case "良":
                    airqualityColor = resources.getColor(R.color.airquality_miderate);
                    break;
                case "轻度污染":
                    airqualityColor = resources.getColor(R.color.airquality_lightly_polltue);
                    break;
                case "中度污染":
                    airqualityColor = resources.getColor(R.color.airquality_miderate_pollute);
                    break;
                case "重度污染":
                    airqualityColor = resources.getColor(R.color.airquality_heavy_pollute);
                    break;
                case "严重污染":
                    airqualityColor = resources.getColor(R.color.airquality_deep_plooute);
                    break;
            }
            return airqualityColor;
        }
        return airqualityColor;
    }

    public static int getWeatherType(String img){

        int weatherType = 0;

        if (!img.isEmpty()) {
            int imgCode = Integer.parseInt(img);
            if(imgCode == 0) {
                weatherType = R.string.weather_view_sunny;
            } else if (imgCode == 1 || imgCode == 2) {
                weatherType = R.string.weather_view_cloudy;
            } else if (imgCode >= 3 && imgCode <= 12 || imgCode == 19 || imgCode>= 21 && imgCode <= 25 || imgCode == 301) {
               weatherType = R.string.weather_view_rainy;
            } else if (imgCode >= 13 && imgCode <= 17 || imgCode >= 26 && imgCode <= 28 || imgCode == 302) {
                weatherType = R.string.weather_view_snowy;
            } else if (imgCode == 18 || imgCode == 32 || imgCode == 49 || imgCode == 57 || imgCode == 58) {
                weatherType = R.string.weather_view_foggy;
            } else if (imgCode == 20 || imgCode == 29 || imgCode == 30 || imgCode == 31) {
                weatherType = R.string.weather_view_sand;
            } else if (imgCode >= 53 && imgCode <= 56) {
                weatherType = R.string.weather_view_hazy;
            }
            return weatherType;
        }
        return weatherType;
    }

    public static List<Integer> getSunriseSunset(Weather weather) {
        String Sunrise = weather.getInfo().getDailyList().get(0).getSunrise();
        String Sunset = weather.getInfo().getDailyList().get(0).getSunset();

        int index = Sunrise.indexOf(":");
        String SunriseBefore = Sunrise.substring(0,index);
        String SunriseAfter = Sunrise.substring(index+1);
        String SunsetBefore = Sunset.substring(0,index);
        String SunsetAfter = Sunset.substring(index+1);

        int Sunrise_h = Integer.parseInt(SunriseBefore);
        int Sunrise_m = Integer.parseInt(SunriseAfter);
        int Sunset_h = Integer.parseInt(SunsetBefore);
        int Sunset_m = Integer.parseInt(SunsetAfter);

        // list 0 - 3 分别是 日出的小时和分钟 和 日落的小时和分钟
        List<Integer> list = new ArrayList<>();
        list.add(Sunrise_h);
        list.add(Sunrise_m);
        list.add(Sunset_h);
        list.add(Sunset_m);

        return list;
    }

}
