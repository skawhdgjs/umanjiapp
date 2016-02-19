package com.umanji.umanjiapp.ui.channel._fragment.posts;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class PostListFragment extends BaseChannelListFragment {
    private static final String TAG = "PostListFragment";

    public static PostListFragment newInstance(Bundle bundle) {
        PostListFragment fragment = new PostListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new PostListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {

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

            if(mChannel.getLevel() <= LEVEL_DONG) {
                params.put("sort", "point DESC");
            }

            switch (mChannel.getType()) {
                case TYPE_USER:
                    params.put("owner", mChannel.getId());
                    break;
                case TYPE_INFO_CENTER:
                    setAddressParams(params, mChannel);
                    break;
                case TYPE_COMMUNITY:
                    params.put("level", mChannel.getLevel());
                    setAddressParams(params, mChannel);
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


                            } else {
                                for(int idx = 0; idx < jsonArray.length(); idx++) {
                                    JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                    ChannelData doc = new ChannelData(jsonDoc);
                                    mAdapter.addBottom(doc);
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
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
