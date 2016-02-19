package com.umanji.umanjiapp.ui.channel._fragment.noties;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.NotyData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class NotyListFragment extends BaseChannelListFragment {
    private static final String TAG = "NotyListFragment";

    public static NotyListFragment newInstance(Bundle bundle) {
        NotyListFragment fragment = new NotyListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_links, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new NotyListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {
    }

    @Override
    public void loadMoreData() {
        isLoading = true;

        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging

            mApi.call(api_noites_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            boolean hasNewNoty = false;
                            JSONArray jsonArray = object.getJSONArray("data");
                            for(int idx = 0; idx < jsonArray.length(); idx++) {
                                JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                NotyData notyData = new NotyData(jsonDoc);

                                if(!hasNewNoty) {
                                    hasNewNoty = !notyData.isRead();
                                }
                                ((NotyListAdapter)mAdapter).addBottom(notyData);
                                mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                            }

                            if(hasNewNoty) {
                                mApi.call(api_noites_read);
                            }

                        } catch(JSONException e) {
                            Log.e(TAG, "error " + e.toString());
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

        switch (v.getId()) {
            case R.id.addChannelBtn:
                Intent intent = new Intent(mActivity, SpotCreateActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                intent.putExtra("bundle", bundle);

                startActivity(intent);

                break;
        }
    }
}
