package com.umanji.umanjiapp.ui.channel.appointment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class AppointCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "AppointCreateFragment";

    private TextView tv ;
    private String c;

    public static AppointCreateFragment newInstance(Bundle bundle) {
        AppointCreateFragment fragment = new AppointCreateFragment();
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
    public void initWidgets(View view) {
        super.initWidgets(view);

        c = mChannel.getAddress().toString();

        tv = (TextView) view.findViewById(R.id.bundleTest);
        tv.setText(c);

        mSubmitBtn.setText("임명 제출");
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_appoint_create, container, false);
    }

    @Override
    protected void request() {
        try {
            JSONObject params = new JSONObject();
            setUserDesc(params);

            ArrayList<String> roles = new ArrayList<>();
            roles.add("umanji_cow");
            params.put("roles", new JSONArray(roles));

            mApi.call(api_channels_id_update, params);  // 1st must changed

        }catch(JSONException e) {
            Log.e("AppointCreateFragment", "error " + e.toString());
        }
    }

    protected void setUserDesc(JSONObject params) throws JSONException {

        params.put("id", mChannel.getId());

    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_id_update:
                mActivity.finish();
                EventBus.getDefault().post(new SuccessData(EVENT_UPDATEVIEW, null));
                break;
        }
    }


}
