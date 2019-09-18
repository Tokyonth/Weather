package com.tokyonth.weather.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kyleduo.switchbutton.SwitchButton;
import com.tokyonth.weather.R;
import com.tokyonth.weather.activity.MainActivity;
import com.tokyonth.weather.adapter.DailyDayAdapter;
import com.tokyonth.weather.adapter.DailyNightAdapter;
import com.tokyonth.weather.adapter.IndexAdapter;
import com.tokyonth.weather.blur.BlurSingle;
import com.tokyonth.weather.fragment.component.base.BaseSubscribeFragment;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.WeatherInfoHelper;
import com.tokyonth.weather.view.sunrisesunsetview.formatter.SunriseSunsetLabelFormatter;
import com.tokyonth.weather.view.sunrisesunsetview.model.Time;
import com.tokyonth.weather.view.sunrisesunsetview.ssv.SunriseSunsetView;
import com.tokyonth.weather.view.widget.EnglishTextView;

import java.util.Locale;

public class WeatherPageDetailed extends BaseSubscribeFragment {

    private TextView levelTv, primaryPolluteTv, affectTv, pm25Tv, pm10Tv;
    private FrameLayout dailyDayFl, dailyNightFl;
    private TextView day_title_tv, airQualityTv, windTv, forecastDayTv, forecastHourlyTv;
    private SwitchButton dayNightSwitch;
    private RecyclerView dailyDayRv, dailyNightRv;
    private RecyclerView indexRv;
    private ImageView airqualityIv;
    private boolean isShownBack;

    private DailyDayAdapter dailyDayAdapter;
    private DailyNightAdapter dailyNightAdapter;
    private IndexAdapter indexAdapter;
    private EnglishTextView tempMaxMinTv, humidityTv;

    private SunriseSunsetView sunsetView;
    private View blur0, blur1, blur2,blur3, blur4;
    private BlurSingle.BlurLayout blur;

    private void setBlur(){
        final View view_test = ((MainActivity)getActivity()).main_ll;
        blur = new BlurSingle.BlurLayout(blur0,view_test);
        blur = new BlurSingle.BlurLayout(blur1,view_test);
        blur = new BlurSingle.BlurLayout(blur2,view_test);
        blur = new BlurSingle.BlurLayout(blur3,view_test);
        blur = new BlurSingle.BlurLayout(blur4,view_test);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.page_weather_detailed;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        blur0 = view.findViewById(R.id.content_weather_msg);
        blur1 = view.findViewById(R.id.ssv);
        blur2 = view.findViewById(R.id.pager_aqi_weather);
        blur3 = view.findViewById(R.id.pager_daily_weather);
        blur4 = view.findViewById(R.id.pager_index_weather);
        setBlur();

        levelTv = (TextView) view.findViewById(R.id.aqi_quality_level_tv);
        primaryPolluteTv = (TextView) view.findViewById(R.id.aqi_primary_pollute_tv);
        affectTv = (TextView) view.findViewById(R.id.aqi_affect_tv);
        pm25Tv = (TextView) view.findViewById(R.id.aqi_pm25_tv);
        pm10Tv = (TextView) view.findViewById(R.id.aqi_pm10_tv);
        dayNightSwitch = (SwitchButton) view.findViewById(R.id.daily_weather_switch_tb);
        dailyDayFl = (FrameLayout) view.findViewById(R.id.daily_day_fl);
        dailyNightFl = (FrameLayout) view.findViewById(R.id.daily_night_fl);
        dailyDayRv = (RecyclerView) view.findViewById(R.id.weather_daily_day_weather_rv);
        dailyNightRv = (RecyclerView) view.findViewById(R.id.weather_daily_night_weather_rv);
        day_title_tv = (TextView) view.findViewById(R.id.day_weather_title_tv);
        indexRv = (RecyclerView) view.findViewById(R.id.weather_index_rv);

        airQualityTv = (TextView) view.findViewById(R.id.weather_airquality_tv);
        windTv = (TextView) view.findViewById(R.id.weather_wind_tv);
        humidityTv = (EnglishTextView) view.findViewById(R.id.weather_humidity_tv);
        tempMaxMinTv = (EnglishTextView) view.findViewById(R.id.weather_temp_max_min_tv);
        airqualityIv = (ImageView) view.findViewById(R.id.weather_airquality_image_iv);
        forecastHourlyTv = (TextView) view.findViewById(R.id.weather_forecast_hourly_tips_tv);
        forecastDayTv = (TextView) view.findViewById(R.id.weather_forecast_day_tips_tv);

        sunsetView = (SunriseSunsetView) view.findViewById(R.id.ssv);
        sunsetView.setLabelFormatter(new SunriseSunsetLabelFormatter() {
            @Override
            public String formatSunriseLabel(@NonNull Time sunrise) {
                return formatLabel(sunrise);
            }

            @Override
            public String formatSunsetLabel(@NonNull Time sunset) {
                return formatLabel(sunset);
            }

            private String formatLabel(Time time) {
                return String.format(Locale.getDefault(), "%02d:%02d", time.hour, time.minute);
            }
        });

    }

    private void refreshSSV(int sunriseHour, int sunriseMinute, int sunsetHour, int sunsetMinute) {
        sunsetView.setSunriseTime(new Time(sunriseHour, sunriseMinute));
        sunsetView.setSunsetTime(new Time(sunsetHour, sunsetMinute));
        sunsetView.startAnimate();
    }

    @Override
    protected void setWeather(Weather weather) {
        String levelInfo = "空气质量" + weather.getInfo().getAqi().getAqiInfo().getLevel();
        String primaryPolluteInfo = "首要污染物:" + weather.getInfo().getAqi().getPrimarypollutant();
        //String[] updateTimes = weather.getInfo().getAqi().getTimePoint().split(" ");
        String pm25Info = "PM2.5: " + weather.getInfo().getAqi().getPm2_5();
        String pm10Info = "PM10: " + weather.getInfo().getAqi().getPm10();
        //updateTimeTv.setText(updateTimes[1]);
        levelTv.setText(levelInfo);
        levelTv.setTextColor(WeatherInfoHelper.getAirqualityColor(weather.getInfo().getAqi().getQuality()));
        primaryPolluteTv.setText(primaryPolluteInfo);
        affectTv.setText(weather.getInfo().getAqi().getAqiInfo().getAffect());
        pm25Tv.setText(pm25Info);
        pm10Tv.setText(pm10Info);
        forecastDayTv.setText(WeatherInfoHelper.getDayWeatherTipsInfo(weather.getInfo().getDailyList()));

        String tempLow = "L:" + weather.getInfo().getTempLow();
        String tempHigh = "H:" + weather.getInfo().getTempHigh();

        String humidityInfo = weather.getInfo().getHumidity() + "%";
        String windInfo = weather.getInfo().getWindDirect() + "\n" +  weather.getInfo().getWindPower();
        forecastHourlyTv.setText(WeatherInfoHelper.getHourlyWeatherTipsInfo(weather.getInfo().getHourlyList()));

        String airquality = weather.getInfo().getAqi().getQuality();
        int airqualityColor = WeatherInfoHelper.getAirqualityColor(airquality);
        tempMaxMinTv.setText(tempLow + "\n" + tempHigh);
        if(airquality.length() > 1){
            airQualityTv.setText(airquality.substring(0,2) + "\n" + airquality.substring(2,airquality.length()));
        }else {
            airQualityTv.setText(airquality);
        }

        airQualityTv.setTextColor(airqualityColor);
        airqualityIv.setColorFilter(airqualityColor);

        windTv.setText(windInfo);
        humidityTv.setText(humidityInfo);
        //sunTv.setText(sunInfo);

        String Sunrise = weather.getInfo().getDailyList().get(0).getSunrise();
        String Sunset = weather.getInfo().getDailyList().get(0).getSunset();

        int index = Sunrise.indexOf(":");
        String SunriseBefore = Sunrise.substring(0,index);
        String SunriseAfter = Sunrise.substring(index+1);

        String SunsetBefore = Sunset.substring(0,index);
        String SunsetAfter = Sunset.substring(index+1);

        refreshSSV(Integer.parseInt(SunriseBefore), Integer.parseInt(SunriseAfter), Integer.parseInt(SunsetBefore), Integer.parseInt(SunsetAfter));

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
            }
        });
    }

}
