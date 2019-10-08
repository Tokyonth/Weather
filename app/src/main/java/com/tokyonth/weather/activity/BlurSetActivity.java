package com.tokyonth.weather.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.tokyonth.weather.R;
import com.tokyonth.weather.utils.tools.ContentUriUtils;
import com.tokyonth.weather.utils.tools.SPUtils;

import org.greenrobot.eventbus.EventBus;

public class BlurSetActivity extends AppCompatActivity {

    private CardView screenshot_dynamic_card;
    private CardView screenshot_blur_card;
    private RadioButton dynamic_rb;
    private RadioButton blur_rb;
    private SeekBar blur_seek_bar;
    private Button select_image_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur_set);
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
        screenshot_dynamic_card = findViewById(R.id.screenshot_dynamic_card);
        screenshot_blur_card = findViewById(R.id.screenshot_blur_card);
        dynamic_rb = findViewById(R.id.screenshot_dynamic_rb);
        blur_rb = findViewById(R.id.screenshot_blur_rb);
        blur_seek_bar = findViewById(R.id.blur_seek_bar);
        select_image_btn = findViewById(R.id.select_image_btn);

        select_image_btn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 10);
        });
    }

    private void initData() {
        screenshot_blur_card.setOnClickListener(view -> {
            setView(true);
            SPUtils.putData("isBlur", true);
        });
        screenshot_dynamic_card.setOnClickListener(view -> {
            setView(false);
            SPUtils.putData("isBlur", false);
        });
    }

    private void initSettings() {
        setView((boolean)SPUtils.getData("isBlur", false));
    }

    private void setView(boolean bool) {
        if (bool) {
            blur_rb.setChecked(true);
            dynamic_rb.setChecked(false);
            select_image_btn.setEnabled(true);
            blur_seek_bar.setEnabled(true);
            select_image_btn.setTextColor(Color.WHITE);
        } else {
            blur_rb.setChecked(false);
            dynamic_rb.setChecked(true);
            select_image_btn.setEnabled(false);
            blur_seek_bar.setEnabled(false);
            select_image_btn.setTextColor(Color.GRAY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 10) {
            String path = ContentUriUtils.getPath(this, data.getData());
            assert path != null;
            SPUtils.putData("image_path", path);
            EventBus.getDefault().post(path);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
