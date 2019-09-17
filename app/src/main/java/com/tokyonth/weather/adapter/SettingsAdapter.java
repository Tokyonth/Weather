package com.tokyonth.weather.adapter;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kyleduo.switchbutton.SwitchButton;
import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.SettingsItemBean;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_COMMON = 0;
    public static final int TYPE_SWITCH = 1;
    public static final int TYPE_TITLE = 2;

    private List<SettingsItemBean> list;

    public SettingsAdapter(List<SettingsItemBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_TITLE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_title_assembly,parent,false);
            return new TitleViewHolder(view);
        } else if (viewType == TYPE_COMMON) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_ordinary_assembly,parent,false);
            return new CommonViewHolder(view);
        } else if (viewType == TYPE_SWITCH) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_switch_assembly,parent,false);
            return new SwitchViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleViewHolder) {
            ((TitleViewHolder) holder).imageView.setColorFilter(list.get(position).getColor(), PorterDuff.Mode.SRC_IN);
            ((TitleViewHolder) holder).textView.setTextColor(list.get(position).getColor());
            ((TitleViewHolder) holder).textView.setText(list.get(position).getTitle());
        } else if (holder instanceof CommonViewHolder) {
            ((CommonViewHolder) holder).title.setText(list.get(position).getTitle());
            if (list.get(position).getSub() == null) {
                ((CommonViewHolder) holder).sub.setVisibility(View.GONE);
            } else {
                ((CommonViewHolder) holder).sub.setText(list.get(position).getSub());
            }
        } else if (holder instanceof SwitchViewHolder) {
            ((SwitchViewHolder) holder).title.setText(list.get(position).getTitle());
            if (list.get(position).getSub() == null) {
                ((SwitchViewHolder) holder).sub.setVisibility(View.GONE);
            } else {
                ((SwitchViewHolder) holder).sub.setText(list.get(position).getSub());
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public TitleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.title_tv);
            imageView = (ImageView) itemView.findViewById(R.id.title_iv);
        }

    }

    class CommonViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView sub;

        public CommonViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.settings_item_title);
            sub = (TextView) itemView.findViewById(R.id.settings_item_sub);
        }

    }

    class SwitchViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView sub;
        SwitchButton switchButton;

        public SwitchViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.settings_item_title);
            sub = (TextView) itemView.findViewById(R.id.settings_item_sub);
            switchButton = (SwitchButton) itemView.findViewById(R.id.sb_default);
        }

    }

}
