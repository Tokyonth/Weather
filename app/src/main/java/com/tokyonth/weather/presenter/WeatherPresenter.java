package com.tokyonth.weather.presenter;

import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;

/**
 * Created by Administrator on 2017/8/21 0021.
 */

public interface WeatherPresenter {

    void getWeather(SavedCity savedCity);

    void getLocationWeather(DefaultCity defaultCity);

}
