package com.umanji.umanjiapp.ui.channel.post.reply;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class ReplyFragment extends BaseChannelListFragment {
    private static final String TAG = "ReplyFragment";

    protected FloatingActionButton mFab;


    public static ReplyFragment newInstance(Bundle bundle) {
        ReplyFragment fragment = new ReplyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_reply, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new ReplyListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {

        mEmptyStates = (ImageView) view.findViewById(R.id.empty_states);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

    }

    @Override
    public void loadMoreData() {
        isLoading = true;
        mLoadCount = mLoadCount + 1;

        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging
            params.put("limit", 5);
            params.put("type", TYPE_POST);

            switch (mChannel.getType()) {
                case TYPE_USER:
                    params.put("owner", mChannel.getId());
                    break;
                default:
                    params.put("parent", mChannel.getId());
                    break;
            }


            mApi.call(api_channels_posts_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");

                            if(jsonArray.length() == 0) {

                                mEmptyStates.setVisibility(View.VISIBLE);
                                updateView();

                            } else {
                                for(int idx = 0; idx < jsonArray.length(); idx++) {
                                    JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                    ChannelData doc = new ChannelData(jsonDoc);

                                    if(doc != null && doc.getOwner() != null && !TextUtils.isEmpty(doc.getOwner().getId())) {
                                        mAdapter.addBottom(doc);
                                    }
                                }
                                updateView();
                            }
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

        if(AuthHelper.isLogin(mActivity)) {
            mFab.setVisibility(View.VISIBLE);
        }else {
            mFab.setVisibility(View.GONE);
        }
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

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                    Intent intent = new Intent(mActivity, PostCreateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", mChannel.getJsonObject().toString());
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                break;
        }
    }
}
