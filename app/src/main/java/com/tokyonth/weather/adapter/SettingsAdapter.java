package com.tokyonth.weather.adapter;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kyleduo.switchbutton.SwitchButton;
import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.SettingsItemBean;

import java.util.List;

import static com.tokyonth.weather.model.bean.SettingsItemBean.TYPE_COMMON;
import static com.tokyonth.weather.model.bean.SettingsItemBean.TYPE_SWITCH;
import static com.tokyonth.weather.model.bean.SettingsItemBean.TYPE_TITLE;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SettingsItemBean> list;
    private OnItemClick onItemCommonClick;
    private OnItemSwitchClick onItemSwitchClick;

    private static boolean isSetChecked = false;
    private static int index = 0;

    public SettingsAdapter(List<SettingsItemBean> list) {
        this.list = list;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemCommonClick = onItemClick;
    }

    public void setOnItemSwitchClick(OnItemSwitchClick onItemSwitchClick) {
        this.onItemSwitchClick = onItemSwitchClick;
    }

    public interface OnItemClick {
        void onCommonClick(View view, int pos);
    }

    public interface OnItemSwitchClick {
        void onSwitch(View view, boolean bool, int pos);
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
            ((CommonViewHolder) holder).cardView.setOnClickListener(v -> onItemCommonClick.onCommonClick(v, position));
        } else if (holder instanceof SwitchViewHolder) {
            ((SwitchViewHolder) holder).title.setText(list.get(position).getTitle());
            if (list.get(position).getSub() == null) {
                ((SwitchViewHolder) holder).sub.setVisibility(View.GONE);
            } else {
                ((SwitchViewHolder) holder).sub.setText(list.get(position).getSub());
            }
            ((SwitchViewHolder) holder).switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> onItemSwitchClick.onSwitch(buttonView, isChecked, position));
            ((SwitchViewHolder) holder).cardView.setOnClickListener(null);
            if (position == index) {
                ((SwitchViewHolder) holder).switchButton.setChecked(isSetChecked);
            }
        }
    }

    public void setSettingsSwitchChecked(int PutIndex, boolean PutIsSetChecked) {
        index = PutIndex;
        isSetChecked = PutIsSetChecked;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        TitleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.title_tv);
            imageView = (ImageView) itemView.findViewById(R.id.title_iv);
        }

    }

    class CommonViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView sub;
        CardView cardView;

        CommonViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.settings_item_title);
            sub = (TextView) itemView.findViewById(R.id.settings_item_sub);
            cardView = (CardView) itemView.findViewById(R.id.common_content_card);
        }

    }

    class SwitchViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView sub;
        SwitchButton switchButton;
        CardView cardView;

        SwitchViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.settings_item_switch_title);
            sub = (TextView) itemView.findViewById(R.id.settings_item_switch_sub);
            switchButton = (SwitchButton) itemView.findViewById(R.id.sb_default);
            cardView = (CardView) itemView.findViewById(R.id.switch_content_card);
        }

    }

}
