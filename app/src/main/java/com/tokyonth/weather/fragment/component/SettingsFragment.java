package com.tokyonth.weather.fragment.component;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tokyonth.weather.BaseApplication;
import com.tokyonth.weather.R;
import com.tokyonth.weather.fragment.component.base.BaseFragment;
import com.tokyonth.weather.notification.ListenerNotificationBrodcaseRecever;
import com.tokyonth.weather.notification.NotificationTools;

public class SettingsFragment extends BaseFragment {

    private String actionStr = "com.tokyonth.weather.receiver";
    private ListenerNotificationBrodcaseRecever receiver;
    private Context mContext = BaseApplication.getContext();

    private TextView title, sub;

 /*   @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_page);
        Intent intent = new Intent();
        intent.setAction(actionStr);

        findPreference("key_show_notification").setOnPreferenceChangeListener((preference, newValue) -> {

            receiver = new ListenerNotificationBrodcaseRecever();
            IntentFilter filter = new IntentFilter();
            filter.addAction(actionStr);
            mContext.registerReceiver(receiver, filter);

            if ((boolean)newValue) {
                intent.putExtra("key", NotificationTools.OPEN_WEATHER_NOTIFICATION);
                mContext.sendBroadcast(intent);
            } else {
                intent.putExtra("key", NotificationTools.CLOSE_WEATHER_NOTIFICATION);
                mContext.sendBroadcast(intent);
            }
            return true;
        });


    }*/

    @Override
    protected int getLayoutId() {
        return 0;
      //  return R.layout.settings_fragment_page;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        title = (TextView) view.findViewById(R.id.settings_item_title);
        sub = (TextView) view.findViewById(R.id.settings_item_sub);

        title.setText("通知栏显示天气");
        sub.setText("开启");

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

}
