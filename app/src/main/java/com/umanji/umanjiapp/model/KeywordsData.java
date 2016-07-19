package com.umanji.umanjiapp.model;

import org.json.JSONObject;


public class KeywordsData {
    private String owner;
    private String id;
    private String name;
    private String point;

    public KeywordsData(JSONObject jsonObject) {
        this.owner  = jsonObject.optString("owner");
        this.id     = jsonObject.optString("id");
        this.name   = jsonObject.optString("name");
        this.point   = jsonObject.optString("point");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return point;
    }

    public void setType(String point) {
        this.point = point;
    }
}
