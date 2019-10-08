package com.tokyonth.weather.assembly;

import com.tokyonth.weather.R;
import com.tokyonth.weather.dynamic.BaseDrawer;

public class WeatherType {

    public static BaseDrawer.Type getType(boolean bool, int weatherType) {

        BaseDrawer.Type re_type;

        if (bool) {
            switch (weatherType) {
                case R.string.weather_view_sunny:
                    re_type = BaseDrawer.Type.CLEAR_D;
                    break;
                case R.string.weather_view_cloudy:
                    re_type = BaseDrawer.Type.CLOUDY_D;
                    break;
                case R.string.weather_view_rainy:
                    re_type = BaseDrawer.Type.RAIN_D;
                    break;
                case R.string.weather_view_snowy:
                    re_type = BaseDrawer.Type.SNOW_D;
                    break;
                case R.string.weather_view_foggy:
                    re_type = BaseDrawer.Type.FOG_D;
                    break;
                case R.string.weather_view_sand:
                    re_type = BaseDrawer.Type.SAND_D;
                    break;
                case R.string.weather_view_hazy:
                    re_type = BaseDrawer.Type.HAZE_D;
                    break;
                default:
                    re_type = BaseDrawer.Type.DEFAULT;
                    break;
            }
        } else {
            switch (weatherType) {
                case R.string.weather_view_sunny:
                    re_type = BaseDrawer.Type.CLEAR_N;
                    break;
                case R.string.weather_view_cloudy:
                    re_type = BaseDrawer.Type.CLOUDY_N;
                    break;
                case R.string.weather_view_rainy:
                    re_type = BaseDrawer.Type.RAIN_N;
                    break;
                case R.string.weather_view_snowy:
                    re_type = BaseDrawer.Type.SNOW_N;
                    break;
                case R.string.weather_view_foggy:
                    re_type = BaseDrawer.Type.FOG_N;
                    break;
                case R.string.weather_view_sand:
                    re_type = BaseDrawer.Type.SAND_N;
                    break;
                case R.string.weather_view_hazy:
                    re_type = BaseDrawer.Type.HAZE_N;
                    break;
                default:
                    re_type = BaseDrawer.Type.DEFAULT;
                    break;
            }
        }
        return re_type;
    }

}
