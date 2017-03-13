package com.umanji.umanjiapp.ui.newMain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.umanji.umanjiapp.ui.BaseActivity;

public class NewMainActivity extends BaseActivity {


    @Override
    protected Fragment createFragment() {
        return NewMainFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
