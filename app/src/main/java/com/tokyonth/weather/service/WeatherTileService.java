package com.tokyonth.weather.service;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.WeatherModel;
import com.tokyonth.weather.model.WeatherModelImpl;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.OnWeatherListener;
import com.tokyonth.weather.util.WeatherInfoHelper;
import com.tokyonth.weather.activity.MainActivity;

import org.litepal.crud.DataSupport;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WeatherTileService extends TileService implements OnWeatherListener {

    @Override
    public void onStartListening() {
        DefaultCity defaultCity = DataSupport.find(DefaultCity.class,1);
        if(defaultCity != null){
            WeatherModel weatherModel = new WeatherModelImpl();
            weatherModel.loadLocationWeather(defaultCity,this);
        }else {
            Toast.makeText(this, "默认城市不存在！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(intent);
    }

    @Override
    public void loadSuccess(Weather weather) {
        int weatherIconPath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        Icon icon = Icon.createWithResource(this,weatherIconPath);
        String temp = weather.getInfo().getTemp() + "℃";
        getQsTile().setState(Tile.STATE_INACTIVE);
        getQsTile().setIcon(icon);
        getQsTile().setLabel(temp);
        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void loadFailure(String msg) {
        Icon icon = Icon.createWithResource(this, R.drawable.weather_nothing);
        String info = "无";
        getQsTile().setState(Tile.STATE_INACTIVE);
        getQsTile().setIcon(icon);
        getQsTile().setLabel(info);
        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void loadOffline() {

    }

}
