package com.umanji.umanjiapp.helper;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;


public class DefaultActivity extends BaseActivity {
    private static final String TAG = "DefaultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return DefaultFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
