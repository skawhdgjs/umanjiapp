package com.umanji.umanjiapp.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;


public class AuthData implements Serializable {

    public String token;
    public ChannelData user;

    public AuthData(JSONObject jsonObject) {
        try {
            JSONObject data = jsonObject;
            if(jsonObject.optJSONObject("data") != null) {
                data = jsonObject.optJSONObject("data");
            }

            this.token = data.optString("token");
            user = new ChannelData(data.optJSONObject("user"));
        } catch (Exception e) {
            Log.e("Document: Error: ", e.toString());
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ChannelData getUser() {
        return user;
    }

    public void setUser(ChannelData user) {
        this.user = user;
    }
}
