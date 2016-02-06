package com.umanji.umanjiapp.ui.page.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;
import com.umanji.umanjiapp.ui.page.main.MainFragment;


public class MapActivity extends BaseActivity {
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return MapFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
    }
}
