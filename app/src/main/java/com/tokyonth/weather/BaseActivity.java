package com.tokyonth.weather;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tokyonth.weather.utils.tools.SPUtils;
import com.tokyonth.weather.utils.helper.WeatherSettingsHelper;

import org.greenrobot.eventbus.EventBus;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initSettings();
    }

    private void initSettings() {
        boolean weather_notification = (boolean)SPUtils.getData("switch_notification_weather", false);
        WeatherSettingsHelper.setWeatherNotification(this, weather_notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
