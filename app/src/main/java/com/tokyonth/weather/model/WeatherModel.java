package com.tokyonth.weather.model;

import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.presenter.OnWeatherListener;

public interface WeatherModel {

    void loadCityWeather(SavedCity savedCity, OnWeatherListener listener);
    void loadLocationWeather(DefaultCity defaultCity, OnWeatherListener listener);

}
