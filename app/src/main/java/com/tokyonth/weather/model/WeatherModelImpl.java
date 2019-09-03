package com.tokyonth.weather.model;

import com.google.gson.Gson;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.OnWeatherListener;
import com.tokyonth.weather.util.Api;
import com.tokyonth.weather.util.NetworkUtil;
import com.tokyonth.weather.util.PreferencesLoader;
import com.tokyonth.weather.util.RetrofitFactory;
import com.tokyonth.weather.util.data.FileUtil;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class WeatherModelImpl implements WeatherModel {

    @Override
    public void loadCityWeather(SavedCity savedCity, final OnWeatherListener listener) {
        if (NetworkUtil.isWifiConnected() || NetworkUtil.isMobileConnected()) {
            new RetrofitFactory(Api.JISU_URL).getApiInterface()
                    .getWeather(Api.JISU_APP_KEY,savedCity.getCityCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Weather>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }
                        @Override
                        public void onNext(Weather weather) {
                            listener.loadSuccess(weather);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            listener.loadFailure("网络访问错误，请检查网络连接是否正常。");
        }
    }

    @Override
    public void loadLocationWeather(DefaultCity defaultCity, final OnWeatherListener listener) {
        listener.loadOffline();
      //  String locationInfo = defaultCity.getLatitude() + "," + defaultCity.getLongitude();
        String city = defaultCity.getCityName();
        if (NetworkUtil.isWifiConnected() || NetworkUtil.isMobileConnected()) {
          //  Log.d("地区----------->", city);
            new RetrofitFactory(Api.JISU_URL).getApiInterface()
                    .getLocationWeather(Api.JISU_APP_KEY, city)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) { }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            String jsonStr = null;
                            try {
                                jsonStr = new String(responseBody.bytes());
                                FileUtil.saveFile(jsonStr, "save_weather.json");
                              //  Log.d("----------->", jsonStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Weather weather = new Gson().fromJson(jsonStr, Weather.class);

                            int temp = Integer.valueOf(weather.getInfo().getTemp());
                            int img = Integer.valueOf(weather.getInfo().getImg());
                            PreferencesLoader.putInt(PreferencesLoader.DEFAULT_CITY_TEMP,temp);
                            PreferencesLoader.putInt(PreferencesLoader.DEFAULT_CITY_IMG,img);
                            listener.loadSuccess(weather);
                        }

                        @Override
                        public void onError(Throwable e) { }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            listener.loadFailure("网络访问错误，请检查网络连接是否正常。");
        }

    }

}
