package com.umanji.umanjiapp.ui.channel.community.update;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelUpdateFragment;
import com.umanji.umanjiapp.ui.channel.spot.update.SpotUpdateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class CommunityUpdateFragment extends BaseChannelUpdateFragment {
    private static final String TAG = "CommunityUpdateFragment";


    protected EditText mKeywordName;
    protected Button mAddKeywordBtn;

    protected LinearLayout mKeywordPanel;

    protected TextView mKeyword1;
    protected TextView mKeyword2;

    ArrayList<String> mKeywords = new ArrayList<>();


    public static CommunityUpdateFragment newInstance(Bundle bundle) {
        CommunityUpdateFragment fragment = new CommunityUpdateFragment();
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


        mKeywordName = (EditText) view.findViewById(R.id.keywordName);
        mAddKeywordBtn = (Button) view.findViewById(R.id.addKeywordBtn);
        mAddKeywordBtn.setOnClickListener(this);

        mKeyword1 = (TextView) view.findViewById(R.id.keyword1);
        mKeyword1.setOnClickListener(this);

        mKeyword2 = (TextView) view.findViewById(R.id.keyword2);
        mKeyword2.setOnClickListener(this);

        mKeywordPanel = (LinearLayout) view.findViewById(R.id.keywordPanel);

        updateView();
        return view;


    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel_update, container, false);
    }


    @Override
    protected void request() {
        try {
            JSONObject params = new JSONObject();
            setChannelParams(params);

            params.put("keywords", new JSONArray(mKeywords));

            mApi.call(api_channels_id_update, params);

        }catch(JSONException e) {
            Log.e("BaseChannelUpdate", "error " + e.toString());
        }
    }


    @Override
    public void updateView() {
        super.updateView();

        setName(mActivity, mChannel);
        setPhoto(mActivity, mChannel);

        setKeywords(mActivity, mChannel);
    }

    protected void setKeywords(Activity activity, final ChannelData channelData) {

        String [] keywords = channelData.getKeywords();
        if(keywords == null) return;
        if(keywords.length == 1) {
            mKeywordPanel.setVisibility(View.VISIBLE);
            mKeywords.add(keywords[0]);
            mKeyword1.setText(keywords[0] + " [X]");
        } else if(keywords.length == 2){
            mKeywordPanel.setVisibility(View.VISIBLE);
            mKeywords.add(keywords[0]);
            mKeyword1.setText(keywords[0] + " [X]");

            mKeywords.add(keywords[1]);
            mKeyword2.setText(keywords[1] + " [X]");
        } else {
            mKeywordPanel.setVisibility(View.VISIBLE);
            mKeywords.add(keywords[0]);
            mKeyword1.setText(keywords[0] + " [X]");

            mKeywords.add(keywords[1]);
            mKeyword2.setText(keywords[1] + " [X]");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.addKeywordBtn:
                if(TextUtils.isEmpty(mKeyword1.getText())) {
                    mKeywordPanel.setVisibility(View.VISIBLE);

                    String dictionaryKeyword;
                    dictionaryKeyword = Helper.dictionaryHasKeyword(mKeywordName.getText().toString());

                    mKeyword1.setText(dictionaryKeyword + " [X]");
                    mKeywords.add(dictionaryKeyword);

//                    mKeyword1.setText(mKeywordName.getText() + " [X]");
//                    mKeywords.add(mKeywordName.getText().toString());
                } else if(TextUtils.isEmpty(mKeyword2.getText())){

                    String dictionaryKeyword2;
                    dictionaryKeyword2 = Helper.dictionaryHasKeyword(mKeywordName.getText().toString());

                    mKeyword2.setText(dictionaryKeyword2 + " [X]");
                    mKeywords.add(dictionaryKeyword2);

//                    mKeyword2.setText(mKeywordName.getText() + " [X]");
//                    mKeywords.add(mKeywordName.getText().toString());
                    updateView();
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
            case api_channels_id_update:
                mActivity.finish();
                EventBus.getDefault().post(new SuccessData(EVENT_UPDATEVIEW, null));
                break;
        }
    }


}
