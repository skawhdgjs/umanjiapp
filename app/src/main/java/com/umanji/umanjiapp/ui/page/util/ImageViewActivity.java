package com.umanji.umanjiapp.ui.page.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;
import com.umanji.umanjiapp.ui.page.auth.SignupFragment;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotFragment;

public class ImageViewActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return ImageViewFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

}
