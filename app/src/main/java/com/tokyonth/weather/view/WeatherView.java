package com.tokyonth.weather.view;

import com.tokyonth.weather.model.bean.Weather;

public interface WeatherView {
    void showWeather(Weather weather);
    void showErrorInfo(String error);
    void showOffLine();
}
