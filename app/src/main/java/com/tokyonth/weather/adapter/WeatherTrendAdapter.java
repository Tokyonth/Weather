package com.tokyonth.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.WeatherBean;
import com.tokyonth.weather.view.custom.VerticalProgressBar;

import java.util.List;

public class WeatherTrendAdapter extends RecyclerView.Adapter<WeatherTrendAdapter.WeatherTrendHolder> {

    private List<WeatherBean> weatherBean;

    public WeatherTrendAdapter(List<WeatherBean> weatherBean) {
        this.weatherBean = weatherBean;
    }

    private int getIconResId(String weather) {
        int resId = R.drawable.weather_sunny;
        switch (weather) {
            case WeatherBean.SUN:
                resId = R.drawable.weather_sunny;
                break;
            case WeatherBean.OVERCAST:
                resId = R.drawable.weather_overcast;
                break;
            case WeatherBean.RAIN:
                resId = R.drawable.weather_heavy_rain;
                break;
            case WeatherBean.SNOW:
                resId = R.drawable.weather_light_snow;
                break;
            case WeatherBean.CLOUDY:
                resId = R.drawable.weather_cloudy;
                break;
            case WeatherBean.THUNDER:
                resId = R.drawable.weather_thunder;
                break;
            case WeatherBean.LIGHT_RAIN:
                resId = R.drawable.weather_light_rain;
                break;
            case WeatherBean.SHOWER:
                resId = R.drawable.weather_rain_shower;
                break;
        }
        return resId;
    }

    @NonNull
    @Override
    public WeatherTrendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_trend_item,parent,false);
        return new WeatherTrendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherTrendHolder holder, int position) {
        WeatherBean bean = weatherBean.get(position);
        holder.progressBar.setProgress(bean.temperature);
        holder.weather.setText(bean.weather);
        holder.time.setText(bean.time);
        holder.temp.setText(bean.temperatureStr);
        holder.iv.setImageResource(getIconResId(bean.weather));
    }

    @Override
    public int getItemCount() {
        return weatherBean.size();
    }

    class WeatherTrendHolder extends RecyclerView.ViewHolder {

        TextView temp, time, weather;
        ImageView iv;
        VerticalProgressBar progressBar;

        WeatherTrendHolder(@NonNull View itemView) {
            super(itemView);
            temp = itemView.findViewById(R.id.weather_trend_item_tv_temp);
            time = itemView.findViewById(R.id.weather_trend_item_tv_time);
            iv = itemView.findViewById(R.id.weather_trend_item_iv_info);
            weather = itemView.findViewById(R.id.weather_trend_item_tv_weather);
            progressBar = itemView.findViewById(R.id.weather_trend_item_progressbar);
        }
    }

}
