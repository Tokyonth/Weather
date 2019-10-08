package com.tokyonth.weather.activity;

import android.annotation.SuppressLint;
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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.CityManagementAdapter;
import com.tokyonth.weather.model.bean.City;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.search.SearchFragment;
import com.tokyonth.weather.utils.tools.CustomPopWindow;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Field;
import java.util.List;

public class CityActivity extends AppCompatActivity {

    private CoordinatorLayout cdl_city_management;
    private CityManagementAdapter city_adapter;
    private List<SavedCity> saved_city_list;
    private Toolbar toolbar;

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
        saved_city_list = DataSupport.findAll(SavedCity.class);
    }

    private void initView() {
        cdl_city_management = findViewById(R.id.city_management_con);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_title_arrow_left);
        FloatingActionButton addCity = findViewById(R.id.fab);
        RecyclerView cityRv = findViewById(R.id.city_management_rv);
        setSupportActionBar(toolbar);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(null);

        addCity.setOnClickListener(v -> {
            SearchFragment searchFragment = SearchFragment.newInstance();
            searchFragment.showFragment(getSupportFragmentManager(),SearchFragment.TAG);
            searchFragment.setCitySelect(city -> {
                if(city.getCityCode() != null){
                    saveCity(city);
                    searchFragment.dismiss();
                }else {
                    Snackbar.make(cdl_city_management, getResources().getString(R.string.no_city_code),Snackbar.LENGTH_LONG)
                            .show();
                }
            });
        });

        city_adapter = new CityManagementAdapter(this, saved_city_list);
        cityRv.setLayoutManager(new LinearLayoutManager(this));
        cityRv.setAdapter(city_adapter);
        city_adapter.setOnItemClickListener((view, position) -> {
            SavedCity savedCity = saved_city_list.get(position);
            EventBus.getDefault().post(savedCity);
            finish();
        });
        city_adapter.setOnDefaultClickListener(this::finish);
        city_adapter.setOnItemLongClickListener(this::showPopupMenu);
    }

    private void saveCity(City city) {
        if (city != null) {
            SavedCity savedCity = new SavedCity(city.getCityId(),city.getParentId(),city.getCityCode(),city.getCityName());
            if(!compareTwoCities(savedCity)){
                savedCity.save();
                Snackbar.make(cdl_city_management,getResources().getString(R.string.add_success),Snackbar.LENGTH_LONG).show();
                saved_city_list.add(savedCity);
                city_adapter.notifyDataSetChanged();
            }else {
                Snackbar.make(cdl_city_management,getResources().getString(R.string.city_already_exist),Snackbar.LENGTH_LONG).show();
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.city_management_popup_menu_delete) {
                saved_city_list.get(position - 1).delete();
                saved_city_list.remove(position - 1);
                city_adapter.notifyItemRemoved(position);
                Snackbar.make(cdl_city_management, getResources().getString(R.string.city_del), Snackbar.LENGTH_SHORT)
                        .show();
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.city_management_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_city_menu_tips) {
            @SuppressLint("InflateParams")
            View contentView = LayoutInflater.from(this).inflate(R.layout.city_menu_tips_content,null);
            new CustomPopWindow.PopupWindowBuilder(this)
                    .setView(contentView)
                    .create()
                    .showAsDropDown(toolbar, Gravity.END,-50);
        }
        return super.onOptionsItemSelected(item);
    }

}
