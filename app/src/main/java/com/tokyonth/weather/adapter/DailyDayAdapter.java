package com.tokyonth.weather.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.entity.Daily;
import com.tokyonth.weather.view.widget.EnglishTextView;
import com.tokyonth.weather.utils.WeatherInfoHelper;

import java.util.List;

public class DailyDayAdapter extends RecyclerView.Adapter<DailyDayAdapter.DailyViewHolder> {

    private List<Daily> dailyList;

    public DailyDayAdapter(List<Daily> dailyList){
        this.dailyList = dailyList;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_weather,parent,false);
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyViewHolder holder, int position) {
        Daily daily = dailyList.get(position);
        holder.timeTv.setText(WeatherInfoHelper.getDay(daily.getDate()));
        holder.weekTv.setText(daily.getWeek());
        holder.tempTv.setText(daily.getDay().getTempHigh());
        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(daily.getDay().getImg());
        holder.weatherImageIv.setImageResource(weatherImagePath);

    }

    @Override
    public int getItemCount() {
        return dailyList.size();
    }

    class DailyViewHolder extends RecyclerView.ViewHolder {

        private TextView weekTv;
        private EnglishTextView timeTv , tempTv ;
        private ImageView weatherImageIv;

        DailyViewHolder(View itemView) {
            super(itemView);
            weekTv = (TextView) itemView.findViewById(R.id.item_daily_week_tv);
            timeTv = (EnglishTextView) itemView.findViewById(R.id.item_daily_time_tv);
            tempTv = (EnglishTextView) itemView.findViewById(R.id.item_daily_temp_tv);
            weatherImageIv = (ImageView) itemView.findViewById(R.id.item_daily_weather_image_iv);
        }
    }

}
