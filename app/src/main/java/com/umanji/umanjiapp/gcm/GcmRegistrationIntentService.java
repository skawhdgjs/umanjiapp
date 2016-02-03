/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.umanji.umanjiapp.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.model.SuccessData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class GcmRegistrationIntentService extends IntentService implements AppConfig {

    private static final String TAG = "GcmRegIntentService";
    private static final String[] TOPICS = {"global"};

    /****************************************************
     *  Api
     ****************************************************/
    public ApiHelper mApiHelper;

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mApiHelper  = new ApiHelper(getApplicationContext());
        initGcm(this);
    }

    private void initGcm(IntentService service) {
        Log.d(TAG, "initGcm");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(service);
            final String token = instanceID.getToken(getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);


            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendRegistrationToServer(token);
                }
            }, 2000);

            subscribeTopics(token);

            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(service).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
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
            mApiHelper.call(api_channels_gcm, params);
            FileHelper.setString(getApplicationContext(), "gcm_token", token);
            FileHelper.setString(getApplicationContext(), "gcm_user", loginUserId);

        }catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}
