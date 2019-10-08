package com.tokyonth.weather.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.SettingsAdapter;
import com.tokyonth.weather.model.bean.SettingsItemBean;
import com.tokyonth.weather.notification.NotificationBrodcaseRecever;
import com.tokyonth.weather.utils.tools.SPUtils;
import com.tokyonth.weather.utils.helper.WeatherSettingsHelper;
import com.tokyonth.weather.view.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView rv_settings_list;
    private SettingsAdapter settings_adapter;

    private CustomDialog dialog;
    private NotificationBrodcaseRecever receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
        initData();
        initSettings();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_title_arrow_left);
        setTitle(null);
        setSupportActionBar(toolbar);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        rv_settings_list = findViewById(R.id.setting_rv);
    }

    private void initData() {
        List<SettingsItemBean> list = new ArrayList<>();
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_TITLE, "天气通知", null, Color.parseColor("#E91E63")));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_SWITCH, "通知栏天气", "在通知栏显示天气", Color.BLUE));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_COMMON, "通知栏样式", "设置显示在通知栏天气的样式", Color.BLUE));

        list.add(new SettingsItemBean(SettingsItemBean.TYPE_TITLE, "个性化", null, Color.parseColor("#4CAF50")));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_COMMON, "使用高斯模糊背景", "这将会禁用动态天气背景", Color.BLUE));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_SWITCH, "夜间模式", null, Color.BLUE));

        list.add(new SettingsItemBean(SettingsItemBean.TYPE_TITLE, "数据源", null, Color.parseColor("#FF5722")));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_SWITCH, "使用自己的KEY", null, Color.BLUE));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_COMMON, "自定义KEY", "输入自己所申请的KEY", Color.BLUE));

        list.add(new SettingsItemBean(SettingsItemBean.TYPE_TITLE, "其他", null, Color.parseColor("#2196F3")));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_COMMON, "更新城市", "更新城市数据库", Color.BLUE));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_COMMON, "检测更新", "当前版本：1.0", Color.BLUE));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_COMMON, "关于", null, Color.BLUE));
        list.add(new SettingsItemBean(SettingsItemBean.TYPE_COMMON, "捐赠", null, Color.BLUE));

        settings_adapter = new SettingsAdapter(list);
        rv_settings_list.setLayoutManager(new LinearLayoutManager(this));
        rv_settings_list.setAdapter(settings_adapter);
        settings_adapter.setOnItemClick((view, pos) -> {
            switch (pos) {
                case 2:
                    dialog = new CustomDialog(SettingsActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("测试");
                    dialog.setYesOnclickListener("确定", () -> dialog.dismiss());
                    dialog.setCancelable(false);
                    dialog.create();
                    dialog.show();
                    break;
                case 4:
                    Intent intent = new Intent();
                    intent.setClass(this, BlurSetActivity.class);
                    startActivity(intent);
                    break;
            }
        });
        settings_adapter.setOnItemSwitchClick((view, bool, pos) -> {
            switch (pos) {
                case 1:
                    receiver = WeatherSettingsHelper.setWeatherNotification(SettingsActivity.this, bool);
                    break;
                case 4:

                    break;
            }
        });
    }

    private void initSettings() {
        boolean bool = (boolean) SPUtils.getData("switch_notification_weather", false);
        settings_adapter.setSettingsSwitchChecked(1, bool);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

}
