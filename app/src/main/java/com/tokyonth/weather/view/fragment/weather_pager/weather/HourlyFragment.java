package com.tokyonth.weather.view.fragment.weather_pager.weather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.HourlyAdapter;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.view.fragment.weather_pager.base.BaseSubscribeFragment;


/**
 * Created by Administrator on 2017/8/21 0021.
 */

public class HourlyFragment extends BaseSubscribeFragment {

    private RecyclerView hourlyRv;
    private HourlyAdapter hourlyAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.pager_hourly_weather;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        hourlyRv = (RecyclerView) view.findViewById(R.id.weather_hourly_rv);
    }

    @Override
    protected void setWeather(Weather weather) {
        hourlyRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        hourlyAdapter = new HourlyAdapter(weather.getInfo().getHourlyList());
        hourlyRv.setAdapter(hourlyAdapter);
    }
}
