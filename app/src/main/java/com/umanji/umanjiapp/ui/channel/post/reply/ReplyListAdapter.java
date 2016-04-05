package com.umanji.umanjiapp.ui.channel.post.reply;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;


public class ReplyListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "ReplyListAdapter";

    public ReplyListAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public ReplyListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_post, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData       = mChannels.get(position);
        if(channelData == null || channelData.getOwner() == null || TextUtils.isEmpty(channelData.getOwner().getId())) return;

        setUserName(holder, channelData.getOwner());
        setPoint(holder, channelData);
        setName(holder, channelData);
        setParentName(holder, channelData.getParent());
        setSurvey(holder, channelData);
        setPreview(holder, channelData);
        setPhoto(holder, channelData);
        setUserPhoto(holder, channelData.getOwner());
        setActionPanel(holder, channelData);
        setCreatedAt(holder, channelData);
    }

    protected void setName(final ViewHolder holder, final ChannelData channelData) {

        if(TextUtils.isEmpty(channelData.getName())) {
            holder.name.setText("내용없음");
        } else {
            holder.name.setText(channelData.getName());
        }
    }
}
