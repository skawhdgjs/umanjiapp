package com.umanji.umanjiapp.ui.page.channel.spot.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.create.SpotCreateFragment;


public class HomeListActivity extends BaseActivity {
    private static final String TAG = "HomeListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return HomeListFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
