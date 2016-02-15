package com.umanji.umanjiapp.model;

import org.json.JSONObject;


public class SuccessData {

    public String type;
    public String key;
    public JSONObject response;

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
}
