package com.umanji.umanjiapp.ui.util.image;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;


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
