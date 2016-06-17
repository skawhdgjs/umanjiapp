package com.umanji.umanjiapp.ui.channel._fragment.posts;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;


public class PostListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "PostListAdapter";

    public PostListAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public PostListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
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
        setParentType(holder, channelData.getParent());
        setKeywords(holder, channelData);
    }

    protected void setKeywords(final ViewHolder holder, ChannelData channelData) {
        String [] keywords = channelData.getKeywords();


        if(keywords != null && keywords.length > 0) {
            String keyword = "";
            for(int idx=0; idx < keywords.length; idx++) {
                keyword = keyword + "#" + keywords[idx] + " ";
            }
            holder.keyword.setText(keyword);
            holder.keyword.setVisibility(View.VISIBLE);
        }else {
            holder.keyword.setText("키워드 없음");
            holder.keyword.setVisibility(View.GONE);
        }
    }
}
