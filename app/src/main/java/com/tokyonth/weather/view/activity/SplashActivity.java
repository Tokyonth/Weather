package com.tokyonth.weather.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tokyonth.weather.R;
import com.tokyonth.weather.presenter.CityPresenter;
import com.tokyonth.weather.presenter.CityPresenterImpl;
import com.tokyonth.weather.presenter.LoadCitySituationListener;
import com.tokyonth.weather.util.PreferencesLoader;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION = 1;
    private ImageView loadingIv;
    private TextView loadingTextTv;
    private RelativeLayout containerRl;
    private CityPresenter cityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (isNetworkConnected(this)) {
            initLayout();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("你似乎没有连接到网络，请连接网络后重试")
                    .setPositiveButton("退出", (dialogInterface, i) -> finish())
                    .create().show();
        }

    }

    private void initLayout() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        containerRl = (RelativeLayout) findViewById(R.id.splash_container_rl);
        loadingIv = (ImageView) findViewById(R.id.splash_loading_iv);
        loadingTextTv = (TextView) findViewById(R.id.splash_loading_text_tv);

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        loadingIv.startAnimation(rotateAnimation);

        cityPresenter = new CityPresenterImpl();
        cityPresenter.saveCityList(new LoadCitySituationListener() {
            @Override
            public void Success() {
                Snackbar.make(containerRl, "导入成功", Snackbar.LENGTH_LONG)
                        .show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showTips();
                } else {
                    startHomeActivity();
                }
            }

            @Override
            public void Fail() {
                Snackbar.make(containerRl, "导入失败!请稍后重试", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void showTips() {
        new AlertDialog.Builder(this)
                .setTitle("权限说明")
                .setMessage("定位权限用于获取所在地点的城市信息，存储权限用于高德定位写入离线定位数据。")
                .setPositiveButton("确定", (dialog, which) -> requestPermission())
                .create().show();
    }

    private void requestPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, PERMISSION);
        }
    }

    private void startHomeActivity() {
        new Handler().postDelayed(() -> {
            PreferencesLoader.putBoolean(PreferencesLoader.IMPORT_DATA, false);
            finish();
            Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(homeIntent);
        }, 1000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本应用程序！", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    startHomeActivity();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


}
