package com.umanji.umanjiapp.model;

import org.json.JSONObject;

/**
 * Created by bruce on 16. 1. 15..
 */
public class SubLinkData {
    private String owner;
    private String id;
    private String type;
    private String name;

    public SubLinkData(JSONObject jsonObject) {
        this.owner  = jsonObject.optString("owner");
        this.id     = jsonObject.optString("id");
        this.type   = jsonObject.optString("type");
        this.name   = jsonObject.optString("name");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
