package com.umanji.umanjiapp.ui.auth;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.umanji.umanjiapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SignSelectActivity extends AppCompatActivity {

    private Button mEmailButton;
    private Button mNaverButton;
    private Button mFacebookButton;
    private Button mGoogleButton;

    private TextView mLogin;


    /*****************
     * Sign in google
     *****************/
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    /*****************
     * Sign in facebook
     *****************/
    private static final int FB_SIGN_IN = 64206;
    private LoginButton mSignFaceBook;
    private CallbackManager callbackManager;
    private AccessTokenTracker tokenTracker;
    private ProfileTracker profileTracker;
    private Profile profile;

    /*
       Debug TAG
     */

    private static final String TAG_Google = "google-", TAG_Facebook="facebook-";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_select);

        mEmailButton = (Button) findViewById(R.id.select_email);
        mNaverButton = (Button) findViewById(R.id.select_naver);
        mFacebookButton = (Button) findViewById(R.id.select_facebook);
        mGoogleButton = (Button) findViewById(R.id.singinG);

        mLogin = (TextView) findViewById(R.id.login);

        mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSign = new Intent(SignSelectActivity.this, SignupActivity.class);
                startActivity(intentSign);
            }
        });

        mNaverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Sorry Naver is Preparing", Toast.LENGTH_SHORT).show();

            }
        });

        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), "Sorry Facebook is Preparing", Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logInWithReadPermissions( SignSelectActivity.this , Arrays.asList("public_profile","email"));

            }
        });

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), "Sorry Google is Preparing", Toast.LENGTH_SHORT).show();
                signInGoogle();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignSelectActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });


        /*******************
         *  google signin setup
         *******************/
        updateUI_G(false);
        // [START configure_signin]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // [START build_client]
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("SignupActivity","onConeectionFail"+connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        /************************
         * facebook signin setup
         ************************/
        updateUI_F(false);



        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, buildFacebookCallback());

    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onStop() {
        super.onStop();

    }

    /***************************************************
     * google Sign method
     ***************************************************/

    private void signInGoogle(){
        Intent singinIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(singinIntent, RC_SIGN_IN);
    }

    private void singOutGoogle(){


        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI_G(false);
            }
        });
    }

    private void revokeAccess() {


        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI_G(false);
                        // [END_EXCLUDE]
                    }
                });
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG_Google,"success get account");

            updateUI_G(true);

        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG_Google,"faile get account");
            updateUI_G(false);
        }
    }

    private void CheckInGoogleSingin(){

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG_Google,"cache got it");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            Log.d(TAG_Google,"cache run out");
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    private void updateUI_G(boolean signedIn) {
        if (signedIn) {
            this.findViewById(R.id.singinG).setVisibility(View.GONE);
            this.findViewById(R.id.singoutG).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.singinG).setVisibility(View.VISIBLE);
            this.findViewById(R.id.singoutG).setVisibility(View.GONE);
        }
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("캐쉬만료_재연동중");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    /**********
     * facebook method
     */

    private FacebookCallback<LoginResult> buildFacebookCallback(){

        FacebookCallback<LoginResult> FC = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebook","success");

/*
                GraphRequest requset = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String email = object.getString("email");
                                    Log.d("facebook-email",email);

                                }catch(JSONException e){
                                    Log.d("facebook-email","fail");
                                }
                            }
                        }
                );

*/

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        };

        return FC;

    }


    private void updateUI_F(boolean signedIn) {
        if (signedIn) {
            this.findViewById(R.id.select_facebook).setVisibility(View.GONE);
            this.findViewById(R.id.select_facebook_out).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.select_facebook).setVisibility(View.VISIBLE);
            this.findViewById(R.id.select_facebook_out).setVisibility(View.GONE);
        }
    }



    /*result handling*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        //google result / facebook result
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG_Google,"result for google");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }else if(requestCode == FB_SIGN_IN){
            Log.d(TAG_Facebook,"result for facebook");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }

}
