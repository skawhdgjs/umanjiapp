package com.umanji.umanjiapp.ui.newMain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.BaseFragment;

/**
 * Created by nam on 2017. 2. 20..
 */

public class WorldFragment extends BaseFragment {

    public static WorldFragment newInstance(Bundle bundle) {
        WorldFragment fragment = new WorldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void updateView() {

    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_world_list, container, false);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void initWidgets(View view) {

    }

}
