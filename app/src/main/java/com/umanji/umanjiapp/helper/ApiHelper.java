package com.umanji.umanjiapp.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.auth.AccountHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class ApiHelper implements AppConfig {
    private static final String TAG = "ApiHelper";

    private AQuery aQuery;
    private Context mContext;

    private ProgressDialog mProgress;

    public ApiHelper(Context context) {
        this.mContext = context;
        aQuery = new AQuery(context);
    }

    public ApiHelper(Context context, ProgressDialog progress) {
        this.mContext = context;
        this.mProgress = progress;
        aQuery = new AQuery(context);
    }

    public void call(String api, JSONObject params, AjaxCallback<JSONObject> callback) {
        String method = api.substring(0, api.indexOf(" "));
        String uri = api.substring(api.indexOf("/"));
        String url = REST_SERVER_URL + uri;

        final String fApi = api;
        final JSONObject fParams = params;

        try{
            params.put("access_token", AuthHelper.getToken(mContext));

            if(method.equals("GET")) {
                url = ApiHelper.buildGETParams(url, params);
                aQuery.ajax(url, JSONObject.class, callback);
            }else if(method.equals("POST")) {
                aQuery.post(url, params, JSONObject.class, callback);
            }else if(method.equals("PUT")) {
                aQuery.put(url, params, JSONObject.class, callback);
            }else if(method.equals("DELETE")) {
                url = ApiHelper.buildGETParams(url, params);
                aQuery.delete(url, JSONObject.class, callback);
            }

        }catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    public void call(String api, JSONObject params) {

        String method = api.substring(0, api.indexOf(" "));
        String uri = api.substring(api.indexOf("/"));
        String url = REST_SERVER_URL + uri;

        final String fApi = api;
        final JSONObject fParams = params;

        try{
            params.put("access_token", AuthHelper.getToken(mContext));

            if(method.equals("GET")) {
                url = ApiHelper.buildGETParams(url, params);
                aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {

                        if(status.getCode() == 500) {
                            EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                        }else {
                            EventBus.getDefault().post(new SuccessData( fApi, json));
                        }
                    }
                });
            }else if(method.equals("POST")) {
                aQuery.post(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {

                        if(status.getCode() == 500) {
                            EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                        }else {
                            EventBus.getDefault().post(new SuccessData( fApi, json));
                        }
                    }
                });
            }else if(method.equals("PUT")) {
                aQuery.put(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        if(status.getCode() == 500) {
                            EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                        }else {
                            EventBus.getDefault().post(new SuccessData( fApi, json));
                        }
                    }
                });
            }else if(method.equals("DELETE")) {
                url = ApiHelper.buildGETParams(url, params);
                aQuery.delete(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        if(status.getCode() == 500) {
                            EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                        }else {
                            EventBus.getDefault().post(new SuccessData( fApi, json));
                        }
                    }
                });
            }

        }catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    public void call(String api) {

        JSONObject params = new JSONObject();
        call(api, params);

    }

    public void call(String api, Map<String, Object> params) {
        String method = api.substring(0, api.indexOf(" "));
        String uri = api.substring(api.indexOf("/"));
        String url = REST_SERVER_URL + uri;

        final String fApi = api;

        params.put("access_token", AuthHelper.getToken(mContext));

        if(mProgress != null) {
            aQuery.progress(mProgress).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    EventBus.getDefault().post(new SuccessData( fApi, json));
                }
            });
        }else {
            aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    EventBus.getDefault().post(new SuccessData( fApi, json));
                }
            });
        }


    }

    public static String buildGETParams(String url, JSONObject params) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        if (!url.endsWith("/"))
            stringBuilder.append("/");

        if(params !=null && params.length() > 0) {
            stringBuilder.append("?");

            Iterator<String> iterator = params.keys();
            while(iterator.hasNext()) {
                String key = iterator.next();

                String value = params.optString(key);
                stringBuilder.append(key).append("=").append(value);
                if (iterator.hasNext())
                    stringBuilder.append("&");
            }
        }

        return stringBuilder.toString();
    }

    public static String buildGETParams(String url, Map<String, Object> params) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        if (!url.endsWith("/"))
            stringBuilder.append("/");

        if(params !=null && params.size() > 0) {
            stringBuilder.append("?");



            Iterator<String> iterator = params.keySet().iterator();
            while(iterator.hasNext()) {
                String key = iterator.next();
                Object value = params.get(key);


                stringBuilder.append(key).append("=").append(value);
                if (iterator.hasNext())
                    stringBuilder.append("&");
            }
        }

        return stringBuilder.toString();
    }

    public static String [] getStringArray(JSONObject response, String name) throws JSONException, UnsupportedEncodingException {
        String [] stringArray;

        if(!response.optString(name).equals("") && !response.optString(name).equals("null")) {

            JSONArray jArrayTypes   = response.getJSONArray(name);

            if(jArrayTypes.length() > 0) {
                stringArray = new String[jArrayTypes.length()];
                for(int i=0; i < jArrayTypes.length(); i++) {
                    stringArray[i] = jArrayTypes.getString(i);
                }
            }else {
                stringArray = null;
            }
        } else {
            stringArray = null;
        }

        return stringArray;

    }


    public static long getDate(JSONObject response, String name) throws JSONException, UnsupportedEncodingException {
        JSONObject dateObj = response.optJSONObject(name);
        return dateObj.optLong("date");

    }

    public static ArrayList<ChannelData> getChannelsFromJSONArray(JSONArray jsonArray) throws JSONException {

        ArrayList<ChannelData> newDocs = new ArrayList();

        for(int idx = 0; idx < jsonArray.length(); idx++) {
            JSONObject jsonDoc = jsonArray.getJSONObject(idx);
            ChannelData doc = new ChannelData(jsonDoc);

            newDocs.add(doc);
        }

        return newDocs;
    }
}
