package com.umanji.umanjiapp.helper;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;

import org.json.JSONException;
import org.json.JSONObject;

public final class AuthHelper {

    public static boolean isLoginUser(Context context, String userId) {
        if(TextUtils.equals(userId, AuthHelper.getUserId(context))) return true;
        return false;
    }

    public static boolean isLogin(Context context) {
        String token = FileHelper.getString(context, "token");

        if(TextUtils.isEmpty(token)) {
            return false;
        } else {
            return true;
        }
    }

    public static String getUserId(Context context) {
        String id = FileHelper.getString(context, "id");

        if(TextUtils.isEmpty(id)) {
            return "";
        } else {
            return id;
        }
    }

    public static String getUserName(Context context) {
        String name = FileHelper.getString(context, "name");

        if(TextUtils.isEmpty(name)) {
            return "";
        } else {
            return name;
        }
    }

    public static String getPhoto(Context context) {
        String photo = FileHelper.getString(context, "photo");

        if(TextUtils.isEmpty(photo)) {
            return "";
        } else {
            return photo;
        }
    }

    public static String getToken(Context context) {
        String token = FileHelper.getString(context, "token");

        if(TextUtils.isEmpty(token)) {
            return "";
        } else {
            return token;
        }
    }

    public static void login(Context context, AuthData auth) {
        String token = auth.getToken();
        ChannelData userData = auth.getUser();
        FileHelper.setString(context, "id", userData.getId());
        FileHelper.setString(context, "email", userData.getEmail());
        FileHelper.setString(context, "name", userData.getUserName());
        FileHelper.setString(context, "photo", userData.getPhoto());
        FileHelper.setString(context, "token", token);
    }

    public static void logout(Context context) {
        FileHelper.setString(context, "id", null);
        FileHelper.setString(context, "email", null);
        FileHelper.setString(context, "name", null);
        FileHelper.setString(context, "photo", null);
        FileHelper.setString(context, "token", null);
    }
}
