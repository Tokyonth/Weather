package com.tokyonth.weather.preference;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class CustPreference extends Preference {

    public CustPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if (view instanceof ListView) {
            ListView listView = (ListView) view;
            listView.setDivider(null);
        }

    }

}
