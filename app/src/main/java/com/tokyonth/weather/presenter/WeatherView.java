package com.tokyonth.weather.presenter;

import com.tokyonth.weather.model.bean.Weather;

public interface WeatherView {

    void showWeather(Weather weather);
    void showErrorInfo(String error);
    void showOffLine();

}
