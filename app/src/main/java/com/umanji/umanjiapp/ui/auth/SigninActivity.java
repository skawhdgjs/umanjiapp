package com.umanji.umanjiapp.ui.auth;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;

public class SigninActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return SigninFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
