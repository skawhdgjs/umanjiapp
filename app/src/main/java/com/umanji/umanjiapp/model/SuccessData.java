package com.umanji.umanjiapp.model;

import org.json.JSONObject;


public class SuccessData {

    public String type;
    public JSONObject response;

    public SuccessData(String type, JSONObject response) {
        this.type = type;
        this.response = response;
    }
}
