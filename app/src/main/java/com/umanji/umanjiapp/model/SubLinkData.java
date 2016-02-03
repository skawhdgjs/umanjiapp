package com.umanji.umanjiapp.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bruce on 16. 1. 15..
 */
public class SubLinkData {
    private String owner;
    private String id;
    private String type;

    public SubLinkData(JSONObject jsonObject) {
        this.owner  = jsonObject.optString("owner");
        this.id     = jsonObject.optString("id");
        this.type   = jsonObject.optString("type");
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
