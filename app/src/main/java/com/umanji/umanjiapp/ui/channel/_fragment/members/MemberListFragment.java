package com.umanji.umanjiapp.ui.channel._fragment.members;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class MemberListFragment extends BaseChannelListFragment {
    private static final String TAG = "MemberListFragment";

    private Button mJoinBtn;
    private Button mUnJoinBtn;
    private LinearLayout mlayout;

    public static MemberListFragment newInstance(Bundle bundle) {
        MemberListFragment fragment = new MemberListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_members, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new MemberListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {
        mJoinBtn = (Button)view.findViewById(R.id.joinBtn);
        mJoinBtn.setOnClickListener(this);

        mUnJoinBtn = (Button)view.findViewById(R.id.unJoinBtn);
        mUnJoinBtn.setOnClickListener(this);

        mlayout = (LinearLayout) view.findViewById(R.id.memberLayout);
    }

    @Override
    public void loadMoreData() {
        isLoading = true;
        mLoadCount = mLoadCount + 1;

        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging
            params.put("type", TYPE_MEMBER);
            params.put("sort", "point DESC");

            switch (mChannel.getType()) {
                case TYPE_USER:
                    params.put("owner", mChannel.getId());
                    break;
                case TYPE_INFO_CENTER:
                    params.put("type", TYPE_USER);
                    setAddressParams(params, mChannel);
                    break;
                default:
                    params.put("parent", mChannel.getId());
                    break;
            }

            mApi.call(api_channels_members_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");

                            if(jsonArray.length() != 0) {

                                mlayout.setBackgroundResource(R.color.feed_bg);
                                
                                for(int idx = 0; idx < jsonArray.length(); idx++) {
                                    JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                    ChannelData doc = new ChannelData(jsonDoc);
                                    mAdapter.addBottom(doc);
                                }
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
        mJoinBtn.setEnabled(false);
        mUnJoinBtn.setEnabled(false);
        //mEmptyMember.setVisibility(View.GONE);

        switch (mChannel.getType()) {
            case TYPE_INFO_CENTER:
                mJoinBtn.setVisibility(View.GONE);
                mUnJoinBtn.setVisibility(View.GONE);
                break;

            default:
                mJoinBtn.setVisibility(View.VISIBLE);
                String actionId = mChannel.getActionId(TYPE_MEMBER, AuthHelper.getUserId(mActivity));

                if(TextUtils.isEmpty(actionId)) {
                    mJoinBtn.setVisibility(View.VISIBLE);
                    mJoinBtn.setEnabled(true);
                    mUnJoinBtn.setVisibility(View.GONE);
                }else {
                    mJoinBtn.setVisibility(View.GONE);
                    mUnJoinBtn.setVisibility(View.VISIBLE);
                    mUnJoinBtn.setEnabled(true);
                }
                break;
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_id_join:
                String parentId = event.response.optString("parent");
                if(TextUtils.equals(mChannel.getId(), parentId)) {
                    ChannelData channelData = new ChannelData(event.response);
                    mChannel = channelData.getParent();
                }

                loadData();
                break;
            case api_channels_id_unJoin:
                ChannelData channelData = new ChannelData(event.response);
                if(TextUtils.equals(mChannel.getId(), channelData.getId())) {
                    mChannel = channelData;
                }

                loadData();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.joinBtn:
                try {
                    JSONObject params = mChannel.getAddressJSONObject();
                    params.put("parent", mChannel.getId());
                    params.put("type", TYPE_MEMBER);
                    params.put("name", AuthHelper.getUserName(mActivity));

                    mApi.call(api_channels_id_join, params);
                    mJoinBtn.setEnabled(false);
                    mUnJoinBtn.setEnabled(false);

                }catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }

                break;
            case R.id.unJoinBtn:
                try {
                    String actionId = mChannel.getActionId(TYPE_MEMBER, AuthHelper.getUserId(mActivity));

                    JSONObject params = new JSONObject();
                    params.put("parent", mChannel.getId());
                    params.put("id", actionId);

                    mApi.call(api_channels_id_unJoin, params);
                    mJoinBtn.setEnabled(false);
                    mUnJoinBtn.setEnabled(false);

                }catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }

                break;
        }
    }
}
