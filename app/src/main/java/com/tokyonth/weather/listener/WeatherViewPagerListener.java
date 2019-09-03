package com.tokyonth.weather.listener;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.tokyonth.weather.activity.MainActivity;

public class WeatherViewPagerListener implements ViewPager.OnPageChangeListener {

    private MainActivity activity;

    public WeatherViewPagerListener (MainActivity activity) {
        this.activity = activity;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position > 0) {
          //  activity.toStartIb.setVisibility(View.VISIBLE);
        }else {
           // activity.toStartIb.setVisibility(View.INVISIBLE);
        }
        if (position == activity.fragmentList.size() - 2 || position == activity.fragmentList.size() - 1) {
          //  activity.toEndIb.setVisibility(View.INVISIBLE);
        }else {
          //  activity.toEndIb.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
