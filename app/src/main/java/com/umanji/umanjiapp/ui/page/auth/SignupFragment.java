package com.umanji.umanjiapp.ui.page.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupFragment extends BaseFragment {
    private static final String TAG = "SignupFragment";


    /****************************************************
     *  View
     ****************************************************/
    private AutoCompleteTextView mEmail;
    private EditText mPassword;
    private Button mSubmit;


    /****************************************************
     *  Etc
     ****************************************************/
    private LatLng mLatLng;


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
            mLatLng = new LatLng(latitude, longitude);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_auth, container, false);
        super.onCreateView(view);

        mEmail = (AutoCompleteTextView)view.findViewById(R.id.email);
        mPassword = (EditText)view.findViewById(R.id.password);

        mSubmit = (Button)view.findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);


        if(mLatLng != null) loadData();

        return view;
    }

    @Override
    public void loadData() {
        try {
            JSONObject params = new JSONObject();
            params.put("latitude", mLatLng.latitude);
            params.put("longitude", mLatLng.longitude);

            mApiHelper.call(api_sign_getByPoint, params);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public void updateView() {

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                boolean isValid = isValidLoginForm(mEmail, mPassword);
                if(isValid) {
                    signin();
                }
                break;
        }
    }



    /****************************************************
     *  Event Bus
     ****************************************************/

    public void onEvent(SuccessData event){
        AuthData auth;
        switch (event.type) {
            case api_signin:
                auth = new AuthData(event.response);
                if(auth.user != null) {
                    mActivity.finish();
                }else {
                    signup();
                }
                break;
            case api_signup:
                auth = new AuthData(event.response);
                if(auth.user != null) {
                    mActivity.finish();
                }else {
                    //TODO: 회원가입 오류 처리
                }
                break;
            case api_sign_getByPoint:
                mChannel = new ChannelData(event.response);
                break;
        }
    }




    /****************************************************
     *  Submit Methods
     ****************************************************/

    private void signin() {
        final String fEmail     = mEmail.getText().toString();
        final String fPassword  = mPassword.getText().toString();

        try {
            JSONObject params = new JSONObject();
            params.put("email", fEmail);
            params.put("password", fPassword);

            mApiHelper.call(api_signin, params);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    private void signup() {
        final String fEmail     = mEmail.getText().toString();
        final String fPassword  = mPassword.getText().toString();

        try {

            JSONObject params;
            if(mChannel != null) {
                params = mChannel.getAddressJSONObject();
            } else {
                params = new JSONObject();
            }

            params.put("email", fEmail);
            params.put("password", fPassword);

            mApiHelper.call(api_signup, params);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }




    /****************************************************
     *  Private Methods
     ****************************************************/

    public boolean isValidLoginForm(AutoCompleteTextView emailInput, EditText passwordInput) {

        emailInput.setError(null);
        passwordInput.setError(null);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !UiHelper.isPasswordValid(password)) {
            passwordInput.setError(mContext.getString(R.string.sign_error_invalid_password));
            focusView = passwordInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError(mContext.getString(R.string.error_field_required));
            focusView = emailInput;
            cancel = true;
        } else if (!UiHelper.isEmailValid(email)) {
            emailInput.setError(mContext.getString(R.string.sign_error_invalid_email));
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
