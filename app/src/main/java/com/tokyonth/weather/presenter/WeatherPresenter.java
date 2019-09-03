package com.tokyonth.weather.presenter;

import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;

public interface WeatherPresenter {

    void getWeather(SavedCity savedCity);
    void getLocationWeather(DefaultCity defaultCity);

}
