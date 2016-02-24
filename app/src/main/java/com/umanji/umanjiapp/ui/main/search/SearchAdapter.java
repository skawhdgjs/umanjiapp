package com.umanji.umanjiapp.ui.main.search;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class SearchAdapter extends BaseChannelListAdapter {
    private static final String TAG = "SearchAdapter";

    public SearchAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public SearchAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_spot, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData       = mChannels.get(position);

        setPoint(holder, channelData);
        setName(holder, channelData);
        setKeywords(holder, channelData);
        setPhoto(holder, channelData);
        setUserPhoto(holder, channelData);

        setSelectEvent(holder, channelData);
    }

    protected void setName(final ViewHolder holder, final ChannelData channelData) {

        if(TextUtils.isEmpty(channelData.getName())) {
            holder.name.setText("이름없음");
        } else {
            holder.name.setText(channelData.getName());
        }
    }

    protected void setSelectEvent(final ViewHolder holder, final ChannelData channelData) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SuccessData(EVENT_LOOK_AROUND, channelData.getJsonObject()));
            }
        });
    }

    protected void setPhoto(final ViewHolder holder, ChannelData channelData) {
        String photo = channelData.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .placeholder(R.drawable.dark_bg)
                    .into(holder.photo);
        } else {
            switch (channelData.getType()) {
                case TYPE_SPOT:
                case TYPE_SPOT_INNER:
                    Glide.with(mActivity)
                            .load(R.drawable.spot_default)
                            .into(holder.photo);
                    break;
                case TYPE_COMMUNITY:
                    Glide.with(mActivity)
                            .load(R.drawable.community_default)
                            .into(holder.photo);
                    break;
                case TYPE_COMPLEX:
                    Glide.with(mActivity)
                            .load(R.drawable.complex_background)
                            .into(holder.photo);
                    break;
            }

        }
    }
}
