package com.tokyonth.weather.presenter;

import com.tokyonth.weather.model.CityLoadModel;
import com.tokyonth.weather.model.CityModel;
import com.tokyonth.weather.model.CityModelImpl;


public class CityPresenterImpl implements CityPresenter {

    private CityModel cityModel;
    private LoadCitySituationListener listener;

    public CityPresenterImpl(){
        cityModel = new CityModelImpl();
        ((CityModelImpl) cityModel).setCityLoadModel(new CityLoadModel() {
            @Override
            public void loadComplete() {
                listener.Success();
            }

            @Override
            public void loadFail() {
                listener.Fail();
            }
        });
    }

    @Override
    public void saveCityList(LoadCitySituationListener listener) {
        cityModel.loadCityList();
        this.listener = listener;
    }

}
