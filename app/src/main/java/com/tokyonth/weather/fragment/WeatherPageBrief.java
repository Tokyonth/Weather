package com.tokyonth.weather.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tokyonth.weather.R;
import com.tokyonth.weather.activity.MainActivity;
import com.tokyonth.weather.blur.BlurSingle;
import com.tokyonth.weather.fragment.component.base.BaseSubscribeFragment;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.model.bean.WeatherBean;
import com.tokyonth.weather.model.bean.entity.Hourly;
import com.tokyonth.weather.view.widget.EnglishTextView;
import com.tokyonth.weather.utils.WeatherInfoHelper;
import com.tokyonth.weather.view.widget.TempTextView;
import com.tokyonth.weather.view.custom.LineWeatherView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class WeatherPageBrief extends BaseSubscribeFragment {

    private EnglishTextView updateTimeTv;
    private TempTextView tempTv;
    private TextView weatherTextTv;
    private LineWeatherView lineWeatherView;

    private LinearLayout blur_line_ll;
    private BlurSingle.BlurLayout blur;
    private ImageView weatherTextIv;


    @Override
    protected int getLayoutId() {
        return R.layout.page_weather_brief;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        updateTimeTv = (EnglishTextView) view.findViewById(R.id.weather_update_time_tv);

        tempTv = (TempTextView) view.findViewById(R.id.weather_temp_tv);
        lineWeatherView = (LineWeatherView) view.findViewById(R.id.weather);
        blur_line_ll = (LinearLayout) view.findViewById(R.id.blur_line_ll);

        weatherTextTv = (TextView) view.findViewById(R.id.weather_weather_text_tv);
        weatherTextIv = (ImageView) view.findViewById(R.id.weather_weather_text_image_iv);
        setBlur();


    }


    private void setBlur(){
        final View view_test=((MainActivity)getActivity()).main_ll;
        blur=new BlurSingle.BlurLayout(blur_line_ll,view_test);
    }

    @Override
    protected void setWeather(Weather weather) {
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

        List<WeatherBean> data = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            Hourly hourly = weather.getInfo().getHourlyList().get(i);
            WeatherBean bean = new WeatherBean(hourly.getWeather(), hourly.getTemp(), hourly.getTime());
            data.add(bean);
        }
        lineWeatherView.setData(data);
        String updateTime = WeatherInfoHelper.getUpdateTime(weather.getInfo().getUpdateTime());
        String tempInfo = weather.getInfo().getTemp() + "°";

        updateTimeTv.setText("提供商数据更新时间:" + updateTime);
        tempTv.setText(tempInfo);

        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        weatherTextIv.setImageResource(weatherImagePath);
        weatherTextTv.setText(weather.getInfo().getWeather());
    }

}
