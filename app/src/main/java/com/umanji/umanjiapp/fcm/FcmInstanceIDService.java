package com.umanji.umanjiapp.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.gcm.QuickstartPreferences;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.FileHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nam on 2017. 2. 28..
 */

public class FcmInstanceIDService extends FirebaseInstanceIdService implements AppConfig{
    private static final String TAG = "FcmInstanceIDService";

    public ApiHelper mApi;




    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);

        subscribeTopics(refreshedToken);

        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        String loginUserId  = AuthHelper.getUserId(getApplicationContext());
        String preUserId    = FileHelper.getString(getApplicationContext(), "gcm_user");
        String preToken     = FileHelper.getString(getApplicationContext(), "gcm_token");

        if(preToken.equals(token) && loginUserId.equals(preUserId)) {
            return;
        }

        try {
            JSONObject params = new JSONObject();
            params.put("access_token", AuthHelper.getToken(getApplicationContext()));
            params.put("token", token);
            mApi.call(api_channels_gcm, params);
            FileHelper.setString(getApplicationContext(), "gcm_token", token);
            FileHelper.setString(getApplicationContext(), "gcm_user", loginUserId);

        }catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }


    private void subscribeTopics(String token) {
        FirebaseMessaging.getInstance().subscribeToTopic(token);
    }

}
