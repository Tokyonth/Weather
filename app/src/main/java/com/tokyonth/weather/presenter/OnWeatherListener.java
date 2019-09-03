package com.tokyonth.weather.presenter;

import com.tokyonth.weather.model.bean.Weather;

public interface OnWeatherListener {

    void loadSuccess(Weather weather);
    void loadFailure(String msg);
    void loadOffline();

}
