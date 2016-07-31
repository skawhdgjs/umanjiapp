package com.umanji.umanjiapp.ui.channel.duty;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.model.TestCDN;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class DutyCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "DutyCreateFragment";

    private TextView mManage_area;
    private ImageView mManagerPhoto;
    private TextView mManager;
    private Button mAppointmentBtn;
    private Button mDismissal;

    private LinearLayout mCandidatePanel;

    private ChannelData mUser;
    private ChannelData mCandidateFirst;
    private ChannelData mCandidateSecond;
    private ChannelData mCandidateThird;
    private ChannelData mAppointedCandidate;

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

        mManagerPhoto = (ImageView) view.findViewById(R.id.managerPhoto);
//        mChannel.getOwner().getPhoto();
        mManager = (TextView) view.findViewById(R.id.manager);
        if(mChannel.getOwner() != null){
            if(!TextUtils.isEmpty(mChannel.getOwner().getPhoto())) {
                String userPhoto = mChannel.getOwner().getPhoto();
                Glide.with(mActivity)
                        .load(userPhoto)
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mManagerPhoto);

            } else {
                Glide.with(mActivity)
                        .load(R.drawable.avatar_default_0)
                        .override(40, 40)
                        .into(mManagerPhoto);
            }

            mManager.setText(mChannel.getOwner().getName().toString());
        }


        mCandidatePanel = (LinearLayout) view.findViewById(R.id.candidatePanel);
        mDismissal = (Button) view.findViewById(R.id.dismissal);
        mDismissal.setOnClickListener(this);

        final TestCDN mCdn = new TestCDN();

        try {
            JSONObject param = new JSONObject();
            param.put("access_token", AuthHelper.getToken(mActivity));

            mApi.call(api_token_check, param, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    AuthData auth = new AuthData(object);

                    String[] myRoleArr = new String[0];
                    if(auth.getUser().getRoles() != null){
                        myRoleArr = auth.getUser().getRoles();
                    }

                    String[] centerManagerArr = new String[0];
                    if(mChannel.getOwner() != null){
                        centerManagerArr = mChannel.getOwner().getRoles();
                    }


                    String myRole = null;
                    String centerManager = null;
                    if(myRoleArr.length > 0){
                        myRole = myRoleArr[0];
                    }
                    if(centerManagerArr.length >0){
                        centerManager = centerManagerArr[0];
                    } else {
                        centerManager = ROLE_UMANJI_CITIZEN;
                    }
                    boolean isUpper = false;
                    isUpper = mCdn.isUpper(myRole, centerManager);

                    if(mChannel.getOwner() != null){
                        mCandidatePanel.setVisibility(View.GONE);
                        if(isUpper){                                   // 상위권한자일때
                            mDismissal.setVisibility(View.VISIBLE);
                        } else {
                            mDismissal.setVisibility(View.GONE);
                        }
                    } else {
                        mCandidatePanel.setVisibility(View.VISIBLE);
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mManage_area = (TextView) view.findViewById(R.id.manage_area);
        mManager = (TextView) view.findViewById(R.id.manager);
        mAppointmentBtn = (Button) view.findViewById(R.id.appointment);
        mAppointmentBtn.setOnClickListener(this);


        int mAreaLevelInt = mChannel.getLevel();
        String mAreaLevel = Integer.toString(mAreaLevelInt);
        String mAreaName = mChannel.getAdminArea();
        String mLocalityName = mChannel.getLocality();
        String mToroughfareName = mChannel.getThoroughfare();

        if (mAreaLevel.equals("8")) {
            mManage_area.setText(mAreaName + "정보센터장");
//            roleName = "ad_admin";
        } else if (mAreaLevel.equals("12")) {
            mManage_area.setText(mLocalityName);
//            roleName = "ad_locality";
        } else if (mAreaLevel.equals("14")) {
            mManage_area.setText(mToroughfareName);
//            roleName = "ad_thoroughfare";
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

                                boolean hasRole = false;

                                for(int idx = 0; idx < jsonArray.length(); idx++) {                 // user 77명
                                    JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                    ChannelData doc = new ChannelData(jsonDoc);
                                    mExperts = doc.getSubLinks();                               // subLink 갯수
                                    String[] mRoles;
                                    mRoles = doc.getRoles();
                                    if(mRoles != null && mRoles.length > 0){            // 무조건 info관련 역할이 있으면 true가 되서 아래 후보자에서 제외되어야 함
                                        for(int i=0; mRoles.length > i; i++){
                                            String role = mRoles[i];
                                            switch(role){
                                                case ROLE_INFO_COUNTRY:
                                                    hasRole = true;
                                                    break;
                                                case ROLE_INFO_ADMIN:
                                                    hasRole = true;
                                                    break;
                                                case ROLE_INFO_LOCALITY:
                                                    hasRole = true;
                                                    break;
                                                case ROLE_INFO_THOROUGHFARE:
                                                    hasRole = true;
                                                    break;
                                            }
                                        }
                                    }

                                    mUser = doc;

                                    SubLinkData element;

                                    int tempPoint = 0;

                                    if(!hasRole){                           // 이미 관리직이면 또 관리를 안 줌
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

                                    }

                                    hasRole = false;






                                } // for user count

                                if(mCandidateFirst !=null && !TextUtils.isEmpty(mCandidateFirst.getPhoto())) {
                                    String userPhoto = mCandidateFirst.getPhoto();
                                    Glide.with(mActivity)
                                            .load(userPhoto)
                                            .thumbnail(1f)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                                            .thumbnail(1f)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                                            .thumbnail(1f)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mCandidate3);

                                } else {
                                    Glide.with(mActivity)
                                            .load(R.drawable.avatar_default_0)
                                            .override(40, 40)
                                            .into(mCandidate3);
                                }

                                if(mCandidateFirst !=null && !TextUtils.isEmpty(mCandidateFirst.getName())) {
                                    mCandidateName1.setText(mCandidateFirst.getName().toString());
                                } else {
                                    mCandidateName1.setText("후보 없음");
                                }

                                if(mCandidateSecond !=null && !TextUtils.isEmpty(mCandidateSecond.getName())) {
                                    mCandidateName2.setText(mCandidateSecond.getName().toString());
                                } else {
                                    mCandidateName2.setText("후보 없음");
                                }

                                if(mCandidateThird !=null && !TextUtils.isEmpty(mCandidateThird.getName())) {
                                    mCandidateName3.setText(mCandidateThird.getName().toString());
                                } else {
                                    mCandidateName3.setText("후보 없음");
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

    private void getRoleName() {
//        mChannel의 level로 지금 Info center의 수위를 결정하고 거기에 맞는 role을 줘야한다
        int zoom = mChannel.getLevel();

        switch(zoom){
            case 10:
                roleName = ROLE_INFO_ADMIN;
                break;

            case 14:
                roleName = ROLE_INFO_ADMIN;
                break;

            default:
                roleName = ROLE_INFO_THOROUGHFARE;
                break;
        }
    }



    @Override
    protected void request() {

        try {
            JSONObject params = new JSONObject();
            JSONObject centerParams = new JSONObject();
            getRoleName();
            setUserDesc(params);
            String email = mAppointedCandidate.getEmail();
            ArrayList<String> roles = new ArrayList<>();
            String [] preRoles = mAppointedCandidate.getRoles();
            if(preRoles != null){
                for(int i=0; preRoles.length > i; i++){
                    roles.add(preRoles[i]);
                }
            }
            roles.add(roleName);
            params.put("email", email);
            params.put("roles", new JSONArray(roles));
            mApi.call(api_profile_role_update, params);

            centerParams.put("id", mChannel.getId());
            centerParams.put("owner", mAppointedCandidate.getId());
            mApi.call(api_channels_id_update, centerParams);


        } catch (JSONException e) {
            Log.e("DutyCreateFragment", "error " + e.toString());
        }
    }

    protected void dismissalRequest(){
        try {
            JSONObject params = new JSONObject();
            JSONObject centerParams = new JSONObject();
            getRoleName();
            setUserDesc(params);
            String email = mChannel.getOwner().getEmail();
            ArrayList<String> roles = new ArrayList<>();
            String [] preRoles = mChannel.getOwner().getRoles();
            if(preRoles != null){
                for(int i=0; preRoles.length > i; i++){
                    roles.add(preRoles[i]);
                }
            }
            roles.remove(roleName);
            params.put("email", email);
            params.put("roles", new JSONArray(roles));
            mApi.call(api_profile_role_update, params);

            centerParams.put("id", mChannel.getId());
            centerParams.put("owner", "");
            mApi.call(api_channels_id_update, centerParams);


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
                request();
                break;

            case R.id.dismissal:
                dismissalRequest();

                break;
//
            case R.id.radioButton1:
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                radioTest = "1";
                mAppointedCandidate = mCandidateFirst;
                if(mAppointedCandidate != null){
                    mAppointmentBtn.setVisibility(View.VISIBLE);
                } else {
                    mAppointmentBtn.setVisibility(View.GONE);
                }
                break;

            case R.id.radioButton2:
                radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                radioTest = "2";
                mAppointedCandidate = mCandidateSecond;
                if(mAppointedCandidate != null){
                    mAppointmentBtn.setVisibility(View.VISIBLE);
                } else {
                    mAppointmentBtn.setVisibility(View.GONE);
                }
                break;

            case R.id.radioButton3:
                radioButton1.setChecked(false);
                radioButton2.setChecked(false);
                radioTest = "3";
                mAppointedCandidate = mCandidateThird;
                if(mAppointedCandidate != null){
                    mAppointmentBtn.setVisibility(View.VISIBLE);
                } else {
                    mAppointmentBtn.setVisibility(View.GONE);
                }

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
