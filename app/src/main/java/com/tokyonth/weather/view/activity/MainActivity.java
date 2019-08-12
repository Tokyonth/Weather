package com.tokyonth.weather.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.WeatherPagerAdapter;
import com.tokyonth.weather.listener.WeatherViewPagerListener;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.LocationPresenter;
import com.tokyonth.weather.presenter.LocationPresenterImpl;
import com.tokyonth.weather.presenter.WeatherPresenter;
import com.tokyonth.weather.presenter.WeatherPresenterImpl;
import com.tokyonth.weather.util.Api;
import com.tokyonth.weather.util.DateUtil;
import com.tokyonth.weather.util.NetworkUtil;
import com.tokyonth.weather.util.PreferencesLoader;
import com.tokyonth.weather.util.RetrofitFactory;
import com.tokyonth.weather.util.WeatherInfoHelper;
import com.tokyonth.weather.util.data.FileUtil;
import com.tokyonth.weather.util.weatherview.cloudy.CloudView;
import com.tokyonth.weather.util.weatherview.foggy.FoggyView;
import com.tokyonth.weather.util.weatherview.haze.HazeView;
import com.tokyonth.weather.util.weatherview.rainy.RainView;
import com.tokyonth.weather.util.weatherview.sand.SandView;
import com.tokyonth.weather.util.weatherview.snow.SnowView;
import com.tokyonth.weather.util.weatherview.sunny.LeafView;
import com.tokyonth.weather.view.WeatherView;
import com.tokyonth.weather.view.fragment.weather_pager.weather.AqiFragment;
import com.tokyonth.weather.view.fragment.weather_pager.weather.DailyFragment;
import com.tokyonth.weather.view.fragment.weather_pager.weather.HourlyFragment;
import com.tokyonth.weather.view.fragment.weather_pager.weather.IndexFragment;
import com.tokyonth.weather.view.fragment.weather_pager.weather.SummaryFragment;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements WeatherView {

    private static final String TAG = "MainActivity";
    
    private boolean isFirst;
    private LocationPresenter location_presenter;
    private WeatherPresenter weather_presenter;
    private RelativeLayout weatherviewContainerRl;
    private ViewPager weatherViewPager;
    private WeatherPagerAdapter adapter;
    private Toolbar toolbar;
    private SwipeRefreshLayout weather_refresh;
    private CoordinatorLayout weather_basic_coor;

    public List<Fragment> fragmentList;
    public ImageButton toStartIb, toEndIb;

    private String city_code;
    private String city_coordinate;
    private TextView toolbar_tv_city;
    private Weather offline_weather;

    private boolean isDefaultCity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSplashActivity();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        initLayout();
        initView();

        if (!isLocServiceEnable(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("检测到你未打开定位功能,请打开后重试")
                    .setNegativeButton("确定", (dialog, which) -> finish());
                    builder.setCancelable(false);
                    builder.create().show();
        } else {

            String weather_data = FileUtil.getFile(FileUtil.SAVE_WEATHER_NAME);
            if (!weather_data.equals("")) {
                offline_weather = new Gson().fromJson(weather_data, Weather.class);
                setWeatherBackground(offline_weather);
                toolbar_tv_city.setText(FileUtil.getFile(FileUtil.PRECISE_LOCATION_NAME));
            }
            weather_presenter = new WeatherPresenterImpl(this);
            location_presenter = new LocationPresenterImpl();
            location_presenter.loadLocation(this);
        }
    }

    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    private void initLayout() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        SummaryFragment dwf = new SummaryFragment();
        HourlyFragment hourlyFragment = new HourlyFragment();
        DailyFragment dailyFragment = new DailyFragment();
        AqiFragment aqiFragment = new AqiFragment();
        IndexFragment indexFragment = new IndexFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(dwf);
        fragmentList.add(hourlyFragment);
        fragmentList.add(dailyFragment);
        fragmentList.add(aqiFragment);
        fragmentList.add(indexFragment);
        adapter = new WeatherPagerAdapter(getSupportFragmentManager(),fragmentList);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        toolbar_tv_city = (TextView) findViewById(R.id.toolbar_tv_city);
        weather_basic_coor = (CoordinatorLayout) findViewById(R.id.weather_basic_coor);

        weather_refresh = (SwipeRefreshLayout) findViewById(R.id.refresh_city);
        weatherviewContainerRl = (RelativeLayout) findViewById(R.id.weather_weatherview_container_rl);
        weatherViewPager = (ViewPager) findViewById(R.id.weather_view_pager);
        weatherViewPager.setAdapter(adapter);
        weatherViewPager.setOffscreenPageLimit(5);
        toStartIb = (ImageButton) findViewById(R.id.weather_to_start_ib);
        toStartIb.setOnClickListener(v -> weatherViewPager.setCurrentItem(0));
        toEndIb = (ImageButton) findViewById(R.id.weather_to_end_ib);
        toEndIb.setOnClickListener(v -> weatherViewPager.setCurrentItem(fragmentList.size()-1));
        if (weatherViewPager.getCurrentItem() == 0) {
            toStartIb.setVisibility(View.INVISIBLE);
        }
        weatherViewPager.addOnPageChangeListener(new WeatherViewPagerListener(this));

        weather_refresh.setOnRefreshListener(this::WeatherRefresh);
    }

    private void startSplashActivity() {
        isFirst = PreferencesLoader.getBoolean(PreferencesLoader.IMPORT_DATA,true);
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
            case R.id.action_shard:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void getLocation(DefaultCity defaultCity){
        weather_presenter.getLocationWeather(defaultCity);
       // city_coordinate = defaultCity.getLatitude() + "," + defaultCity.getLongitude();
        city_coordinate = defaultCity.getCityName();
        isDefaultCity = true;
    }

    @Subscribe
    public void getCity(SavedCity savedCity){
        weather_presenter.getWeather(savedCity);
        city_code = savedCity.getCityCode();
        isDefaultCity = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("Weather");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("Weather");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showWeather(Weather weather) {
        setWeatherBackground(weather);
        EventBus.getDefault().post(weather);
        toolbar_tv_city.setText(FileUtil.getFile(FileUtil.PRECISE_LOCATION_NAME));
    }

    @Override
    public void showErrorInfo(String error) {
        Snackbar.make(weather_basic_coor,error + "已为你加载上一次天气信息",Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showOffLine() {
        EventBus.getDefault().post(offline_weather);
    }

    private void setWeatherBackground(Weather weather) {
        String img = weather.getInfo().getImg();
        int weatherColor = WeatherInfoHelper.getWeatherColor(img);
        int weatherType = WeatherInfoHelper.getWeatherType(img);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        weatherviewContainerRl.removeAllViews();
          switch (weatherType){
            case R.string.weatherview_sunny:
                LeafView leafView = new LeafView(this);
                leafView.setBackgroundColor(weatherColor);
                weatherviewContainerRl.addView(leafView,params);
                break;
            case R.string.weatherview_cloudy:
                CloudView cloudView = new CloudView(this);
                cloudView.setBackgroundColor(weatherColor);
                weatherviewContainerRl.addView(cloudView,params);
                break;
            case R.string.weatherview_rainy:
                RainView rainView = new RainView(this);
                rainView.setBackgroundColor(weatherColor);
                weatherviewContainerRl.addView(rainView,params);
                break;
            case R.string.weatherview_snowy:
                SnowView snowView = new SnowView(this);
                snowView.setBackgroundColor(weatherColor);
                weatherviewContainerRl.addView(snowView,params);
                break;
            case R.string.weatherview_foggy:
                FoggyView foggyView = new FoggyView(this);
                foggyView.setBackgroundColor(weatherColor);
                weatherviewContainerRl.addView(foggyView,params);
                break;
            case R.string.weatherview_sand:
                SandView sandView = new SandView(this);
                sandView.setBackgroundColor(weatherColor);
                weatherviewContainerRl.addView(sandView,params);
                break;
            case R.string.weatherview_hazy:
                HazeView hazeView = new HazeView(this);
                hazeView.setBackgroundColor(weatherColor);
                weatherviewContainerRl.addView(hazeView,params);
                break;
        }
        PreferencesLoader.putInt(PreferencesLoader.WEATHER_COLOR, weatherColor);
    }

    private void WeatherRefresh() {
        if (NetworkUtil.isWifiConnected() || NetworkUtil.isMobileConnected()) {
            if (!isDefaultCity) {
                new RetrofitFactory(Api.JISU_URL).getApiInterface()
                        .getWeather(Api.JISU_APP_KEY, city_code)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Weather>() {
                            @Override
                            public void onSubscribe(Disposable d) { }
                            @Override
                            public void onNext(Weather weather) {
                                weather_refresh.setRefreshing(false);
                                Snackbar.make(weather_basic_coor,"刷新成功!",Snackbar.LENGTH_LONG).show();
                                setWeatherBackground(weather);
                                EventBus.getDefault().post(weather);
                                toolbar_tv_city.setText(FileUtil.getFile(FileUtil.PRECISE_LOCATION_NAME));
                            }
                            @Override
                            public void onError(Throwable e) { }
                            @Override
                            public void onComplete() { }
                        });
            } else {
                new RetrofitFactory(Api.JISU_URL).getApiInterface()
                        .getLocationWeather(Api.JISU_APP_KEY, city_coordinate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) { }
                            @Override
                            public void onNext(ResponseBody responseBody) {
                                weather_refresh.setRefreshing(false);
                                Snackbar.make(weather_basic_coor,"刷新成功!",Snackbar.LENGTH_LONG).show();
                                String jsonStr = null;
                                try {
                                    jsonStr = new String(responseBody.bytes());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Weather weather = new Gson().fromJson(jsonStr, Weather.class);
                                setWeatherBackground(weather);
                                EventBus.getDefault().post(weather);
                                toolbar_tv_city.setText(FileUtil.getFile(FileUtil.PRECISE_LOCATION_NAME));
                            }
                            @Override
                            public void onError(Throwable e) { }
                            @Override
                            public void onComplete() { }
                        });
            }
            toolbar_tv_city.setText(FileUtil.getFile(FileUtil.PRECISE_LOCATION_NAME));

        } else {
            Snackbar.make(weather_basic_coor,"刷新失败!请检测网络连接!",Snackbar.LENGTH_LONG).show();
            weather_refresh.setRefreshing(false);
        }

    }

}
