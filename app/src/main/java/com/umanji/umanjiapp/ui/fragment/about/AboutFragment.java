package com.umanji.umanjiapp.ui.fragment.about;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.page.channel.about.edit.AboutEditActivity;
import com.umanji.umanjiapp.ui.page.channel.keyword.create.KeywordCreateActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AboutFragment extends BaseChannelListFragment {
    private static final String TAG = "AboutFragment";

    /****************************************************
     *  View
     ****************************************************/
    protected Button mCreateKeywordBtn;
    protected Button mEditBackgroundBtn;
    protected Button mDeleteBtn;
    protected TextView mAddress;

    private AlertDialog.Builder mAlert;

    public static AboutFragment newInstance(Bundle bundle) {
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createKeyword;
        mListApiName = api_channels_keywords_find;
        mType   = TYPE_KEYWORD;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_about, container, false);
        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);

        mCreateKeywordBtn = (Button) view.findViewById(R.id.createKeywordBtn);
        mCreateKeywordBtn.setOnClickListener(this);

        mEditBackgroundBtn = (Button) view.findViewById(R.id.editBackgroundBtn);
        mEditBackgroundBtn.setOnClickListener(this);

        mDeleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        mDeleteBtn.setOnClickListener(this);

        mAddress = (TextView) view.findViewById(R.id.address);

        mAlert = new AlertDialog.Builder(mActivity);

        return view;
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new KeywordListAdapter(getActivity(), this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.createKeywordBtn:
                Intent intent = new Intent(mContext, KeywordCreateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                bundle.putString("type", mType);
                intent.putExtra("bundle", bundle);

                startActivityForResult(intent, UiHelper.CODE_ABOUT_FRAGMENT);
                break;

            case R.id.editBackgroundBtn:
                Intent aboutIntent = new Intent(mContext, AboutEditActivity.class);
                Bundle aboutBundle = new Bundle();
                aboutBundle.putString("channel", mChannel.getJsonObject().toString());
                aboutBundle.putString("type", mType);
                aboutIntent.putExtra("bundle", aboutBundle);

                startActivityForResult(aboutIntent, UiHelper.CODE_ABOUT_FRAGMENT);
                break;

            case R.id.deleteBtn:
                showCreateSpotDialog();
                break;
        }
    }

    @Override
    public void updateView() {
        super.updateView();

        mAddress.setText(mChannel.getCountryName() + " " + mChannel.getAdminArea() + " " + mChannel.getLocality() + " " + mChannel.getThoroughfare() + " " + mChannel.getFeatureName());

        switch (mLevel) {
            case LEVEL_LOCAL:
                mCreateKeywordBtn.setVisibility(View.VISIBLE);
                mEditBackgroundBtn.setVisibility(View.VISIBLE);
                break;
            default:
                mCreateKeywordBtn.setVisibility(View.GONE);
                mEditBackgroundBtn.setVisibility(View.GONE);
                break;
        }


        if(mChannel.isOwner(AuthHelper.getUserId(mActivity))) {
            mDeleteBtn.setVisibility(View.VISIBLE);
        }else {
            mDeleteBtn.setVisibility(View.GONE);
        }

    }



    private void showCreateSpotDialog() {
        mAlert.setPositiveButton(R.string.delete_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {

                    JSONObject params = new JSONObject();
                    params.put("id", mChannel.getId());
                    mApiHelper.call(api_channels_id_delete, params);
                    dialog.cancel();
                    mActivity.finish();
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
