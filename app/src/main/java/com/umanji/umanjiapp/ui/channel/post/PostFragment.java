package com.umanji.umanjiapp.ui.channel.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.BaseFragment;

public class PostFragment extends BaseFragment {
    private static final String TAG = "PostFragment";

    public static PostFragment newInstance(Bundle bundle) {
        PostFragment fragment = new PostFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_post, container, false);
    }

    @Override
    public void initWidgets(View view) {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void updateView() {

    }


}
