package com.tokyonth.weather.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.tokyonth.weather.R;

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

        screenshot_dynamic_card = (CardView) findViewById(R.id.screenshot_dynamic_card);
        screenshot_blur_card = (CardView) findViewById(R.id.screenshot_blur_card);
        dynamic_rb = (RadioButton) findViewById(R.id.screenshot_dynamic_rb);
        blur_rb = (RadioButton) findViewById(R.id.screenshot_blur_rb);
        blur_seek_bar = (SeekBar) findViewById(R.id.blur_seek_bar);
        select_image_btn = (Button) findViewById(R.id.select_image_btn);
    }

    private void initData() {
        screenshot_blur_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blur_rb.setChecked(true);
                dynamic_rb.setChecked(false);
                select_image_btn.setEnabled(true);
                blur_seek_bar.setEnabled(true);
            }
        });
        screenshot_dynamic_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blur_rb.setChecked(false);
                dynamic_rb.setChecked(true);
                select_image_btn.setEnabled(false);
                blur_seek_bar.setEnabled(false);
            }
        });
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
