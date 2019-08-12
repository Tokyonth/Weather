package com.tokyonth.weather.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.WarningAdapter;

public class WarningActivity extends AppCompatActivity {

    private WarningAdapter mWarningAdapter;
    private RecyclerView mWarningRv;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mWarningRv = (RecyclerView) findViewById(R.id.warning_rv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("气象预警");

        mWarningAdapter = new WarningAdapter(this);
        mWarningRv.setLayoutManager(new LinearLayoutManager(this));
        mWarningRv.setAdapter(mWarningAdapter);
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
