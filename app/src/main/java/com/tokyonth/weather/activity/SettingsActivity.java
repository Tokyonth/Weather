package com.tokyonth.weather.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import com.tokyonth.weather.model.SettingsModel;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_title_arrow_left);
        setTitle(null);
        setSupportActionBar(toolbar);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        rv = (RecyclerView) findViewById(R.id.setting_rv);
    }

    private void initData() {
        List<SettingsModel> list = new ArrayList<>();
        list.add(new SettingsModel(2, "天气通知", null, Color.parseColor("#E91E63")));
        list.add(new SettingsModel(1, "通知栏天气", "在通知栏显示天气", Color.BLUE));
        list.add(new SettingsModel(0, "通知栏样式", "设置显示在通知栏天气的样式", Color.BLUE));

        list.add(new SettingsModel(2, "个性化", null, Color.parseColor("#4CAF50")));
        list.add(new SettingsModel(0, "使用高斯模糊背景", "这将会禁用动态天气背景", Color.BLUE));

        list.add(new SettingsModel(2, "数据源", null, Color.parseColor("#FF5722")));
        list.add(new SettingsModel(1, "使用自己的KEY", null, Color.BLUE));
        list.add(new SettingsModel(0, "自定义KEY", "输入自己所申请的KEY", Color.BLUE));

        list.add(new SettingsModel(2, "其他", null, Color.parseColor("#2196F3")));
        list.add(new SettingsModel(0, "更新城市", "更新城市数据库", Color.BLUE));
        list.add(new SettingsModel(0, "检测更新", "当前版本：1.0", Color.BLUE));
        list.add(new SettingsModel(0, "关于", null, Color.BLUE));
        list.add(new SettingsModel(0, "捐赠", null, Color.BLUE));

        adapter = new SettingsAdapter(list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
