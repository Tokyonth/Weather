package com.tokyonth.weather.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokyonth.weather.BaseApplication;
import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.util.Api;
import com.tokyonth.weather.util.EnglishTextView;
import com.tokyonth.weather.util.PreferencesLoader;
import com.tokyonth.weather.util.RetrofitFactory;
import com.tokyonth.weather.util.WeatherInfoHelper;

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == NORMAL){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_management,parent,false);
            return new CityViewHolder(view);
        }else {
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

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            final DefaultCity defaultCity = DataSupport.find(DefaultCity.class,1);
            ((DefaultCityViewHolder)holder).cityName.setText(defaultCity.getCityName());
            int temp = PreferencesLoader.getInt(PreferencesLoader.DEFAULT_CITY_TEMP,0);
            int img = PreferencesLoader.getInt(PreferencesLoader.DEFAULT_CITY_IMG,0);
            ((DefaultCityViewHolder)holder).tempTv.setText(String.valueOf(temp) + " °C");
            Drawable drawable = WeatherInfoHelper.getWeatherColor(String.valueOf(img));
            int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(String.valueOf(img));
            ((DefaultCityViewHolder)holder).weatherBg.setImageDrawable(drawable);
            ((DefaultCityViewHolder)holder).weatherImageIv.setImageResource(weatherImagePath);
        //    ((DefaultCityViewHolder)holder).imageViewLocal.setImageDrawable(BaseApplication.getContext().getResources().getDrawable(R.drawable.ic_location));
            ((DefaultCityViewHolder)holder).view.setOnClickListener(v -> {
                EventBus.getDefault().post(defaultCity);
                defaultClickListener.onClick();
            });
            ((DefaultCityViewHolder)holder).view.setOnLongClickListener(v -> {
                CoordinatorLayout cityManagementCon = (CoordinatorLayout) activity.findViewById(R.id.city_management_con);
                Snackbar.make(cityManagementCon,"默认城市不能删除",Snackbar.LENGTH_SHORT)
                        .show();
                return true;
            });
        }else {
            SavedCity savedCity = savedCityList.get(position-1);
            ((CityViewHolder)holder).cityName.setText(savedCity.getCityName());
            setWeatherInfo(savedCity,((CityViewHolder)holder));
            ((CityViewHolder)holder).view.setOnClickListener(v -> listener.onItemClick(v,holder.getAdapterPosition()-1));
            ((CityViewHolder)holder).view.setOnLongClickListener(v -> {
                longClickListener.onItemLongClick(v,holder.getAdapterPosition()-1);
                return true;
            });
        }

    }

    private void setWeatherInfo(SavedCity savedCity , final CityViewHolder holder){
        new RetrofitFactory(Api.JISU_URL).getApiInterface()
                .getWeather(Api.JISU_APP_KEY,savedCity.getCityCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        Drawable drawable = WeatherInfoHelper.getWeatherColor(weather.getInfo().getImg());
                        holder.weatherBg.setImageDrawable(drawable);
                        holder.tempTv.setText(weather.getInfo().getTemp() + " °C");
                        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
                        holder.weatherImageIv.setImageResource(weatherImagePath);
                    }

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

        public CityViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            weatherBg = (ImageView) itemView.findViewById(R.id.city_management_bg);
            cityName = (TextView) itemView.findViewById(R.id.city_management_city_name_tv);
            tempTv = (EnglishTextView) itemView.findViewById(R.id.city_management_weather_temp_tv);
            weatherImageIv = (ImageView) itemView.findViewById(R.id.city_management_weather_image_iv);
        }

    }

    class DefaultCityViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView cityName;
        private EnglishTextView tempTv;
        private ImageView weatherImageIv;
        private ImageView weatherBg;

        public DefaultCityViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            weatherBg = (ImageView) itemView.findViewById(R.id.city_management_bg);
            cityName = (TextView) itemView.findViewById(R.id.city_management_default_city_name_tv);
            tempTv = (EnglishTextView) itemView.findViewById(R.id.city_management_default_weather_temp_tv);
            weatherImageIv = (ImageView) itemView.findViewById(R.id.city_management_default_weather_image_iv);
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
