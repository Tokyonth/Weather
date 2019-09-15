package com.tokyonth.weather.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class WeatherPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> list_fragment;

    public WeatherPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Fragment> list_fragment) {
        super(fragmentManager, lifecycle);
        this.list_fragment = list_fragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return list_fragment.get(position);
    }

    @Override
    public int getItemCount() {
        return list_fragment.size();
    }

}

