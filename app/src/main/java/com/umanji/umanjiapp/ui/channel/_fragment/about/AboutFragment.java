package com.umanji.umanjiapp.ui.channel._fragment.about;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.umanji.umanjiapp.ui.channel.community.update.CommunityUpdateActivity;
import com.umanji.umanjiapp.ui.channel.complex.update.ComplexUpdateActivity;
import com.umanji.umanjiapp.ui.channel.spot.update.SpotUpdateActivity;
import com.umanji.umanjiapp.ui.modal.map.update_address.MapUpdateAddressActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class AboutFragment extends BaseChannelListFragment {
    private static final String TAG = "AboutFragment";

    protected Button mAddBtn;
    protected Button mEditChannelBtn;
    protected Button mDeleteBtn;
    protected Button mAddHomeBtn;
    protected TextView mAddress;

    private AlertDialog.Builder mAlert;

    public static AboutFragment newInstance(Bundle bundle) {
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new KeywordListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {
        mAddBtn = (Button)view.findViewById(R.id.addKeywordBtn);
        mAddBtn.setOnClickListener(this);

        mEditChannelBtn = (Button) view.findViewById(R.id.editChannelBtn);
        mEditChannelBtn.setOnClickListener(this);

        mDeleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        mDeleteBtn.setOnClickListener(this);

        mAddHomeBtn = (Button) view.findViewById(R.id.addHomeBtn);
        mAddHomeBtn.setOnClickListener(this);

        mAddress = (TextView) view.findViewById(R.id.address);

        mAlert = new AlertDialog.Builder(mActivity);

        mAddress.setText(Helper.getFullAddress(mChannel));
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
        //mAddress.setText(Helper.getFullAddress(mChannel));
        mAdapter.notifyDataSetChanged();

        setAddBtn(mActivity, mChannel);
        setDeleteBtn(mActivity, mChannel);
    }

    private void setAddBtn(Activity activity, ChannelData channelData) {
        switch (channelData.getLevel()) {
            case LEVEL_LOCAL:
                mAddBtn.setVisibility(View.VISIBLE);
                mEditChannelBtn.setVisibility(View.VISIBLE);
                break;

            case LEVEL_COMPLEX:
                mAddBtn.setVisibility(View.VISIBLE);
                mEditChannelBtn.setVisibility(View.VISIBLE);
                break;

            default:
                mAddBtn.setVisibility(View.GONE);
                mEditChannelBtn.setVisibility(View.GONE);
                break;
        }
    }

    private void setDeleteBtn(Activity activity, ChannelData channelData) {
        int loginUserLevel = Integer.parseInt(AuthHelper.getLevel(mActivity));
        switch (channelData.getType()) {
            case TYPE_SPOT:
                if(loginUserLevel < channelData.getLevel()) {
                    mDeleteBtn.setVisibility(View.VISIBLE);
                }else {
                    mDeleteBtn.setVisibility(View.GONE);
                }
                break;
            default:
                if(loginUserLevel < channelData.getLevel() ||
                        channelData.isOwner(AuthHelper.getUserId(mActivity))) {
                    mDeleteBtn.setVisibility(View.VISIBLE);
                }else {
                    mDeleteBtn.setVisibility(View.GONE);
                }
                break;
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
            case R.id.addKeywordBtn:
                Helper.startCreateActivity(mActivity, mChannel, TYPE_KEYWORD);
                break;

            case R.id.editChannelBtn:
                startChannelUpdateActivity(mActivity, mChannel);
                break;

            case R.id.deleteBtn:
                showDeleteChannelDialog();
                break;
            case R.id.addHomeBtn:
                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                bundle.putString("mapType", MAP_UPDATE_ADDRESS);
                Intent intent = new Intent(mActivity, MapUpdateAddressActivity.class);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
        }
    }

    private void startChannelUpdateActivity(Activity activity, ChannelData channelData) {
        Intent intent = null;

        switch (channelData.getType()) {
            case TYPE_SPOT_INNER:
            case TYPE_SPOT:
                intent = new Intent(activity, SpotUpdateActivity.class);
                break;
            case TYPE_COMMUNITY:
                intent = new Intent(activity, CommunityUpdateActivity.class);
                break;
            case TYPE_INFO_CENTER:
                break;
            case TYPE_USER:
                break;
            case TYPE_COMPLEX:
                intent = new Intent(activity, ComplexUpdateActivity.class);
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());
        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }
    private void showDeleteChannelDialog() {
        mAlert.setPositiveButton(R.string.delete_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                try {
                    JSONObject params = new JSONObject();
                    params.put("id", mChannel.getId());
                    mApi.call(api_channels_id_delete, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject object, AjaxStatus status) {
                            dialog.cancel();
                            mActivity.finish();
                            EventBus.getDefault().post(new SuccessData(api_channels_id_delete, object));
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Error " + e.toString());
                }
            }
        });

        mAlert.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mAlert.setTitle(R.string.delete_confirm);
        mAlert.show();
    }
}
