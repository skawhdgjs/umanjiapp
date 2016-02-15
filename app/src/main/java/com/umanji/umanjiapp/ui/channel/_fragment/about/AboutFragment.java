package com.umanji.umanjiapp.ui.channel._fragment.about;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.channel.keyword.create.KeywordCreateActivity;
import com.umanji.umanjiapp.ui.channel.spot.edit.SpotEditActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class AboutFragment extends BaseChannelListFragment {
    private static final String TAG = "AboutFragment";

    protected Button mAddBtn;
    protected Button mEditChannelBtn;
    protected Button mDeleteBtn;
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

        mAddress = (TextView) view.findViewById(R.id.address);

        mAlert = new AlertDialog.Builder(mActivity);
    }

    @Override
    public void loadData() {
        mAdapter.resetDocs();
        mAdapter.setCurrentPage(0);

        loadMoreData();
    }

    @Override
    public void loadMoreData() {
        isLoading = true;

        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging
            params.put("type", TYPE_KEYWORD);

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
        mAddress.setText(Helper.getFullAddress(mChannel));
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
            default:
                mAddBtn.setVisibility(View.GONE);
                mEditChannelBtn.setVisibility(View.GONE);
                break;
        }
    }

    private void setDeleteBtn(Activity activity, ChannelData channelData) {
        if(channelData.isOwner(AuthHelper.getUserId(mActivity))) {
            mDeleteBtn.setVisibility(View.VISIBLE);
        }else {
            mDeleteBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.addKeywordBtn:
                Intent intent = new Intent(mActivity, KeywordCreateActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                intent.putExtra("bundle", bundle);

                startActivity(intent);
                break;

            case R.id.editChannelBtn:
                Intent aboutIntent = new Intent(mActivity, SpotEditActivity.class);
                Bundle aboutBundle = new Bundle();
                aboutBundle.putString("channel", mChannel.getJsonObject().toString());
                aboutIntent.putExtra("bundle", aboutBundle);

                startActivity(aboutIntent);
                break;

            case R.id.deleteBtn:
                showCreateSpotDialog();
                break;
        }
    }

    private void showCreateSpotDialog() {
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
