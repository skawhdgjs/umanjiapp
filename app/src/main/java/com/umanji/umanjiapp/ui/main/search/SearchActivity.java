package com.umanji.umanjiapp.ui.main.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.modal.imageview.ImageViewFragment;


public class SearchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return SearchFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
