package com.umanji.umanjiapp.model;

import org.json.JSONObject;


public class PaulBusData {

    public String type;
    public String key;
    public JSONObject response;

    public PaulBusData(String type, String key) {
        this.type = type;
        this.key = key;
    }

    public PaulBusData(String type, JSONObject obj) {
        this.type = type;
        this.response = obj;
    }
}
