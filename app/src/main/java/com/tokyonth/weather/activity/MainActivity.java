package com.tokyonth.weather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tokyonth.weather.BaseActivity;
import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.WeatherPagerAdapter;
import com.tokyonth.weather.dynamic.BaseDrawer;
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
import com.tokyonth.weather.utils.sundry.PhoneUtil;
import com.tokyonth.weather.utils.sundry.PreferencesLoader;
import com.tokyonth.weather.utils.RefreshWeather;
import com.tokyonth.weather.utils.WeatherInfoHelper;
import com.tokyonth.weather.utils.file.FileUtil;
import com.tokyonth.weather.view.WeatherView;
import com.tokyonth.weather.view.custom.VerticalSwipeRefreshLayout;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements WeatherView {

    private WeatherPresenter weather_presenter;
    private DynamicWeatherView weatherView;
    private VerticalSwipeRefreshLayout weatherRefresh;
    private CoordinatorLayout weatherBasic;

    private String city;
    private TextView toolbar_tv_city;
    private Weather offline_weather;

    private boolean isDefaultCity = true;
    private ViewPager2 weather_page;
    private WeatherPagerAdapter weather_page_adapter;

    public LinearLayout main_ll;
    private ImageView default_city_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSplashActivity();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        initLayout();
        initView();

        if (!PhoneUtil.isLocServiceEnable(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("检测到你未打开定位功能,请打开后重试")
                    .setNegativeButton("确定", (dialog, which) -> finish());
                    builder.setCancelable(false);
                    builder.create().show();
        } else {

            String weather_data = FileUtil.getFile(FileUtil.SAVE_WEATHER_NAME);
            assert weather_data != null;
            if (!weather_data.equals("")) {
                offline_weather = new Gson().fromJson(weather_data, Weather.class);
                setWeatherBackground(offline_weather);
            //    toolbar_tv_city.setText(FileUtil.getFile(FileUtil.PRECISE_LOCATION_NAME));
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_title_more));
        setSupportActionBar(toolbar);
        setTitle(null);

        default_city_iv = (ImageView) findViewById(R.id.default_city_iv);
        main_ll = (LinearLayout) findViewById(R.id.main_ll);
        toolbar_tv_city = (TextView) findViewById(R.id.weather_city_name_tv);
        weatherBasic = (CoordinatorLayout) findViewById(R.id.weather_basic_coor);
        weatherRefresh = (VerticalSwipeRefreshLayout) findViewById(R.id.refresh_city);
        weatherView = (DynamicWeatherView) findViewById(R.id.weather_weatherview_container_rl);
        weather_page = (ViewPager2) findViewById(R.id.viewpager2);
        weather_page.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        weather_page.setAdapter(weather_page_adapter);
        weather_page.setOffscreenPageLimit(2);
        weather_page.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //Log.d("2.page滑动---->", position + "");
                if (position == 1) {
                    weatherRefresh.setEnabled(false);
                } else if (position == 0) {
                    weatherRefresh.setEnabled(true);
                }
            }

        });

        weatherRefresh.setOnRefreshListener(this::WeatherRefresh);
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        weatherView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        weatherView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        weatherView.onDestroy();
    }

    @Override
    public void showWeather(Weather weather) {
        setWeatherBackground(weather);
        EventBus.getDefault().post(weather);
        toolbar_tv_city.setText(DataSupport.find(DefaultCity.class,1).getCityName());
        String cityName = weather.getInfo().getCityName();
        List<SavedCity> savedCityList = DataSupport.findAll(SavedCity.class);
        for(SavedCity savedCity : savedCityList) {
            if (savedCity.getCityName().equals(cityName)) {
                toolbar_tv_city.setText(cityName);
                break;
            }
        }
        //toolbar_tv_city.setText(FileUtil.getFile(FileUtil.PRECISE_LOCATION_NAME));
    }

    @Override
    public void showErrorInfo(String error) {
        Snackbar.make(weatherBasic,error + "已为你加载上一次天气信息",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showOffLine() {
        EventBus.getDefault().post(offline_weather);
    }

    public void setWeatherBackground(Weather weather) {

        String img = weather.getInfo().getImg();
        int weatherType = WeatherInfoHelper.getWeatherType(img);
        // or Time t=new Time("GMT+8"); 加上Time Zone资料
        Time time = new Time();
        time.setToNow(); // 取得
        int hour = time.hour;

        if (hour <= 18) {
            switch (weatherType) {
                case R.string.weatherview_sunny:
                    weatherView.setDrawerType(BaseDrawer.Type.CLEAR_D);
                    break;
                case R.string.weatherview_cloudy:
                    weatherView.setDrawerType(BaseDrawer.Type.CLOUDY_D);
                    break;
                case R.string.weatherview_rainy:
                    weatherView.setDrawerType(BaseDrawer.Type.RAIN_D);
                    break;
                case R.string.weatherview_snowy:
                    weatherView.setDrawerType(BaseDrawer.Type.SNOW_D);
                    break;
                case R.string.weatherview_foggy:
                    weatherView.setDrawerType(BaseDrawer.Type.FOG_D);
                    break;
                case R.string.weatherview_sand:
                    weatherView.setDrawerType(BaseDrawer.Type.SAND_D);
                    break;
                case R.string.weatherview_hazy:
                    weatherView.setDrawerType(BaseDrawer.Type.HAZE_D);
                    break;
            }
        } else {
            switch (weatherType) {
                case R.string.weatherview_sunny:
                    weatherView.setDrawerType(BaseDrawer.Type.CLEAR_N);
                    break;
                case R.string.weatherview_cloudy:
                    weatherView.setDrawerType(BaseDrawer.Type.CLOUDY_N);
                    break;
                case R.string.weatherview_rainy:
                    weatherView.setDrawerType(BaseDrawer.Type.RAIN_N);
                    break;
                case R.string.weatherview_snowy:
                    weatherView.setDrawerType(BaseDrawer.Type.SNOW_N);
                    break;
                case R.string.weatherview_foggy:
                    weatherView.setDrawerType(BaseDrawer.Type.FOG_N);
                    break;
                case R.string.weatherview_sand:
                    weatherView.setDrawerType(BaseDrawer.Type.SAND_N);
                    break;
                case R.string.weatherview_hazy:
                    weatherView.setDrawerType(BaseDrawer.Type.HAZE_N);
                    break;
            }

        }
        //PreferencesLoader.putInt(PreferencesLoader.WEATHER_COLOR, weatherColor);
    }

    private void WeatherRefresh() {
        new RefreshWeather(isDefaultCity, city).setOnRefresh(new RefreshWeather.OnRefresh() {
            @Override
            public void onSuccess(Weather weather) {
                setWeatherBackground(weather);
                EventBus.getDefault().post(weather);
                Snackbar.make(weatherBasic,"刷新成功!",Snackbar.LENGTH_LONG).show();
                weatherRefresh.setRefreshing(false);
            }

            @Override
            public void onFail(String error) {
                Snackbar.make(weatherBasic,"刷新失败!" + error,Snackbar.LENGTH_LONG).show();
                weatherRefresh.setRefreshing(false);
            }
        });
    }

}
