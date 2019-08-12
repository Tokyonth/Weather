package com.tokyonth.weather.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.tokyonth.weather.BaseApplication;
import com.tokyonth.weather.R;
import com.tokyonth.weather.notification.NotificationTools;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_page);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("weather.receiver");

        findPreference("key_show_notification").setOnPreferenceChangeListener((preference, newValue) -> {

            if ((boolean)newValue) {
                intent.putExtra("key", NotificationTools.OPEN_WEATHER_NOTIFICATION);
                BaseApplication.getContext().sendBroadcast(intent);
            } else {
                intent.putExtra("key", NotificationTools.CLOSE_WEATHER_NOTIFICATION);
                BaseApplication.getContext().sendBroadcast(intent);
            }
            return true;
        });


    }
}
