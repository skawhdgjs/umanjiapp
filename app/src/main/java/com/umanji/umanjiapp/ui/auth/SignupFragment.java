package com.umanji.umanjiapp.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.model.LatLng;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class SignupFragment extends BaseFragment {
    private static final String TAG = "PostFragment";

    protected ChannelData mChannel;

    private AutoCompleteTextView mEmail;
    private EditText mPassword;
    private EditText mPhoneNumber;



    protected Button mSubmit;
    protected ImageView mUserPhoto;
    protected ImageView mLookAround;
    protected TextView mName;
    protected CheckBox mCheckbox;
    protected TextView mLocationConfirm;
    protected TextView mPrivacyConfirm;

    protected FloatingActionButton mFab;


    /****************************************************
     *  Etc
     ****************************************************/
    ChannelData mAddressChannel;
    private LatLng mCurrentMyPosition;

    public static SignupFragment newInstance(Bundle bundle) {
        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            double latitude = getArguments().getDouble("latitude");
            double longitude = getArguments().getDouble("longitude");
            mCurrentMyPosition = new LatLng(latitude, longitude);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_signup, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mCheckbox = (CheckBox) view.findViewById(R.id.checkbox1);
        mLocationConfirm = (TextView) view.findViewById(R.id.locationConfirm);
        mLocationConfirm.setOnClickListener(this);

        mPrivacyConfirm = (TextView) view.findViewById(R.id.privacyConfirm);
        mPrivacyConfirm.setOnClickListener(this);

        // mPrivacyConfirm

        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.checkbox) {
                    if (isChecked) {
                        Toast.makeText(mActivity, "눌림", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mActivity, "안눌림", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mEmail = (AutoCompleteTextView)view.findViewById(R.id.email);
        mPassword = (EditText)view.findViewById(R.id.password);
        mPhoneNumber = (EditText)view.findViewById(R.id.phoneNumber);


        mSubmit = (Button)view.findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);


        if(mCurrentMyPosition != null) {
            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.locationConfirm:
                Intent webIntent = new Intent(mActivity, WebViewActivity.class);
                webIntent.putExtra("url", "http://blog.naver.com/mothcar/220673352101");
                mActivity.startActivity(webIntent);
                break;

            case R.id.privacyConfirm:
                Intent privacyIntent = new Intent(mActivity, WebViewActivity.class);
                privacyIntent.putExtra("url", "http://blog.naver.com/mothcar/220673367007");
                mActivity.startActivity(privacyIntent);
                break;

            case R.id.submitBtn2:
            case R.id.submit:
                boolean isValid = isValidLoginForm(mEmail, mPassword);
                if(isValid) {
                    signup();
                }
                break;


        }
    }

    private void signup() {
        final String fEmail     = mEmail.getText().toString();
        final String fPassword  = mPassword.getText().toString();
        final String fPhoneNumber  = mPhoneNumber.getText().toString();

        try {

            JSONObject params;
            if(mAddressChannel != null) {
                params = mAddressChannel.getAddressJSONObject();
            } else {
                params = new JSONObject();
            }

            params.put("email", fEmail);
            params.put("password", fPassword);
            params.put("phone", fPhoneNumber);


            mApi.call(api_signup, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    AuthData auth = new AuthData(json);
                    if(auth.user != null && !TextUtils.isEmpty(auth.user.getId())) {
                        FileHelper.setString(mActivity, "pre_email", fEmail);
                        EventBus.getDefault().post(new SuccessData(api_signup, json));
                        mActivity.finish();
                    }else {
                        Toast.makeText(mActivity, "이미 존재하는 이메일 주소입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public void loadData() {
        try {
            JSONObject params = new JSONObject();
            params.put("latitude", mCurrentMyPosition.latitude);
            params.put("longitude", mCurrentMyPosition.longitude);

            mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    mAddressChannel = new ChannelData(json);
                }
            });
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public void updateView() {


    }


    protected void setName(Activity activity, ChannelData channelData, String label) {
        if (!TextUtils.isEmpty(mChannel.getName())) {
            mName.setText(mChannel.getName());
        } else {
            mName.setText(label);
        }
    }

    public boolean isValidLoginForm(AutoCompleteTextView emailInput, EditText passwordInput) {

        emailInput.setError(null);
        passwordInput.setError(null);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !Helper.isPasswordValid(password)) {
            passwordInput.setError(mActivity.getString(R.string.sign_error_invalid_password));
            focusView = passwordInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError(mActivity.getString(R.string.error_field_required));
            focusView = emailInput;
            cancel = true;
        } else if (!Helper.isEmailValid(email)) {
            emailInput.setError(mActivity.getString(R.string.sign_error_invalid_email));
            focusView = emailInput;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }


}
