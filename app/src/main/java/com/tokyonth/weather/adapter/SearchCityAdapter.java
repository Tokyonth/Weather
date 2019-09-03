package com.tokyonth.weather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.City;
import com.tokyonth.weather.search.IOnItemClickListener;

import org.litepal.crud.DataSupport;

import java.util.List;

public class SearchCityAdapter extends RecyclerView.Adapter<SearchCityAdapter.ViewHolder> {

    private Context context;
    private List<City> cityList;

    private IOnItemClickListener iOnItemClickListener;

    public void setOnItemClickListener(IOnItemClickListener iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    public SearchCityAdapter(Context context, List<City> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_search_city, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final City city = cityList.get(position);
        List<City> parentCityList = DataSupport.where("cityid = ?" , city.getParentId()).limit(1).find(City.class);
        if(!parentCityList.isEmpty()){
            String cityInfo = parentCityList.get(0).getCityName() + " - " + city.getCityName();
            holder.cityInfoTv.setText(cityInfo);
        } else {
            holder.cityInfoTv.setText(city.getCityName());
        }
        if (iOnItemClickListener != null){
            holder.itemView.setOnClickListener(v -> iOnItemClickListener.onItemClick(city));
        }

    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityInfoTv;
        ViewHolder(View view) {
            super(view);
            cityInfoTv = (TextView) view.findViewById(R.id.tv_item_search_city);
        }
    }

}