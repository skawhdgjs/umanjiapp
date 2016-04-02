package com.umanji.umanjiapp.ui.channel._fragment.roles;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;

import org.json.JSONException;
import org.json.JSONObject;


public class RoleListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "RoleListAdapter";

    public RoleListAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public RoleListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_role, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData       = mChannels.get(position);
        setRole(holder, channelData);
    }

    protected void setRole(final ViewHolder holder, final ChannelData channelData){

        String[] roles ;
        String role = null;

        roles = mChannel.getRoles();
        if(roles != null) {
            role = roles[0];
        }

        holder.mRole.setText(role);
    }
}
