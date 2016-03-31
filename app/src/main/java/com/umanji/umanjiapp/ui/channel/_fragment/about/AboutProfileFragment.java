package com.umanji.umanjiapp.ui.channel._fragment.about;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.distribution.DistributionActivity;
import com.umanji.umanjiapp.ui.setting.home.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class AboutProfileFragment extends BaseChannelListFragment {
    private static final String TAG = "AboutFragment";

    protected Button mLogoutBtn;
    protected TextView mAddress;

    private Button mAddHomeBtn;
    private Button mDutyBtn;
    private TextView mUserName;

    public static AboutProfileFragment newInstance(Bundle bundle) {
        AboutProfileFragment fragment = new AboutProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_about_profile, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new KeywordListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {
        mLogoutBtn = (Button) view.findViewById(R.id.logoutBtn);
        mLogoutBtn.setOnClickListener(this);
        mAddress = (TextView) view.findViewById(R.id.address);

        mAddress.setText(Helper.getFullAddress(mChannel));

        mAddHomeBtn = (Button)view.findViewById(R.id.addHomeBtn);
        mAddHomeBtn.setOnClickListener(this);

        mUserName = (TextView)view.findViewById(R.id.userName);
        //mUserName.setOnClickListener(this);

        mDutyBtn = (Button) view.findViewById(R.id.dutyBtn);
        mDutyBtn.setOnClickListener(this);

        if(AuthHelper.isLoginUser(mActivity, mChannel.getId())) {
            mUserName.setOnClickListener(this);
            mUserName.setTextColor(Color.parseColor("#0066ff"));
        }

        String[] roles ;
        roles = mChannel.getRoles();
        String role = roles[0];

        if(role.equals("umanji_cow")) {
            mDutyBtn.setVisibility(View.VISIBLE);
        } else if(role.equals("ad_admin")){
            mDutyBtn.setVisibility(View.VISIBLE);
        } else if(role.equals("ad_locality")){
            mDutyBtn.setVisibility(View.VISIBLE);
        } else if(role.equals("ad_thoroughfare")){
            mDutyBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loadMoreData() {
        isLoading = true;
        mLoadCount = mLoadCount + 1;

        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging
            params.put("type", TYPE_KEYWORD);
            params.put("sort", "point DESC");

            switch (mChannel.getType()) {
                case TYPE_USER:
                    params.put("owner", mChannel.getId());
                    break;
                case TYPE_INFO_CENTER:
                case TYPE_COMMUNITY:
                    setAddressParams(params, mChannel);
                    break;
                default:
                    params.put("parent", mChannel.getId());
                    break;
            }

            mApi.call(api_channels_keywords_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for(int idx = 0; idx < jsonArray.length(); idx++) {
                                JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                ChannelData doc = new ChannelData(jsonDoc);
                                mAdapter.addBottom(doc);
                            }

                            updateView();

                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }

                        isLoading = false;
                    }
                }
            });
            mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    @Override
    public void updateView() {
        mAddress.setText(Helper.getFullAddress(mChannel));
        mUserName.setText(mChannel.getUserName());
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_create:
                String parentId = event.response.optString("parent");
                if(TextUtils.equals(mChannel.getId(), parentId)) {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("id", mChannel.getId());
                        mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject object, AjaxStatus status) {
                                mChannel = new ChannelData(object);
                            }
                        });
                        updateView();
                    } catch(JSONException e) {
                        Log.e(TAG, "error " + e.toString());
                    }

                    if(TextUtils.isEmpty(channelData.getId())) {
                        Toast.makeText(mActivity, "이미 존재하는 키워드 입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        mAdapter.addTop(channelData);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.logoutBtn:
                JSONObject params = new JSONObject();
                mApi.call(api_logout, params);
                mActivity.finish();
                break;
            case R.id.addHomeBtn:
                Intent homeIntent = new Intent(mActivity, HomeActivity.class);
                Bundle homeBundle = new Bundle();
                homeBundle.putString("channel", mChannel.getJsonObject().toString());
                homeIntent.putExtra("bundle", homeBundle);
                startActivity(homeIntent);
                break;

            case R.id.dutyBtn:
                Intent roleIntent = new Intent(mActivity, DistributionActivity.class);
                Bundle roleBundle = new Bundle();
                roleBundle.putString("channel", mChannel.getJsonObject().toString());
                roleIntent.putExtra("bundle", roleBundle);
                startActivity(roleIntent);
                break;

            case R.id.userName:
                Helper.startUpdateActivity(mActivity, mChannel);
                break;

        }
    }

}
