package com.umanji.umanjiapp.ui.channel._fragment.spots.complex;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.spots.SpotListAdapter;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateActivity;
import com.umanji.umanjiapp.ui.modal.map.MapActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class ComplexSpotListFragment extends BaseChannelListFragment {
    private static final String TAG = "ComplexSpotListFragment";

    private Button mAddBtn;

    public static ComplexSpotListFragment newInstance(Bundle bundle) {
        ComplexSpotListFragment fragment = new ComplexSpotListFragment();
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
        return new ComplexSpotListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {
        mAddBtn = (Button)view.findViewById(R.id.addChannelBtn);
        mAddBtn.setOnClickListener(this);
    }

    @Override
    public void loadData() {
        try {
            JSONObject params = new JSONObject();
            params.put("id", mChannel.getId());

            mApi.call(api_complex_findSpots, params, new AjaxCallback<JSONObject>() {
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
                    }
                }
            });
            mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public void loadMoreData() {
    }

    @Override
    public void updateView() {
        mAddBtn.setText("스팟 연결하기");
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

                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                bundle.putString("mapType", MAP_CREATE_COMPLEX);
                Intent intent = new Intent(mActivity, MapActivity.class);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
        }
    }
}
