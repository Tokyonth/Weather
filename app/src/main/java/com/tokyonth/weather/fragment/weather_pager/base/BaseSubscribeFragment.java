package com.tokyonth.weather.fragment.weather_pager.base;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.DailyDayAdapter;
import com.tokyonth.weather.adapter.DailyNightAdapter;
import com.tokyonth.weather.adapter.HourlyAdapter;
import com.tokyonth.weather.adapter.IndexAdapter;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.util.EnglishTextView;
import com.tokyonth.weather.util.WeatherInfoHelper;
import com.tokyonth.weather.view.linechart.LLineChartView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BaseSubscribeFragment extends Fragment {

    protected Activity mActivity;
    private EnglishTextView updateTimeTv ,tempTv , tempMaxMinTv , humidityTv ,sunTv;
    private TextView forecastHourlyTv,forecastDayTv , airQualityTv, windTv, weatherTextTv  ;
    private ImageView airqualityIv , weatherTextIv;

    private RecyclerView hourlyRv;
    private HourlyAdapter hourlyAdapter;
 //   private LLineChartView lineChart;

    private TextView levelTv , primaryPolluteTv , affectTv , pm25Tv , pm10Tv;
//    private EnglishTextView updateTimeTv0;

    private boolean isShownBack;
    private RecyclerView dailyDayRv , dailyNightRv;
    private DailyDayAdapter dailyDayAdapter;
    private DailyNightAdapter dailyNightAdapter;
    private RelativeLayout swtciRl;
    private FrameLayout dailyDayFl , dailyNightFl;
    private TextView titleTv, day_title_tv;
    private Switch dayNightSwitch;

    private RecyclerView indexRv;
    private IndexAdapter indexAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_default_weather,container,false);
       // initView(view,savedInstanceState);
       // lineChart = (LLineChartView) view.findViewById(R.id.line_chart);
        hourlyRv = (RecyclerView) view.findViewById(R.id.weather_hourly_rv);
        updateTimeTv = (EnglishTextView) view.findViewById(R.id.weather_update_time_tv);
        airQualityTv = (TextView) view.findViewById(R.id.weather_airquality_tv);
        forecastHourlyTv = (TextView) view.findViewById(R.id.weather_forecast_hourly_tips_tv);
        forecastDayTv = (TextView) view.findViewById(R.id.weather_forecast_day_tips_tv);
        windTv = (TextView) view.findViewById(R.id.weather_wind_tv);
        weatherTextTv = (TextView) view.findViewById(R.id.weather_weather_text_tv);
        humidityTv = (EnglishTextView) view.findViewById(R.id.weather_humidity_tv);
        tempTv = (EnglishTextView) view.findViewById(R.id.weather_temp_tv);
        tempMaxMinTv = (EnglishTextView) view.findViewById(R.id.weather_temp_max_min_tv);
        sunTv = (EnglishTextView) view.findViewById(R.id.weather_sun_tv);
        airqualityIv = (ImageView) view.findViewById(R.id.weather_airquality_image_iv);
        weatherTextIv = (ImageView) view.findViewById(R.id.weather_weather_text_image_iv);



        levelTv = (TextView) view.findViewById(R.id.aqi_quality_level_tv);
        primaryPolluteTv = (TextView) view.findViewById(R.id.aqi_primary_pollute_tv);
        affectTv = (TextView) view.findViewById(R.id.aqi_affect_tv);
      //  updateTimeTv = (EnglishTextView) view.findViewById(R.id.aqi_update_time_tv);
        pm25Tv = (TextView) view.findViewById(R.id.aqi_pm25_tv);
        pm10Tv = (TextView) view.findViewById(R.id.aqi_pm10_tv);


        dayNightSwitch = (Switch) view.findViewById(R.id.daily_weather_switch_tb);
        titleTv = (TextView) view.findViewById(R.id.daily_weather_title_tv);
        swtciRl = (RelativeLayout) view.findViewById(R.id.daily_weather_swtch_rl);
        dailyDayFl = (FrameLayout) view.findViewById(R.id.daily_day_fl);
        dailyNightFl = (FrameLayout) view.findViewById(R.id.daily_night_fl);
        dailyDayRv = (RecyclerView) view.findViewById(R.id.weather_daily_day_weather_rv);
        dailyNightRv = (RecyclerView) view.findViewById(R.id.weather_daily_night_weather_rv);
        day_title_tv = (TextView) view.findViewById(R.id.day_weather_title_tv);

        indexRv = (RecyclerView) view.findViewById(R.id.weather_index_rv);
        return view;
    }

    @Subscribe
    public void setWeatherInfo(Weather weather){
       // setWeather(weather);
        DefaultCity defaultCity = DataSupport.find(DefaultCity.class,1);
        String parentCityName = defaultCity.getParentCityName();
        //   if(weather.getInfo().getCityName().equals(parentCityName.substring(0,parentCityName.length()-1))
        //  || weather.getInfo().getCityName().equals(defaultCity.getCityName())
        //  ){
        // cityNameTv.setText(DataSupport.find(DefaultCity.class,1).getCityName());
        // }else {
        String cityName = weather.getInfo().getCityName();
        List<SavedCity> savedCityList = DataSupport.findAll(SavedCity.class);
        for(SavedCity savedCity : savedCityList){
            if(savedCity.getCityName().equals(cityName)){
                // cityNameTv.setText(cityName);
                break;
            }
            //  }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hourlyRv.setLayoutManager(layoutManager);
        hourlyAdapter = new HourlyAdapter(weather.getInfo().getHourlyList());
        hourlyRv.setAdapter(hourlyAdapter);

        String updateTime = WeatherInfoHelper.getUpdateTime(weather.getInfo().getUpdateTime());
        String tempInfo = weather.getInfo().getTemp() + getResources().getString(R.string.celsius);
        String tempLow = "L:" + weather.getInfo().getTempLow();
        String tempHigh = "H:" + weather.getInfo().getTempHigh();
        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        String humidityInfo = weather.getInfo().getHumidity() + "%";
        String windInfo = weather.getInfo().getWindDirect() + "\n" +  weather.getInfo().getWindPower();
        String sunInfo = weather.getInfo().getDailyList().get(0).getSunrise()
                +"\n" + weather.getInfo().getDailyList().get(0).getSunset();
        String airquality = weather.getInfo().getAqi().getQuality();
        int airqualityColor = WeatherInfoHelper.getAirqualityColor(airquality);
        updateTimeTv.setText(updateTime);
        tempTv.setText(tempInfo);
        forecastHourlyTv.setText(WeatherInfoHelper.getHourlyWeatherTipsInfo(weather.getInfo().getHourlyList()));
        forecastDayTv.setText(WeatherInfoHelper.getDayWeatherTipsInfo(weather.getInfo().getDailyList()));
        tempMaxMinTv.setText(tempLow + "\n" + tempHigh);
        if(airquality.length() > 1){
            airQualityTv.setText(airquality.substring(0,2) + "\n" + airquality.substring(2,airquality.length()));
        }else {
            airQualityTv.setText(airquality);
        }
        airQualityTv.setTextColor(airqualityColor);
        airqualityIv.setColorFilter(airqualityColor);
        weatherTextIv.setImageResource(weatherImagePath);
        weatherTextTv.setText(weather.getInfo().getWeather());
        windTv.setText(windInfo);
        humidityTv.setText(humidityInfo);
        sunTv.setText(sunInfo);

        String levelInfo = "空气质量" + weather.getInfo().getAqi().getAqiInfo().getLevel();
        String primaryPolluteInfo = "首要污染物:" + weather.getInfo().getAqi().getPrimarypollutant();
        String[] updateTimes = weather.getInfo().getAqi().getTimePoint().split(" ");
        String pm25Info = "PM2.5: " + weather.getInfo().getAqi().getPm2_5();
        String pm10Info = "PM10: " + weather.getInfo().getAqi().getPm10();
        updateTimeTv.setText(updateTimes[1]);
        levelTv.setText(levelInfo);
        levelTv.setTextColor(WeatherInfoHelper.getAirqualityColor(weather.getInfo().getAqi().getQuality()));
        primaryPolluteTv.setText(primaryPolluteInfo);
        affectTv.setText(weather.getInfo().getAqi().getAqiInfo().getAffect());
        pm25Tv.setText(pm25Info);
        pm10Tv.setText(pm10Info);

        dailyDayRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        dailyDayAdapter = new DailyDayAdapter(weather.getInfo().getDailyList());
        dailyDayRv.setAdapter(dailyDayAdapter);
        setFlipAnimation(weather);

        indexRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        indexAdapter = new IndexAdapter(weather.getInfo().getIndexList());
        indexRv.setAdapter(indexAdapter);
    }

    private void setFlipAnimation(Weather weather) {
        final AnimatorSet rightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),R.animator.card_right_out);
        final AnimatorSet leftInSet = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),R.animator.card_left_in);
        rightOutSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                dayNightSwitch.setClickable(false);
            }
        });
        leftInSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dayNightSwitch.setClickable(true);
            }
        });
        int distance = 16000;
        float scale = getResources().getDisplayMetrics().density*distance;
        dailyDayFl.setCameraDistance(scale);
        dailyNightFl.setCameraDistance(scale);
        dayNightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isShownBack) {
                rightOutSet.setTarget(dailyDayFl);
                leftInSet.setTarget(dailyNightFl);
                rightOutSet.start();
                leftInSet.start();
             //   swtciRl.setBackgroundColor(getResources().getColor(R.color.card_daily_night));
             //   titleTv.setTextColor(Color.WHITE);
                day_title_tv.setText("夜晚");
                isShownBack = true;

                dailyNightRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                dailyNightAdapter = new DailyNightAdapter(weather.getInfo().getDailyList());
                dailyNightRv.setAdapter(dailyNightAdapter);
            }else {
                rightOutSet.setTarget(dailyNightFl);
                leftInSet.setTarget(dailyDayFl);
                rightOutSet.start();
                leftInSet.start();
                isShownBack = false;
                day_title_tv.setText("白天");
              //  swtciRl.setBackgroundColor(Color.WHITE);
              //  titleTv.setTextColor(Color.BLACK);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
