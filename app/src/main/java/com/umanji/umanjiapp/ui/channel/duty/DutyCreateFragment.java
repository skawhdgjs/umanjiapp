package com.umanji.umanjiapp.ui.channel.duty;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.MyRadioButton;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class DutyCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "DutyCreateFragment";

    private TextView mManage_area;
    private TextView mManager;
    private Button mAppointment;
    private Button mDismissal;

    private ChannelData mUser;
    private ChannelData mCandidateFirst;
    private ChannelData mCandidateSecond;
    private ChannelData mCandidateThird;

    private ArrayList<SubLinkData> mExperts;

    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;

    private ImageView mCandidate1;
    private ImageView mCandidate2;
    private ImageView mCandidate3;

    private TextView mCandidateName1;
    private TextView mCandidateName2;
    private TextView mCandidateName3;

    private TextView mCandidatePoint1;
    private TextView mCandidatePoint2;
    private TextView mCandidatePoint3;



    private String radioTest;

    private TextView tv;
    private String c;


    private String roleName;

    public static DutyCreateFragment newInstance(Bundle bundle) {
        DutyCreateFragment fragment = new DutyCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_appoint_create, container, false);
    }

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        mManage_area = (TextView) view.findViewById(R.id.manage_area);
        mManager = (TextView) view.findViewById(R.id.manager);
        mAppointment = (Button) view.findViewById(R.id.appointment);
        mAppointment.setOnClickListener(this);
        mDismissal = (Button) view.findViewById(R.id.dismissal);
        mDismissal.setOnClickListener(this);

        int mAreaLevelInt = mChannel.getLevel();
        String mAreaLevel = Integer.toString(mAreaLevelInt);
        String mAreaName = mChannel.getAdminArea();
        String mLocalityName = mChannel.getLocality();
        String mToroughfareName = mChannel.getThoroughfare();

        if (mAreaLevel.equals("8")) {
            mManage_area.setText(mAreaName);
            roleName = "ad_admin";
        } else if (mAreaLevel.equals("12")) {
            mManage_area.setText(mLocalityName);
            roleName = "ad_locality";
        } else if (mAreaLevel.equals("14")) {
            mManage_area.setText(mToroughfareName);
            roleName = "ad_thoroughfare";
        }

        radioButton1 = (RadioButton) view.findViewById(R.id.radioButton1);
        radioButton1.setOnClickListener(this);
        radioButton2 = (RadioButton) view.findViewById(R.id.radioButton2);
        radioButton2.setOnClickListener(this);
        radioButton3 = (RadioButton) view.findViewById(R.id.radioButton3);
        radioButton3.setOnClickListener(this);

        mCandidate1 = (ImageView) view.findViewById(R.id.candidate1);
        mCandidate2 = (ImageView) view.findViewById(R.id.candidate2);
        mCandidate3 = (ImageView) view.findViewById(R.id.candidate3);

        mCandidateName1 = (TextView) view.findViewById(R.id.candidateName1);
        mCandidateName2 = (TextView) view.findViewById(R.id.candidateName2);
        mCandidateName3 = (TextView) view.findViewById(R.id.candidateName3);

        mCandidatePoint1 = (TextView) view.findViewById(R.id.candidatePoint1);
        mCandidatePoint2 = (TextView) view.findViewById(R.id.candidatePoint2);
        mCandidatePoint3 = (TextView) view.findViewById(R.id.candidatePoint3);


        c = mChannel.getAddress().toString();
//        tv = (TextView) view.findViewById(R.id.bundleTest);
//        tv.setText(c);

        setCandidate();

    }

    protected void setCandidate() {

        try {
            JSONObject params = new JSONObject();
            params.put("type", TYPE_USER);
            params.put("limit", 100);

            mApi.call(api_channels_members_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");      // user전체 불러오기

                            if(jsonArray.length() != 0) {
//                            mlayout.setBackgroundResource(R.color.feed_bg);

                                int number1 = 0;
                                int number2 = 0;
                                int number3 = 0;

                                for(int idx = 0; idx < jsonArray.length(); idx++) {                 // user 77명
                                    JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                    ChannelData doc = new ChannelData(jsonDoc);
                                    mExperts = doc.getSubLinks();                               // subLink 갯수
                                    mUser = doc;


                                    SubLinkData element;

                                    int tempPoint = 0;


                                    if(mExperts != null && mExperts.size() >= 1){           // subLink 가 있으면
                                        for (int idx1 = 0; mExperts.size() > idx1; idx1++) {    // sublinks 배열 갯수
                                            element = mExperts.get(idx1);
                                            String name;
                                            if(element.getName().toString() != null){
                                                name = element.getName().toString();

                                                if (name.equals("정치관심가")) {
                                                    int expertPoint = 0;
                                                    if(element.getPoint().toString() != null){
                                                        String expertPointStr = element.getPoint().toString();   // point 가져온다
                                                        if(expertPointStr.equals("")){
                                                            expertPointStr = "0";
                                                        }
                                                        expertPoint = Integer.parseInt(expertPointStr);          // to Int
                                                    }

                                                    if (expertPoint > tempPoint) {                               // 임시로 가장 큰 놈을 넣는다
                                                        tempPoint = expertPoint;
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    if(tempPoint > 0){
                                        if(number1 >= tempPoint){
                                            if(number2 >= tempPoint){
                                                number3 = tempPoint;
                                                mCandidateThird = mUser;
                                            } else {
                                                number2 = tempPoint;
                                                mCandidateSecond = mUser;
                                            }

                                        } else if(number2 >= tempPoint) {
                                            number2 = tempPoint;
                                            mCandidateSecond = mUser;
                                        } else {
                                            if(number2 >= 100 && number1 >= 100){
                                                number3 = number2;
                                                mCandidateThird = mCandidateSecond;
                                                number2 = number1;
                                                mCandidateSecond = mCandidateFirst;
                                                number1 = tempPoint;
                                                mCandidateFirst = mUser;
                                            } else if(number1 >= 100){
                                                number2 = number1;
                                                mCandidateSecond = mCandidateFirst;
                                                number1 = tempPoint;
                                                mCandidateFirst = mUser;
                                            } else {
                                                number1 = tempPoint;
                                                mCandidateFirst = mUser;
                                            }
                                        }
                                    }



                                } // for user count

                                if(mCandidateFirst !=null && !TextUtils.isEmpty(mCandidateFirst.getPhoto())) {
                                    String userPhoto = mCandidateFirst.getPhoto();
                                    Glide.with(mActivity)
                                            .load(userPhoto)
                                            .override(40, 40)
                                            .into(mCandidate1);

                                } else {
                                    Glide.with(mActivity)
                                            .load(R.drawable.avatar_default_0)
                                            .override(40, 40)
                                            .into(mCandidate1);
                                }

                                if(mCandidateSecond !=null && !TextUtils.isEmpty(mCandidateSecond.getPhoto())) {
                                    String userPhoto = mCandidateSecond.getPhoto();
                                    Glide.with(mActivity)
                                            .load(userPhoto)
                                            .override(40, 40)
                                            .into(mCandidate2);

                                } else {
                                    Glide.with(mActivity)
                                            .load(R.drawable.avatar_default_0)
                                            .override(40, 40)
                                            .into(mCandidate2);
                                }

                                if(mCandidateThird !=null && !TextUtils.isEmpty(mCandidateThird.getPhoto())) {
                                    String userPhoto = mCandidateThird.getPhoto();
                                    Glide.with(mActivity)
                                            .load(userPhoto)
                                            .override(40, 40)
                                            .into(mCandidate3);

                                } else {
                                    Glide.with(mActivity)
                                            .load(R.drawable.avatar_default_0)
                                            .override(40, 40)
                                            .into(mCandidate3);
                                }

                                if(mCandidateFirst !=null && !TextUtils.isEmpty(mCandidateFirst.getName())) {
                                    mCandidateName1.setText(mCandidateFirst.getName().toString());
                                }

                                if(mCandidateSecond !=null && !TextUtils.isEmpty(mCandidateSecond.getName())) {
                                    mCandidateName2.setText(mCandidateSecond.getName().toString());
                                }

                                if(mCandidateThird !=null && !TextUtils.isEmpty(mCandidateThird.getName())) {
                                    mCandidateName3.setText(mCandidateThird.getName().toString());
                                }

                                if(mCandidateFirst !=null) {
                                    mCandidatePoint1.setText(String.valueOf(number1));
                                }

                                if(mCandidateSecond !=null) {
                                    mCandidatePoint2.setText(String.valueOf(number2));
                                }

                                if(mCandidateThird !=null) {
                                    mCandidatePoint3.setText(String.valueOf(number3));
                                }


                            }

                            updateView();

                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }

                    }
                }
            });




        } catch (JSONException e) {
            e.printStackTrace();
        }



    }



    @Override
    protected void request() {

        try {
            JSONObject params = new JSONObject();
            setUserDesc(params);

            ArrayList<String> roles = new ArrayList<>();
            roles.add(roleName);
            params.put("roles", new JSONArray(roles));

            mApi.call(api_profile_role_update, params);

        } catch (JSONException e) {
            Log.e("DutyCreateFragment", "error " + e.toString());
        }
    }

    protected void setUserDesc(JSONObject params) throws JSONException {
        params.put("email", mManager.getText().toString());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.appointment:
                Toast.makeText(mActivity,radioTest,Toast.LENGTH_SHORT).show();
                break;

            case R.id.dismissal:

                break;
//
            case R.id.radioButton1:
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                radioTest = "1";
                break;

            case R.id.radioButton2:
                radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                radioTest = "2";
                break;

            case R.id.radioButton3:
                radioButton1.setChecked(false);
                radioButton2.setChecked(false);
                radioTest = "3";
                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_profile_role_update:
                mActivity.finish();
                EventBus.getDefault().post(new SuccessData(EVENT_UPDATEVIEW));
                break;

            case api_channels_findEmail:

                Toast.makeText(mActivity, "성공", Toast.LENGTH_LONG).show();
                break;
        }
    }


}
