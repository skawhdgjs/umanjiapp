package com.umanji.umanjiapp.model;


import android.util.Log;

import com.umanji.umanjiapp.helper.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class UserData {
    private static final String TAG = "UserData";

    private String id;
    private String email;
    private String name;
    private String desc;
    private String point;
    private int level;

    private String [] userPhotos;
    private String [] photos;
    private String [] roles;
    private String [] keywords;
    private String [] links;
    private String [] actions;

    protected ArrayList<SubLinkData> subLinks;



    private String address;
    private String addressRoad;

    private String countryCode;
    private String countryName;
    private String adminArea;
    private String locality;
    private String thoroughfare;
    private String featureName;

    private double latitude;
    private double longitude;

    private String createdAt;
    private String updatedAt;

    public UserData(JSONObject jsonObject) {
        try {
            JSONObject data = jsonObject;
            if(jsonObject.optJSONObject("data") != null) {
                data = jsonObject.optJSONObject("data");
            }

            this.id = data.optString("id");
            this.email = data.optString("email");

            this.name = data.optString("name");
            this.desc = data.optString("desc");
            this.point= data.optString("point");

            this.level = data.optInt("level");

            this.address = data.optString("address");
            this.address = data.optString("address");

            this.userPhotos= ApiHelper.getStringArray(data, "userPhotos");
            this.photos = ApiHelper.getStringArray(data, "photos");
            this.roles = ApiHelper.getStringArray(data, "roles");
            this.keywords = ApiHelper.getStringArray(data, "keywords");
            this.links = ApiHelper.getStringArray(data, "links");
            this.actions = ApiHelper.getStringArray(data, "actions");

            this.setSubLinks(data.optJSONArray("subLinks"));



            this.address = data.optString("address");

            this.countryCode = data.optString("countryCode");
            this.countryName = data.optString("countryName");
            this.adminArea = data.optString("adminArea");
            this.locality = data.optString("locality");
            this.thoroughfare = data.optString("thoroughfare");
            this.featureName = data.optString("featureName");

            this.latitude = data.optDouble("latitude");
            this.longitude = data.optDouble("longitude");

            this.createdAt = data.optString("createdAt");
            this.updatedAt= data.optString("updatedAt");


        } catch (Exception e) {
            Log.e("Document: Error: ", e.toString());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String[] getUserPhotos() {
        return userPhotos;
    }

    public void setUserPhotos(String[] userPhotos) {
        this.userPhotos = userPhotos;
    }

    public String getUserPhoto() {

        if(this.userPhotos != null)
            return this.userPhotos[0];
        else
            return null;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public String[] getLinks() {
        return links;
    }

    public void setLinks(String[] links) {
        this.links = links;
    }

    public String[] getActions() {
        return actions;
    }

    public void setActions(String[] actions) {
        this.actions = actions;
    }


    public ArrayList<SubLinkData> getSubLinks() {
        return subLinks;
    }

    public ArrayList<SubLinkData> getSubLinks(String type) {

        ArrayList<SubLinkData> newArray = new ArrayList<SubLinkData>();

        for (int idx=0; idx< subLinks.size(); idx++) {
            SubLinkData data = subLinks.get(idx);

            if(data.getType().equals(type)) {
                newArray.add(data);
            }
        }
        return newArray;
    }

    public void setSubLinks(JSONArray jsonArray) {
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                this.subLinks = new ArrayList<SubLinkData>();
                for (int idx=0; idx<jsonArray.length(); idx++){
                    SubLinkData doc = new SubLinkData(jsonArray.getJSONObject(idx));
                    this.subLinks.add(doc);
                }
            }
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressRoad() {
        return addressRoad;
    }

    public void setAddressRoad(String addressRoad) {
        this.addressRoad = addressRoad;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(String thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
