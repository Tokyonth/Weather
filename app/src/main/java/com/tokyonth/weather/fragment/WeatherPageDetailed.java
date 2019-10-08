package com.tokyonth.weather.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.activity.MainActivity;
import com.tokyonth.weather.adapter.IndexAdapter;
import com.tokyonth.weather.adapter.WeatherTrendAdapter;
import com.tokyonth.weather.blur.BlurSingle;
import com.tokyonth.weather.fragment.component.base.BaseSubscribeFragment;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.model.bean.WeatherBean;
import com.tokyonth.weather.model.bean.entity.Hourly;
import com.tokyonth.weather.utils.helper.WeatherInfoHelper;

import com.tokyonth.weather.view.SunriseSunsetView.formatter.SunriseSunsetLabelFormatter;
import com.tokyonth.weather.view.SunriseSunsetView.model.Time;
import com.tokyonth.weather.view.SunriseSunsetView.SunriseSunsetView;
import com.tokyonth.weather.view.widget.EnglishTextView;
import com.tokyonth.weather.view.custom.SemicircleProgressView;
import com.tokyonth.weather.view.custom.Windmill;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherPageDetailed extends BaseSubscribeFragment {

    private TextView tv_level, tv_primary_pollute, tv_affect, tv_pressure;
    private TextView tv_pm25, tv_pm10, tv_so2, tv_no2, tv_co, tv_o3;
    private TextView tv_air_quality, tv_wind, tv_forecast_day, tv_forecast_hourly;
    private RecyclerView rv_index, rv_weather_trend;
    private ImageView iv_air_quality;

    private EnglishTextView tv_humidity;

    private SunriseSunsetView sunset_view;
    private Windmill windmill_big, windmill_small;
    private SemicircleProgressView semicircle_progress_view;

    private View blur0, blur1, blur2,blur3, blur4;

    @Override
    protected int getLayoutId() {
        return R.layout.page_weather_detailed;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windmill_big.clearAnimation();
        windmill_small.clearAnimation();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        blur0 = view.findViewById(R.id.content_weather_msg);
        blur1 = view.findViewById(R.id.ll_ssv);
        blur2 = view.findViewById(R.id.pager_aqi_weather);
        blur4 = view.findViewById(R.id.pager_index_weather);
      //  setBlur();

        tv_level = view.findViewById(R.id.aqi_quality_level_tv);
        tv_primary_pollute = view.findViewById(R.id.aqi_primary_pollute_tv);
        tv_affect = view.findViewById(R.id.aqi_affect_tv);
        tv_pm25 = view.findViewById(R.id.aqi_pm25_tv);
        tv_pm10 = view.findViewById(R.id.aqi_pm10_tv);
        tv_so2 = view.findViewById(R.id.aqi_so2_tv);
        tv_no2 = view.findViewById(R.id.aqi_no2_tv);
        tv_co = view.findViewById(R.id.aqi_co_tv);
        tv_o3 = view.findViewById(R.id.aqi_o3_tv);

        tv_air_quality = view.findViewById(R.id.weather_airquality_tv);
        tv_wind = view.findViewById(R.id.weather_wind_tv);
        tv_humidity = view.findViewById(R.id.weather_humidity_tv);
        tv_forecast_hourly = view.findViewById(R.id.weather_forecast_hourly_tips_tv);
        tv_forecast_day = view.findViewById(R.id.weather_forecast_day_tips_tv);
        tv_pressure = view.findViewById(R.id.weather_pressure_tv);

        iv_air_quality = view.findViewById(R.id.weather_airquality_image_iv);
        rv_weather_trend = view.findViewById(R.id.weather_trend_rv);
        rv_index = view.findViewById(R.id.weather_index_rv);

        windmill_big = view.findViewById(R.id.windmill_big);
        windmill_small = view.findViewById(R.id.windmill_small);
        semicircle_progress_view = view.findViewById(R.id.semicircle_progress_view);
        sunset_view = view.findViewById(R.id.ssv);

        sunset_view.setLabelFormatter(new SunriseSunsetLabelFormatter() {
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

    private void setBlur(){
        View blur_box_view = ((MainActivity)getActivity()).weather_basic;
        BlurSingle.BlurLayout blur_single = null;
        List<View> list = new ArrayList<>();
        list.add(blur0);
        list.add(blur1);
        list.add(blur2);
        list.add(blur3);
        list.add(blur4);
        blur_single = new BlurSingle.BlurLayout(list, blur_box_view);
        blur_single.setRadius(2);
    }

    private void refreshSSV(int sunriseHour, int sunriseMinute, int sunsetHour, int sunsetMinute) {
        sunset_view.setSunriseTime(new Time(sunriseHour, sunriseMinute));
        sunset_view.setSunsetTime(new Time(sunsetHour, sunsetMinute));
        sunset_view.startAnimate();
    }

    @Override
    protected void setWeather(Weather weather) {
        String levelInfo = "空气质量" + weather.getInfo().getAqi().getAqiInfo().getLevel();
        String primaryPolluteInfo = "首要污染物:" + weather.getInfo().getAqi().getPrimarypollutant();
        int color = WeatherInfoHelper.getAirqualityColor(weather.getInfo().getAqi().getQuality());

        tv_level.setText(levelInfo);
        tv_level.setTextColor(WeatherInfoHelper.getAirqualityColor(weather.getInfo().getAqi().getQuality()));
        tv_primary_pollute.setText(primaryPolluteInfo);
        tv_affect.setText(weather.getInfo().getAqi().getAqiInfo().getAffect());
        tv_pm25.setText("PM2.5 : " + weather.getInfo().getAqi().getPm2_5() + " μg/m³");
        tv_pm10.setText("PM10 : " + weather.getInfo().getAqi().getPm10() + " μg/m³");
        tv_so2.setText("SO₂ : " + weather.getInfo().getAqi().getSo2() + " μg/m³");
        tv_no2.setText("NO₂ : " + weather.getInfo().getAqi().getNo2() + " μg/m³");
        tv_o3.setText("O₃ : " + weather.getInfo().getAqi().getO3() + " μg/m³");
        tv_co.setText("CO : " + weather.getInfo().getAqi().getCo() + " μg/m³");

        int aqi_index = Integer.parseInt(weather.getInfo().getAqi().getAqi());
        int default_max = 100;
        if (aqi_index > default_max) {
            default_max += 100;
            semicircle_progress_view.setSesameValues(aqi_index, default_max);
        }

        semicircle_progress_view.setSemicircletitleColor(color);
        semicircle_progress_view.setFrontLineColor(color);

        List<WeatherBean> data = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            Hourly hourly = weather.getInfo().getHourlyList().get(i);
            WeatherBean bean = new WeatherBean(hourly.getWeather(), hourly.getTemp(), hourly.getTime());
            data.add(bean);
        }
        WeatherTrendAdapter adapter = new WeatherTrendAdapter(data);
        LinearLayoutManager ms = new LinearLayoutManager(getContext());
        // 设置 RecyclerView 布局方式为横向布局
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_weather_trend.setLayoutManager(ms);
        rv_weather_trend.setAdapter(adapter);

        tv_forecast_day.setText(WeatherInfoHelper.getDayWeatherTipsInfo(weather.getInfo().getDailyList()));
        tv_forecast_hourly.setText(WeatherInfoHelper.getHourlyWeatherTipsInfo(weather.getInfo().getHourlyList()));

        String humidityInfo = "空气湿度 : " + weather.getInfo().getHumidity() + "%";
        String windInfo = weather.getInfo().getWindDirect() + "\n" +  weather.getInfo().getWindPower();

        String airquality = weather.getInfo().getAqi().getQuality();
        int airqualityColor = WeatherInfoHelper.getAirqualityColor(airquality);

        tv_air_quality.setText(airquality);
        tv_air_quality.setTextColor(airqualityColor);
        iv_air_quality.setColorFilter(airqualityColor);

        tv_wind.setText(windInfo);
        tv_humidity.setText(humidityInfo);
        tv_pressure.setText("气体压强 : " + weather.getInfo().getPressure() + "hPa");

        String Sunrise = weather.getInfo().getDailyList().get(0).getSunrise();
        String Sunset = weather.getInfo().getDailyList().get(0).getSunset();

        int index = Sunrise.indexOf(":");
        String SunriseBefore = Sunrise.substring(0,index);
        String SunriseAfter = Sunrise.substring(index+1);
        String SunsetBefore = Sunset.substring(0,index);
        String SunsetAfter = Sunset.substring(index+1);

        int Sunrise_h = Integer.parseInt(SunriseBefore);
        int Sunrise_m = Integer.parseInt(SunriseAfter);

        int Sunset_h = Integer.parseInt(SunsetBefore);
        int Sunset_m = Integer.parseInt(SunsetAfter);

        refreshSSV(Sunrise_h, Sunrise_m, Sunset_h, Sunset_m);
        List<Integer> list = new ArrayList<>();
        list.add(Sunrise_h);
        list.add(Sunrise_m);
        list.add(Sunset_h);
        list.add(Sunset_m);
        EventBus.getDefault().post(list);

        rv_index.setLayoutManager(new LinearLayoutManager(getActivity()));
        IndexAdapter index_adapter = new IndexAdapter(weather.getInfo().getIndexList());
        rv_index.setAdapter(index_adapter);

        windmill_big.startAnimation();
        windmill_small.startAnimation();

        String str = weather.getInfo().getWindSpeed();
        double wind_speed = Double.parseDouble(str);
        windmill_big.setWindSpeed(wind_speed);
        windmill_small.setWindSpeed(wind_speed);
    }

}
