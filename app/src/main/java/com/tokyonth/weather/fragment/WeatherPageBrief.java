package com.tokyonth.weather.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.activity.MainActivity;
import com.tokyonth.weather.blur.BlurSingle;
import com.tokyonth.weather.fragment.component.base.BaseSubscribeFragment;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.view.widget.EnglishTextView;
import com.tokyonth.weather.utils.helper.WeatherInfoHelper;
import com.tokyonth.weather.view.widget.TempTextView;
import com.tokyonth.weather.view.custom.WeekWeatherView;

import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.util.List;

public class WeatherPageBrief extends BaseSubscribeFragment {

    private EnglishTextView tv_update_time;
    private TempTextView tv_temp;
    private TextView tv_weather_text, tv_temp_max_min;
    private LinearLayout blur_line_ll;
    private ImageView iv_weather_text;

    private BlurSingle.BlurLayout blur_single;
    private WeekWeatherView week_view;

    @Override
    protected int getLayoutId() {
        return R.layout.page_weather_brief;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        tv_update_time = (EnglishTextView) view.findViewById(R.id.weather_update_time_tv);
        tv_temp = (TempTextView) view.findViewById(R.id.weather_temp_tv);
        blur_line_ll = (LinearLayout) view.findViewById(R.id.blur_line_ll);
        tv_weather_text = (TextView) view.findViewById(R.id.weather_weather_text_tv);
        iv_weather_text = (ImageView) view.findViewById(R.id.weather_weather_text_image_iv);
        week_view = view.findViewById(R.id.week_view);
        tv_temp_max_min = (EnglishTextView) view.findViewById(R.id.weather_temp_max_min_tv);
        // setBlur();
    }

    private void setBlur(){
        View blur_box_view = ((MainActivity)getActivity()).weather_basic;
        blur_single = new BlurSingle.BlurLayout(blur_line_ll, blur_box_view);
        blur_single.setRadius(5);
    }

    @Subscribe
    public void getImgPath(String path) {
        if (path != null) {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            ((MainActivity) getActivity()).weather_basic.setBackground(new BitmapDrawable(bmp));
        }
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

        String tempLow = weather.getInfo().getTempLow();
        String tempHigh = weather.getInfo().getTempHigh();
        tv_temp_max_min.setText(tempHigh + "° / " + tempLow + "°");

        week_view.setData(weather.getInfo().getDailyList());
        week_view.invalidate();

        String updateTime = WeatherInfoHelper.getUpdateTime(weather.getInfo().getUpdateTime());
        String tempInfo = weather.getInfo().getTemp() + "°";

        tv_update_time.setText("提供商数据更新时间:" + updateTime);
        tv_temp.setText(tempInfo);

        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        iv_weather_text.setImageResource(weatherImagePath);
        tv_weather_text.setText(weather.getInfo().getWeather());
    }

}
