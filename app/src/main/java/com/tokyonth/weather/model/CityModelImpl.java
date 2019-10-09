package com.tokyonth.weather.model;

import com.tokyonth.weather.model.bean.CityList;
import com.tokyonth.weather.utils.api.Api;
import com.tokyonth.weather.utils.api.RetrofitFactory;

import org.litepal.crud.DataSupport;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CityModelImpl implements CityModel {

    private static final String TAG = "CityModelImpl";
    private CityLoadModel cityLoadModel;

    public void setCityLoadModel(CityLoadModel cityLoadModel) {
        this.cityLoadModel = cityLoadModel;
    }

    @Override
    public void loadCityList() {

        new RetrofitFactory(Api.JISU_URL).getApiInterface()
                .getCityList(Api.getJisuAppKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CityList>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(CityList cityList) {
                        DataSupport.saveAll(cityList.getCityList());
                        cityLoadModel.loadComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        cityLoadModel.loadFail();
                    }

                    @Override
                    public void onComplete() { }
                });
    }

}
