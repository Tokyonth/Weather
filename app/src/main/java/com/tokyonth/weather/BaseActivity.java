package com.tokyonth.weather;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.utils.WeatherSettingsHelper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean weather_notification = (boolean)SPUtils.getData("switch_notification_weather", false);
        WeatherSettingsHelper.setWeatherNotification(this, weather_notification);


    }


}
