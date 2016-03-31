package com.umanji.umanjiapp.ui.channel.duty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class DutyCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "DutyCreateFragment";

    private TextView mAd_area;
    private EditText mAd_admin;
    private Button mConfirm;

    private TextView tv;
    private String c;


    private String roleName;

    public static DutyCreateFragment newInstance(Bundle bundle) {
        DutyCreateFragment fragment = new DutyCreateFragment();
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
        return inflater.inflate(R.layout.activity_appoint_create, container, false);
    }

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        mAd_area = (TextView) view.findViewById(R.id.ad_area);
        mAd_admin = (EditText) view.findViewById(R.id.ad_admin);
        mConfirm = (Button) view.findViewById(R.id.confirm);
        mConfirm.setOnClickListener(this);

        int mAreaLevelInt = mChannel.getLevel();
        String mAreaLevel = Integer.toString(mAreaLevelInt);
        String mAreaName = mChannel.getAdminArea();
        String mLocalityName = mChannel.getLocality();
        String mToroughfareName = mChannel.getThoroughfare();

        if (mAreaLevel.equals("8")) {
            mAd_area.setText(mAreaName);
            roleName = "ad_admin";
        } else if (mAreaLevel.equals("12")) {
            mAd_area.setText(mLocalityName);
            roleName = "ad_locality";
        } else if (mAreaLevel.equals("14")) {
            mAd_area.setText(mToroughfareName);
            roleName = "ad_thoroughfare";
        }


        c = mChannel.getAddress().toString();
        tv = (TextView) view.findViewById(R.id.bundleTest);
        tv.setText(c);

        mSubmitBtn.setText("임명 제출");
    }

    @Override
    protected void request() {

        try {
            JSONObject params = new JSONObject();
            setUserDesc(params);

            ArrayList<String> roles = new ArrayList<>();
            roles.add(roleName);
            params.put("roles", new JSONArray(roles));

            mApi.call(api_profile_role_update, params);

        } catch (JSONException e) {
            Log.e("DutyCreateFragment", "error " + e.toString());
        }
    }

    protected void setUserDesc(JSONObject params) throws JSONException {
        params.put("email", mAd_admin.getText().toString());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.confirm:

                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_profile_role_update:
                mActivity.finish();
                EventBus.getDefault().post(new SuccessData(EVENT_UPDATEVIEW, null));
                break;

            case api_channels_findEmail:

                Toast.makeText(mActivity, "성공", Toast.LENGTH_LONG).show();
                break;
        }
    }


}
