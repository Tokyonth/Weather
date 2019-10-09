package com.tokyonth.weather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.api.Api;
import com.tokyonth.weather.view.widget.EnglishTextView;
import com.tokyonth.weather.utils.sundry.PreferencesLoader;
import com.tokyonth.weather.utils.api.RetrofitFactory;
import com.tokyonth.weather.utils.helper.WeatherInfoHelper;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CityManagementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 0;
    private static final int NORMAL = 1;

    private List<SavedCity> savedCityList;
    private Activity activity;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;
    private OnDefaultClickListener defaultClickListener;

    public CityManagementAdapter(Activity activity ,List<SavedCity> savedCityList){
        this.activity = activity;
        this.savedCityList = savedCityList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_management,parent,false);
            return new CityViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_management_default_city,parent,false);
            return new DefaultCityViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return HEADER;
        }else {
            return NORMAL;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            final DefaultCity defaultCity = DataSupport.find(DefaultCity.class,1);
            ((DefaultCityViewHolder)holder).cityName.setText(defaultCity.getCityName());
            int temp = PreferencesLoader.getInt(PreferencesLoader.DEFAULT_CITY_TEMP,0);
            int img = PreferencesLoader.getInt(PreferencesLoader.DEFAULT_CITY_IMG,0);
            ((DefaultCityViewHolder)holder).tempTv.setText(temp + activity.getResources().getString(R.string.celsius));
            Drawable drawable = WeatherInfoHelper.getWeatherColor(String.valueOf(img));
            int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(String.valueOf(img));
            ((DefaultCityViewHolder)holder).weatherBg.setImageDrawable(drawable);
            ((DefaultCityViewHolder)holder).weatherImageIv.setImageResource(weatherImagePath);

            ((DefaultCityViewHolder)holder).view.setOnClickListener(v -> {
                EventBus.getDefault().post(defaultCity);
                defaultClickListener.onClick();
            });
            ((DefaultCityViewHolder)holder).view.setOnLongClickListener(v -> {
                CoordinatorLayout cityManagementCon = activity.findViewById(R.id.city_management_con);
                Snackbar.make(cityManagementCon, activity.getResources().getString(R.string.cannot_del_default_city),Snackbar.LENGTH_SHORT)
                        .show();
                return true;
            });
        }else {
            SavedCity savedCity = savedCityList.get(position-1);
            ((CityViewHolder)holder).cityName.setText(savedCity.getCityName());
            setWeatherInfo(savedCity,((CityViewHolder)holder));
            ((CityViewHolder)holder).view.setOnClickListener(v -> listener.onItemClick(v,holder.getAdapterPosition()-1));
            ((CityViewHolder)holder).view.setOnLongClickListener(v -> {
                longClickListener.onItemLongClick(v,holder.getAdapterPosition());
                return true;
            });
        }

    }

    private void setWeatherInfo(SavedCity savedCity , final CityViewHolder holder){
        new RetrofitFactory(Api.JISU_URL).getApiInterface()
                .getWeather(Api.getJisuAppKey(),savedCity.getCityCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(Weather weather) {
                        Drawable drawable = WeatherInfoHelper.getWeatherColor(weather.getInfo().getImg());
                        holder.weatherBg.setImageDrawable(drawable);
                        holder.tempTv.setText(weather.getInfo().getTemp() + activity.getResources().getString(R.string.celsius));
                        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
                        holder.weatherImageIv.setImageResource(weatherImagePath);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onError(Throwable e) {
                        holder.tempTv.setText("00");
                        holder.weatherImageIv.setImageResource(R.drawable.weather_nothing);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return savedCityList.size() + 1;
    }

    class CityViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView cityName;
        private EnglishTextView tempTv;
        private ImageView weatherImageIv;
        private ImageView weatherBg;

        CityViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            weatherBg = itemView.findViewById(R.id.city_management_bg);
            cityName = itemView.findViewById(R.id.city_management_city_name_tv);
            tempTv = itemView.findViewById(R.id.city_management_weather_temp_tv);
            weatherImageIv = itemView.findViewById(R.id.city_management_weather_image_iv);
        }

    }

    class DefaultCityViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView cityName;
        private EnglishTextView tempTv;
        private ImageView weatherImageIv;
        private ImageView weatherBg;

        DefaultCityViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            weatherBg = itemView.findViewById(R.id.city_management_bg);
            cityName = itemView.findViewById(R.id.city_management_default_city_name_tv);
            tempTv = itemView.findViewById(R.id.city_management_default_weather_temp_tv);
            weatherImageIv = itemView.findViewById(R.id.city_management_default_weather_image_iv);
        }

    }

    public interface OnDefaultClickListener {
        void onClick();
    }

    public void setOnDefaultClickListener (OnDefaultClickListener defaultClickListener) {
        this.defaultClickListener = defaultClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        longClickListener = listener;
    }

}
