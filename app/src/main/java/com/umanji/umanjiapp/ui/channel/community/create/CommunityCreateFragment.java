package com.umanji.umanjiapp.ui.channel.community.create;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CommunityCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "CommunityCreateFragment";



    protected AutoCompleteTextView mKeywordName;
    protected Button mAddKeywordBtn;

    protected TextView mKeyword1;
    protected TextView mKeyword2;

    ArrayList<String> mKeywords = new ArrayList<>();

    public static CommunityCreateFragment newInstance(Bundle bundle) {
        CommunityCreateFragment fragment = new CommunityCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        updateView();
        return view;
    }

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        mHeaderTitle = (TextView) view.findViewById(R.id.headerTitle);
        mHeaderTitle.setText("커뮤니티 생성");

        mSubmitBtn.setText("커뮤니티 생성");


        mKeywordName = (AutoCompleteTextView) view.findViewById(R.id.keywordName);
        mAddKeywordBtn = (Button) view.findViewById(R.id.addKeywordBtn);
        mAddKeywordBtn.setOnClickListener(this);

        mKeyword1 = (TextView) view.findViewById(R.id.keyword1);
        mKeyword1.setOnClickListener(this);

        mKeyword2 = (TextView) view.findViewById(R.id.keyword2);
        mKeyword2.setOnClickListener(this);

    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel_create, container, false);
    }

    @Override
    protected void request() {
        try {
            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
            params.put("parentType", mChannel.getType());
            params.put("level", mChannel.getLevel());
            params.put("name", mName.getText().toString());
            params.put("type", TYPE_COMMUNITY);


            if(mKeywords.size() > 0) {
                params.put("keywords", new JSONArray(mKeywords));
            }

            mApi.call(api_channels_createCommunity, params);

        }catch(JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.addKeywordBtn:
                if(TextUtils.isEmpty(mKeyword1.getText())) {
                    mKeyword1.setText(mKeywordName.getText() + " [X]");
                    mKeywords.add(mKeywordName.getText().toString());
                } else if(TextUtils.isEmpty(mKeyword2.getText())){
                    mKeyword2.setText(mKeywordName.getText() + " [X]");
                    mKeywords.add(mKeywordName.getText().toString());
                } else {
                    Toast.makeText(mActivity, "키워드는 2개까지 입력 가능합니다.", Toast.LENGTH_SHORT).show();
                }

                mKeywordName.setText(null);
                break;

            case R.id.keyword1:
                if(!TextUtils.isEmpty(mKeyword1.getText())) {
                    if(!TextUtils.isEmpty(mKeyword2.getText())) {
                        mKeyword1.setText(mKeyword2.getText());
                        mKeywords.remove(0);
                        mKeywords.remove(0);

                        mKeywords.add(mKeyword2.getText().toString());
                        mKeyword2.setText(null);
                    }else {
                        mKeyword1.setText(null);
                        mKeywords.remove(0);
                    }
                }
                break;
            case R.id.keyword2:
                if(!TextUtils.isEmpty(mKeyword2.getText())) {
                    mKeyword2.setText(null);
                    mKeywords.remove(1);
                }
                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_createCommunity:
                mActivity.finish();
                break;
        }
    }


}
