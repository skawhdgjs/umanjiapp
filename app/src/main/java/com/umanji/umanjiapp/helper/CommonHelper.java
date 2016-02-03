package com.umanji.umanjiapp.helper;

import android.app.Activity;
import android.content.Intent;

import com.umanji.umanjiapp.ui.page.auth.SignupActivity;


public final class CommonHelper {


    public static int dpToPixel(Activity activity, int dp) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }

    public static boolean isAuthError(Activity activity) {

        boolean isAuthError = !AuthHelper.isLogin(activity);
        if(isAuthError) {
            Intent intent = new Intent(activity, SignupActivity.class);
            activity.startActivity(intent);
        }

        return isAuthError;
    }
}
