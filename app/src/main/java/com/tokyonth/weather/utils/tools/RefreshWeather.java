package com.tokyonth.weather.utils.tools;

import com.google.gson.Gson;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.api.RetrofitFactory;
import com.tokyonth.weather.utils.api.Api;

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

        if (!isDefaultCity) {
            new RetrofitFactory(Api.JISU_URL).getApiInterface()
                    .getWeather(Api.getJisuAppKey(), city)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Weather>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Weather weather) {
                            onRefresh.onSuccess(weather);
                        }

                        @Override
                        public void onError(Throwable e) {
                            onRefresh.onFail(e.toString());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            new RetrofitFactory(Api.JISU_URL).getApiInterface()
                    .getLocationWeather(Api.getJisuAppKey(), city)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

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
                        public void onComplete() {
                        }
                    });
        }

    }

    public interface OnRefresh {
        void onSuccess(Weather weather);

        void onFail(String error);
    }

}
