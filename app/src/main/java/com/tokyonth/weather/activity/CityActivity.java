package com.tokyonth.weather.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.CityManagementAdapter;
import com.tokyonth.weather.model.bean.City;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.search.SearchFragment;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Field;
import java.util.List;

public class CityActivity extends AppCompatActivity {

    private CoordinatorLayout cityManagement;
    private CityManagementAdapter adapter;
    private List<SavedCity> savedCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initData();
        initView();
    }

    private void initData() {
        savedCityList = DataSupport.findAll(SavedCity.class);
    }

    private void initView() {
        cityManagement = (CoordinatorLayout) findViewById(R.id.city_management_con);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_title_arrow_left);
        FloatingActionButton addCity = (FloatingActionButton) findViewById(R.id.fab);
        RecyclerView cityRv = (RecyclerView) findViewById(R.id.city_management_rv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(null);

        final PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        addCity.setOnClickListener(v -> {
            SearchFragment searchFragment = SearchFragment.newInstance();
            searchFragment.showFragment(getSupportFragmentManager(),SearchFragment.TAG);
            searchFragment.setCitySelect(city -> {
                if(city.getCityCode() != null){
                    saveCity(city);
                    searchFragment.dismiss();
                }else {
                    Snackbar.make(cityManagement,"该城市/省份没有天气代码，不能添加",Snackbar.LENGTH_LONG)
                            .show();
                }
            });
        });

        adapter = new CityManagementAdapter(this,savedCityList);
        cityRv.setLayoutManager(new LinearLayoutManager(this));
        cityRv.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            SavedCity savedCity = savedCityList.get(position);
            EventBus.getDefault().post(savedCity);
            finish();
        });
        adapter.setOnDefaultClickListener( () -> finish());
        adapter.setOnItemLongClickListener((view, position) -> showPopupMenu(view,position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.city_management_menu, menu);
        return true;
    }



    private void saveCity(City city) {
        if (city != null) {
            SavedCity savedCity = new SavedCity(city.getCityId(),city.getParentId(),city.getCityCode(),city.getCityName());
            if(!compareTwoCities(savedCity)){
                savedCity.save();
                Snackbar.make(cityManagement,"添加成功",Snackbar.LENGTH_LONG)
                        .show();
                savedCityList.add(savedCity);
                adapter.notifyDataSetChanged();
            }else {
                Snackbar.make(cityManagement,"该城市已经存在",Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }

    private boolean compareTwoCities(SavedCity city){
        List<SavedCity> savedCityList = DataSupport.findAll(SavedCity.class);
        for(SavedCity savedCity : savedCityList){
            if(savedCity.getCityId().equals(city.getCityId())){
                return true;
            }
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.city_management_popup_menu,popupMenu.getMenu());
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper helper = (MenuPopupHelper) field.get(popupMenu);
            helper.setForceShowIcon(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.city_management_popup_menu_delete) {
                savedCityList.get(position).delete();
                savedCityList.remove(position);
                adapter.notifyItemRemoved(position);
                Snackbar.make(cityManagement, "已删除", Snackbar.LENGTH_SHORT)
                        .show();
            }
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
