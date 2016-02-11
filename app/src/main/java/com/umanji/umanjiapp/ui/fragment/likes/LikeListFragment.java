package com.umanji.umanjiapp.ui.fragment.likes;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class LikeListFragment extends BaseChannelListFragment {
    private static final String TAG = "LikeListFragment";


    private Button mLikeBtn;
    private Button mUnLikeBtn;

    public static LikeListFragment newInstance(Bundle bundle) {
        LikeListFragment fragment = new LikeListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_channels_like;
        mListApiName = api_channels_likes_find;
        mType   = TYPE_LIKE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.fragment_likes, container, false);

        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);



        mLikeBtn = (Button)view.findViewById(R.id.likeBtn);
        mLikeBtn.setOnClickListener(this);

        mUnLikeBtn = (Button)view.findViewById(R.id.unLikeBtn);
        mUnLikeBtn.setOnClickListener(this);

        updateView();
        return view;
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new LikeListAdapter(getActivity(), this, mChannel);
    }

    @Override
    public void updateView() {
        super.updateView();

        switch (mLevel) {
            case LEVEL_LOCAL:
                mLikeBtn.setVisibility(View.VISIBLE);

                String actionId = mChannel.getActionId(TYPE_LIKE, AuthHelper.getUserId(mContext));

                if(!TextUtils.isEmpty(actionId)) {
                    mLikeBtn.setVisibility(View.GONE);
                    mUnLikeBtn.setVisibility(View.VISIBLE);

                }else {
                    mLikeBtn.setVisibility(View.VISIBLE);
                    mUnLikeBtn.setVisibility(View.GONE);
                }

                break;
            default:
                mLikeBtn.setVisibility(View.GONE);
                break;
        }

        mLikeBtn.setEnabled(true);
        mUnLikeBtn.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.likeBtn:
                try {
                    JSONObject params = mChannel.getAddressJSONObject();
                    params.put("parent", mId);
                    params.put("type", mType);
                    params.put("name", AuthHelper.getUserName(mContext));

                    mApiHelper.call(api_channels_like, params);
                    mLikeBtn.setEnabled(false);
                    mUnLikeBtn.setEnabled(false);

                }catch(JSONException e) {
                    Log.e("BaseChannelCreate", "error " + e.toString());
                }

                break;
            case R.id.unLikeBtn:
                try {
                    String actionId = mChannel.getActionId(TYPE_LIKE, AuthHelper.getUserId(mContext));

                    JSONObject params = new JSONObject();
                    params.put("parent", mChannel.getId());
                    params.put("id", actionId);

                    mApiHelper.call(api_channels_unLike, params);
                    mLikeBtn.setEnabled(false);
                    mUnLikeBtn.setEnabled(false);

                }catch(JSONException e) {
                    Log.e("BaseChannelCreate", "error " + e.toString());
                }

                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        ChannelData channelData = new ChannelData(event.response);
        if(api_channels_unLike.equals(event.type)) {
            if(TextUtils.equals(mId, channelData.getId())){
                loadData();

                try {
                    String parent = event.response.getString("parent");
                    JSONObject params = new JSONObject();
                    params.put("id", parent);
                    mApiHelper.call(api_channels_get, params);
                } catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
            }

            return;
        }
    }
}
