package com.umanji.umanjiapp.ui.page.channel.about.edit;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;


public class AboutEditActivity extends BaseActivity {
    private static final String TAG = "AboutEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return AboutEditFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
