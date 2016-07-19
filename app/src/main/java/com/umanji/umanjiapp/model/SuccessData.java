package com.umanji.umanjiapp.model;

import org.json.JSONObject;

import java.util.ArrayList;


public class SuccessData {

    public String type;
    public String key;
    public JSONObject response;
    public ArrayList<SubLinkData> arrayData;

    public SuccessData(String type, JSONObject response) {
        this.type = type;
        this.key = "";
        this.response = response;
    }

    public SuccessData(String type, String key, JSONObject response) {
        this.type = type;
        this.key = key;
        this.response = response;
    }

    public SuccessData(String type, ArrayList<SubLinkData> arrayData) {
        this.type = type;
        this.arrayData = arrayData;
    }

    public SuccessData(String type) {
        this.type = type;
    }
}
