package com.umanji.umanjiapp.ui.page.auth;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotFragment;

public class SignupActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return SignupFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
