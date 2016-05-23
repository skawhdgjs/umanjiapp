package com.umanji.umanjiapp.ui.channel.spot.create;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SpotCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "SpotCreateFragment";

    private AutoCompleteTextView mFloor;
    private CheckBox mBasementCheckBox;
    private boolean isBasement = false;


    protected AutoCompleteTextView mKeywordName;
    protected Button mAddKeywordBtn;

    protected TextView mKeyword1;
    protected TextView mKeyword2;

    ArrayList<String> keywords = new ArrayList<>();

    public static SpotCreateFragment newInstance(Bundle bundle) {
        SpotCreateFragment fragment = new SpotCreateFragment();
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
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_spot_create, container, false);
    }

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        mFloor = (AutoCompleteTextView) view.findViewById(R.id.floor);
        mBasementCheckBox = (CheckBox) view.findViewById(R.id.basementCheckBox);
        mBasementCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.basementCheckBox) {
                    if (isChecked) {
                        isBasement = true;
                    } else {
                        isBasement = false;
                    }
                }
            }
        });

        mSubmitBtn.setText("스팟 생성");


        mKeywordName = (AutoCompleteTextView) view.findViewById(R.id.keywordName);
        mAddKeywordBtn = (Button) view.findViewById(R.id.addKeywordBtn);
        mAddKeywordBtn.setOnClickListener(this);

        mKeyword1 = (TextView) view.findViewById(R.id.keyword1);
        mKeyword1.setOnClickListener(this);

        mKeyword2 = (TextView) view.findViewById(R.id.keyword2);
        mKeyword2.setOnClickListener(this);
    }

    @Override
    protected void request() {
        if(mClicked == true){
            Toast.makeText(mActivity,"이미 요청했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
            params.put("name", mName.getText().toString());
            params.put("type", TYPE_SPOT_INNER);


            if(keywords.size() > 0) {
                params.put("keywords", new JSONArray(keywords));
            }

            setSpotDesc(params);

            if(mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            }

            mApi.call(api_channels_create, params);
            mClicked = true;

        }catch(JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }
    }

    protected void setSpotDesc(JSONObject params) throws JSONException {
        String floor = mFloor.getText().toString();
        if(TextUtils.isEmpty(mFloor.getText().toString())){
            floor = "1";
            //return;
        }

        int floorNum = Integer.parseInt(floor);

        if(isBasement) {
            floorNum = floorNum * -1;
        }


        JSONObject descParams = new JSONObject();
        descParams.put("floor", floorNum);
        params.put("desc", descParams);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.addKeywordBtn:
                if(TextUtils.isEmpty(mKeyword1.getText())) {
                    mKeyword1.setText(mKeywordName.getText() + " [X]");
                    keywords.add(mKeywordName.getText().toString());
                } else if(TextUtils.isEmpty(mKeyword2.getText())){
                    mKeyword2.setText(mKeywordName.getText() + " [X]");
                    keywords.add(mKeywordName.getText().toString());
                } else {
                    Toast.makeText(mActivity, "키워드는 2개까지 입력 가능합니다.", Toast.LENGTH_SHORT).show();
                }

                mKeywordName.setText(null);
                break;

            case R.id.keyword1:
                if(!TextUtils.isEmpty(mKeyword1.getText())) {
                    if(!TextUtils.isEmpty(mKeyword2.getText())) {
                        mKeyword1.setText(mKeyword2.getText());
                        keywords.remove(0);
                        keywords.remove(0);

                        keywords.add(mKeyword2.getText().toString());
                        mKeyword2.setText(null);
                    }else {
                        mKeyword1.setText(null);
                        keywords.remove(0);
                    }
                }
                break;
            case R.id.keyword2:
                if(!TextUtils.isEmpty(mKeyword2.getText())) {
                    mKeyword2.setText(null);
                    keywords.remove(1);
                }
                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_create:
                mActivity.finish();
                mClicked = false;
                break;
        }
    }


}
