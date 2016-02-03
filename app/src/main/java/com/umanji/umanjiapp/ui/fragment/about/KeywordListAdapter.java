package com.umanji.umanjiapp.ui.fragment.about;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class KeywordListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "BaseChannelListAdapter";

    public KeywordListAdapter(Activity activity, Fragment fragment) {
        super(activity, fragment);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_keyword, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData doc = mChannels.get(position);
        holder.name.setText(doc.getName());

        String [] photos = doc.getPhotos();
        if(photos != null && photos[0] != null) {
            Glide.with(mActivity)
                    .load(photos[0])
                    .placeholder(R.drawable.empty)
                    .animate(R.anim.abc_fade_in)
                    .into(holder.photo);
        }
    }
}
