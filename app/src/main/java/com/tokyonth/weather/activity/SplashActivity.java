package com.tokyonth.weather.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tokyonth.weather.R;
import com.tokyonth.weather.presenter.CityPresenter;
import com.tokyonth.weather.presenter.CityPresenterImpl;
import com.tokyonth.weather.presenter.LoadCitySituationListener;
import com.tokyonth.weather.utils.sundry.PreferencesLoader;
import com.tokyonth.weather.view.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION = 1;
    private RelativeLayout containerRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (isNetworkConnected(this)) {
            initLayout();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_text_title))
                    .setMessage(getResources().getString(R.string.no_network_connection))
                    .setPositiveButton(getResources().getString(R.string.btn_exit), (dialogInterface, i) -> finish())
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
        containerRl = findViewById(R.id.splash_container_rl);
        ImageView loadingIv = findViewById(R.id.splash_loading_iv);

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        loadingIv.startAnimation(rotateAnimation);

        CityPresenter cityPresenter = new CityPresenterImpl();
        cityPresenter.saveCityList(new LoadCitySituationListener() {
            @Override
            public void Success() {
                Snackbar.make(containerRl, getResources().getString(R.string.import_success), Snackbar.LENGTH_LONG)
                        .show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showTips();
                } else {
                    startHomeActivity();
                }
            }

            @Override
            public void Fail() {
                Snackbar.make(containerRl, getResources().getString(R.string.import_failed), Snackbar.LENGTH_LONG)
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
        CustomDialog dialog = new CustomDialog(this);
        dialog.setTitle(getResources().getString(R.string.permission_explain_title));
        dialog.setMessage(getResources().getString(R.string.permission_explain_msg));
        dialog.setYesOnclickListener(getResources().getString(R.string.btn_ok), () -> {
            requestPermission();
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
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
        if (requestCode == PERMISSION) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, getResources().getString(R.string.must_allow_permission), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
                startHomeActivity();
            } else {
                Toast.makeText(this, getResources().getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
