package com.tokyonth.weather.utils;

import com.google.gson.Gson;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.api.Api;
import com.tokyonth.weather.utils.network.NetworkUtil;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class RefreshWeather {

    private OnRefresh onRefresh;

    public void setOnRefresh(OnRefresh onRefresh) {
        this.onRefresh = onRefresh;
    }

    public RefreshWeather(boolean isDefaultCity, String city) {
        if (NetworkUtil.isWifiConnected() || NetworkUtil.isMobileConnected()) {
            if (!isDefaultCity) {
                new RetrofitFactory(Api.JISU_URL).getApiInterface()
                        .getWeather(Api.JISU_APP_KEY, city)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Weather>() {
                            @Override
                            public void onSubscribe(Disposable d) { }
                            @Override
                            public void onNext(Weather weather) {
                                onRefresh.onSuccess(weather);
                            }
                            @Override
                            public void onError(Throwable e) {
                                onRefresh.onFail(e.toString());
                            }
                            @Override
                            public void onComplete() { }
                        });
            } else {
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
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    onRefresh.onFail(e.toString());
                                }
                                Weather weather = new Gson().fromJson(jsonStr, Weather.class);
                                onRefresh.onSuccess(weather);
                            }
                            @Override
                            public void onError(Throwable e) {
                                onRefresh.onFail(e.toString());
                            }
                            @Override
                            public void onComplete() { }
                        });
            }

        } else {
            if (onRefresh != null) {
                onRefresh.onFail("网络未连接!");
            }
        }
    }

    public interface OnRefresh {
        void onSuccess(Weather weather);
        void onFail(String error);
    }

}
