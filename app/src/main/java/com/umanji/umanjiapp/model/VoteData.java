package com.umanji.umanjiapp.model;

import android.util.Log;

import com.umanji.umanjiapp.helper.ApiHelper;

import org.json.JSONObject;

public class VoteData {
    private String name;
    private int count;
    private String [] voters;


    public VoteData(String name) {
        try {
            this.name   = name;
            this.count  = 0;
            this.voters = null;
        }catch (Exception e) {
            Log.e("VoteData", "Error " + e.toString());
        }
    }

    public VoteData(JSONObject jsonObject) {
        try {
            this.name   = jsonObject.optString("name");
            this.count  = jsonObject.optInt("count");
            this.voters = ApiHelper.getStringArray(jsonObject, "voters");
        }catch (Exception e) {
            Log.e("VoteData", "Error " + e.toString());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String[] getVoters() {
        return voters;
    }

    public void setVoters(String[] voters) {
        this.voters = voters;
    }
}
