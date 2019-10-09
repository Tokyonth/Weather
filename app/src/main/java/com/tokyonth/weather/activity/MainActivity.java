package com.tokyonth.weather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tokyonth.weather.BaseActivity;
import com.tokyonth.weather.R;
import com.tokyonth.weather.assembly.WeatherType;
import com.tokyonth.weather.adapter.WeatherPagerAdapter;
import com.tokyonth.weather.dynamic.DynamicWeatherView;
import com.tokyonth.weather.entirety.FragmentLifecycle;
import com.tokyonth.weather.fragment.WeatherPageBrief;
import com.tokyonth.weather.fragment.WeatherPageDetailed;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.LocationPresenter;
import com.tokyonth.weather.presenter.LocationPresenterImpl;
import com.tokyonth.weather.presenter.WeatherPresenter;
import com.tokyonth.weather.presenter.WeatherPresenterImpl;
import com.tokyonth.weather.utils.network.NetworkUtil;
import com.tokyonth.weather.utils.sundry.DateUtil;
import com.tokyonth.weather.utils.sundry.PhoneUtil;
import com.tokyonth.weather.utils.sundry.PreferencesLoader;
import com.tokyonth.weather.utils.tools.RefreshWeather;
import com.tokyonth.weather.utils.helper.WeatherInfoHelper;
import com.tokyonth.weather.utils.file.FileUtil;
import com.tokyonth.weather.presenter.WeatherView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements WeatherView {

    private WeatherPresenter weather_presenter;
    private DynamicWeatherView dynamic_weatherView;
    private SwipeRefreshLayout weather_refresh;

    private TextView toolbar_tv_city;
    private ImageView default_city_iv;
    private WeatherPagerAdapter weather_page_adapter;

    private boolean isDefaultCity = true;
    private Weather offline_weather;
    private String city;

    public LinearLayout weather_basic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSplashActivity();
        setContentView(R.layout.activity_main);
        initLayout();
        initView();
        if (!PhoneUtil.isLocServiceEnable(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_text_title))
                    .setMessage(getResources().getString(R.string.not_opened_location))
                    .setNegativeButton(getResources().getString(R.string.btn_ok), (dialog, which) -> finish());
                    builder.setCancelable(false);
                    builder.create().show();
        } else {
            String weather_data = FileUtil.getFile(FileUtil.SAVE_WEATHER_NAME);
            assert weather_data != null;
            if (!weather_data.equals("")) {
                offline_weather = new Gson().fromJson(weather_data, Weather.class);
               // EventBus.getDefault().post(offline_weather);
                setWeatherBackground(offline_weather);
                weather_refresh.setRefreshing(true);
            }
            weather_presenter = new WeatherPresenterImpl(this);
            LocationPresenter location_presenter = new LocationPresenterImpl();
            location_presenter.loadLocation(this);
        }
    }

    private void initLayout() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        WeatherPageBrief weatherPageBrief = new WeatherPageBrief();
        WeatherPageDetailed weatherPageDetailed = new WeatherPageDetailed();
        List<Fragment> page_list = new ArrayList<>();
        page_list.add(weatherPageBrief);
        page_list.add(weatherPageDetailed);
        weather_page_adapter = new WeatherPagerAdapter(getSupportFragmentManager(), new FragmentLifecycle(), page_list);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_title_more));
        setSupportActionBar(toolbar);
        setTitle(null);

        default_city_iv = findViewById(R.id.default_city_iv);
        toolbar_tv_city = findViewById(R.id.weather_city_name_tv);
        weather_basic = findViewById(R.id.main_ll);
        weather_refresh = findViewById(R.id.refresh_city);
        dynamic_weatherView = findViewById(R.id.dynamic_weather_view);
        ViewPager2 weather_pages = findViewById(R.id.viewpager2);
        weather_pages.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        weather_pages.setAdapter(weather_page_adapter);
        weather_pages.setOffscreenPageLimit(2);
        weather_pages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {
                    weather_refresh.setEnabled(false);
                } else if (position == 0) {
                    weather_refresh.setEnabled(true);
                }
            }

        });

        weather_refresh.setOnRefreshListener(this::WeatherRefresh);
    }

    private void startSplashActivity() {
        boolean isFirst = PreferencesLoader.getBoolean(PreferencesLoader.IMPORT_DATA, true);
        if (isFirst) {
            Intent splashIntent = new Intent(this,SplashActivity.class);
            startActivity(splashIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.action_city:
                intent.setClass(MainActivity.this, CityActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                intent.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_warning:
                intent.setClass(MainActivity.this, WarningActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void getLocation(DefaultCity defaultCity){
        weather_presenter.getLocationWeather(defaultCity);
        city = defaultCity.getCityName();
        isDefaultCity = true;
        default_city_iv.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void getCity(SavedCity savedCity){
        weather_presenter.getWeather(savedCity);
        city = savedCity.getCityCode();
        isDefaultCity = false;
        default_city_iv.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dynamic_weatherView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dynamic_weatherView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dynamic_weatherView.onDestroy();
    }

    @Override
    public void showWeather(Weather weather) {
        setWeatherBackground(weather);
        EventBus.getDefault().post(weather);
        // 至少加载1.5秒再关闭
        new Handler().postDelayed(() -> weather_refresh.setRefreshing(false), 1500);

        toolbar_tv_city.setText(DataSupport.find(DefaultCity.class,1).getCityName());
        String cityName = weather.getInfo().getCityName();
        List<SavedCity> savedCityList = DataSupport.findAll(SavedCity.class);
        for(SavedCity savedCity : savedCityList) {
            if (savedCity.getCityName().equals(cityName)) {
                toolbar_tv_city.setText(cityName);
                break;
            }
        }
    }

    @Override
    public void showErrorInfo(String error) {
        Snackbar.make(weather_basic,error + getResources().getString(R.string.load_last_time_msg),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showOffLine() {
        EventBus.getDefault().post(offline_weather);
    }

    public void setWeatherBackground(Weather weather) {
        String img = weather.getInfo().getImg();
        int weatherType = WeatherInfoHelper.getWeatherType(img);

        List<Integer> list = WeatherInfoHelper.getSunriseSunset(weather);
        boolean isInTime = DateUtil.isCurrentInTimeScope(list.get(0), list.get(1), list.get(2), list.get(3));

        dynamic_weatherView.setDrawerType(WeatherType.getType(isInTime, weatherType));

    }

    private void WeatherRefresh() {
        if (NetworkUtil.isWifiConnected() || NetworkUtil.isMobileConnected()) {
            new RefreshWeather(isDefaultCity, city).setOnRefresh(new RefreshWeather.OnRefresh() {
                @Override
                public void onSuccess(Weather weather) {
                    setWeatherBackground(weather);
                    EventBus.getDefault().post(weather);
                    Snackbar.make(weather_basic,getResources().getString(R.string.refresh_success),Snackbar.LENGTH_LONG).show();
                    weather_refresh.setRefreshing(false);
                }

                @Override
                public void onFail(String error) {
                    Snackbar.make(weather_basic,getResources().getString(R.string.refresh_failed) + error,Snackbar.LENGTH_LONG).show();
                    weather_refresh.setRefreshing(false);
                }
            });
        } else {
            Snackbar.make(weather_basic,"网络未连接!",Snackbar.LENGTH_LONG).show();
            weather_refresh.setRefreshing(false);
        }

    }

}
