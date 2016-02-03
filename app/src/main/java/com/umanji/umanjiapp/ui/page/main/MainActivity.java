package com.umanji.umanjiapp.ui.page.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telecom.Call;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.base.BaseActivity;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return MainFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
    }
}
