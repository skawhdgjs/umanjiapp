package com.umanji.umanjiapp.ui.channel.profile.update;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelUpdateFragment;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class ProfileUpdateFragment extends BaseChannelUpdateFragment {
    private static final String TAG = "ProfileUpdateFragment";

    private TextView mEmail;

    public static ProfileUpdateFragment newInstance(Bundle bundle) {
        ProfileUpdateFragment fragment = new ProfileUpdateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        updateView();
        return view;
    }

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        mEmail = (TextView) view.findViewById(R.id.email);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_profile_update, container, false);
    }


    @Override
    protected void request() {
        try {
            JSONObject params = new JSONObject();
            setChannelParams(params);
            mApi.call(api_channels_id_update, params);

        }catch(JSONException e) {
            Log.e("BaseChannelUpdate", "error " + e.toString());
        }
    }

    @Override
    public void updateView() {
        super.updateView();

        setEmail(mActivity, mChannel);
        setName(mActivity, mChannel);
        setPhoto(mActivity, mChannel);
    }

    protected void setEmail(Activity activity, final ChannelData channelData) {
        mEmail.setText(mChannel.getEmail().toString());
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_id_update:
                mActivity.finish();
                EventBus.getDefault().post(new SuccessData(EVENT_UPDATEVIEW));
                break;
        }
    }


}
