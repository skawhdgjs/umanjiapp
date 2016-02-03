package com.umanji.umanjiapp.ui.page.channel.post.create;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelCreateFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class PostCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "BaseChannelCreateFragment";

    public static PostCreateFragment newInstance(Bundle bundle) {
        PostCreateFragment fragment = new PostCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createPost;
        mType = TYPE_POST;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_post_create, container, false);
        return view;
    }
}
