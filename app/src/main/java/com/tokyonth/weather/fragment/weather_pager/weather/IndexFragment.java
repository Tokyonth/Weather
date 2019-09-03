package com.tokyonth.weather.view.fragment.weather_pager.weather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.IndexAdapter;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.view.fragment.weather_pager.base.BaseSubscribeFragment;


/**
 * Created by Administrator on 2017/8/21 0021.
 */

public class IndexFragment extends BaseSubscribeFragment {

    private RecyclerView indexRv;
    private IndexAdapter indexAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.pager_index_weather;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        indexRv = (RecyclerView) view.findViewById(R.id.weather_index_rv);
    }

    @Override
    protected void setWeather(Weather weather) {
        indexRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        indexAdapter = new IndexAdapter(weather.getInfo().getIndexList());
        indexRv.setAdapter(indexAdapter);
    }
}
