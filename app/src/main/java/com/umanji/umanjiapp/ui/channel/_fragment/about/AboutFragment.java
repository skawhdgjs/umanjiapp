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

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.channel.advertise.AdsCreateActivity;
import com.umanji.umanjiapp.ui.channel.community.update.CommunityUpdateActivity;
import com.umanji.umanjiapp.ui.channel.complex.update.ComplexUpdateActivity;
import com.umanji.umanjiapp.ui.channel.keyword.create.KeywordCreateActivity;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateActivity;
import com.umanji.umanjiapp.ui.channel.spot.update.SpotUpdateActivity;
import com.umanji.umanjiapp.ui.modal.map.update_address.MapUpdateAddressActivity;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class AboutFragment extends BaseChannelListFragment {
    private static final String TAG = "AboutFragment";

    protected TextView mEditChannelBtn;
    protected Button mDeleteBtn;
    protected TextView mAddHomeBtn;
    protected TextView advertiseBtn;
    protected TextView mAddress;

    protected TextView mName;

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
        mEditChannelBtn = (TextView) view.findViewById(R.id.editChannelBtn);
        mEditChannelBtn.setOnClickListener(this);

        mDeleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        mDeleteBtn.setOnClickListener(this);

        mAddHomeBtn = (TextView) view.findViewById(R.id.addHomeBtn);
        mAddHomeBtn.setOnClickListener(this);

        advertiseBtn = (TextView) view.findViewById(R.id.advertiseBtn);
        advertiseBtn.setOnClickListener(this);

        mAddress = (TextView) view.findViewById(R.id.address);

        mAlert = new AlertDialog.Builder(mActivity);

        mName = (TextView) view.findViewById(R.id.name);
        mName.setText(mChannel.getName());

        mAddress.setText(Helper.getFullAddress(mChannel));
    }

    @Override
    public void loadMoreData() {

    }

    @Override
    public void updateView() {
        //mAddress.setText(Helper.getFullAddress(mChannel));
        mAdapter.notifyDataSetChanged();

        setAddBtn(mActivity, mChannel);
        setDeleteBtn(mActivity, mChannel);
    }

    private void setAddBtn(Activity activity, ChannelData channelData) {

        if(TextUtils.equals(channelData.getType(), TYPE_INFO_CENTER)) {
            mEditChannelBtn.setVisibility(View.GONE);
        } else {
            mEditChannelBtn.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
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

            case R.id.advertiseBtn:
                Bundle adsBundle = new Bundle();
                adsBundle.putString("channel", mChannel.getJsonObject().toString());
                Intent adsIntent = new Intent(mActivity, AdsCreateActivity.class);
                adsIntent.putExtra("bundle", adsBundle);
                startActivity(adsIntent);
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
                intent = new Intent(activity, SpotUpdateActivity.class);
                break;
            case TYPE_USER:
                break;
            case TYPE_COMPLEX:
                intent = new Intent(activity, ComplexUpdateActivity.class);
                break;

            case TYPE_KEYWORD_COMMUNITY:
                intent = new Intent(activity, SpotUpdateActivity.class);
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
