package com.umanji.umanjiapp.ui.channel._fragment.spots;

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
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class SpotListFragment extends BaseChannelListFragment {
    private static final String TAG = "SpotListFragment";

    private Button mAddBtn;
    private LinearLayout mlayout;

    public static SpotListFragment newInstance(Bundle bundle) {
        SpotListFragment fragment = new SpotListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_spots, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new SpotListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {
        mAddBtn = (Button)view.findViewById(R.id.addChannelBtn);
        mAddBtn.setOnClickListener(this);

        mlayout = (LinearLayout) view.findViewById(R.id.spotLayout);

        switch (mChannel.getType()) {
            case TYPE_USER:
                mAddBtn.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void loadMoreData() {
        isLoading = true;
        mLoadCount = mLoadCount + 1;

        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging
            params.put("type", TYPE_SPOT_INNER);

            if(mChannel.getLevel() != LEVEL_LOCAL) {
                params.put("sort", "point DESC");
            }

            switch (mChannel.getType()) {
                case TYPE_USER:
                    params.put("owner", mChannel.getId());
                    params.put("type", TYPE_PROFILE_SPOTS);
                    break;
                case TYPE_INFO_CENTER:
                case TYPE_COMMUNITY:
                    setAddressParams(params, mChannel);
                    break;
                default:
                    params.put("parent", mChannel.getId());
                    break;

            }

            mApi.call(api_channels_spots_find, params, new AjaxCallback<JSONObject>() {
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_create:
                String parentId = event.response.optString("parent");
                if(TextUtils.equals(mChannel.getId(), parentId)) {
                    mChannel = channelData.getParent();
                    mAdapter.addTop(channelData);
                    mAdapter.notifyDataSetChanged();
                }
                mlayout.setBackgroundResource(R.color.feed_bg);
                break;

            case api_channels_id_delete:
                updateView();
                break;

        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.addChannelBtn:
                Helper.startCreateActivity(mActivity, mChannel, TYPE_SPOT);
                break;
        }
    }
}
