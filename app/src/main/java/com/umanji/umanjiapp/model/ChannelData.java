package com.umanji.umanjiapp.model;


import android.text.TextUtils;
import android.util.Log;

import com.umanji.umanjiapp.helper.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ChannelData {
    private static final String TAG = "ChannelData";

    private JSONObject jsonObject;
    private ChannelData owner;
    private String ownerId;

    private ChannelData parent;
    private String parentId;

    private String id;

    private String email;
    private String phone;
    private String type;
    private String subType;
    private String name;
    private JSONObject desc;
    private int point;
    private int level;


    private String [] photos;
    private String [] roles;
    private String [] keywords;
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

    private String startDay;
    private String endDay;

    private String createdAt;
    private String updatedAt;


    public ChannelData() {}

    public ChannelData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            initData(jsonObject);
        } catch (Exception e) {
            Log.e("Document: Error: ", e.toString());
        }
    }

    public ChannelData(JSONObject jsonObject) {
        initData(jsonObject);
    }

    private void initData(JSONObject jsonObject) {
        try {
            JSONObject data = jsonObject;
            if(jsonObject.optJSONObject("data") != null) {
                data = jsonObject.optJSONObject("data");
            }

            this.jsonObject =  data;

            if(data.optJSONObject("owner") != null) {
                this.owner = new ChannelData(data.optJSONObject("owner"));
            }else {
                this.ownerId = data.optString("owner");
            }

            if(data.optJSONObject("parent") != null) {
                this.parent = new ChannelData(data.optJSONObject("parent"));
            }else {
                this.parentId = data.optString("parent");
            }


            this.id = data.optString("id");
            this.email = data.optString("email");
            this.phone = data.optString("phone");
            this.type = data.optString("type");
            this.subType = data.optString("subType");

            this.name = data.optString("name");
            this.desc = data.optJSONObject("desc");
            this.level = data.optInt("level");
            this.point = data.optInt("point");


            this.address = data.optString("address");
            this.address = data.optString("address");

            this.photos = ApiHelper.getStringArray(data, "photos");
            this.roles = ApiHelper.getStringArray(data, "roles");
            this.keywords = ApiHelper.getStringArray(data, "keywords");
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

            this.startDay = data.optString("startDay");
            this.endDay = data.optString("endDay");

            this.createdAt = data.optString("createdAt");
            this.updatedAt= data.optString("updatedAt");


        } catch (Exception e) {
            Log.e("Document: Error: ", e.toString());
        }
    }

    public void setAddressJSONObject(ChannelData channelData) {
        this.address = channelData.getAddress();
        this.countryCode = channelData.getCountryCode();
        this.countryName = channelData.getCountryName();
        this.adminArea = channelData.getAdminArea();
        this.locality = channelData.getLocality();
        this.thoroughfare = channelData.getThoroughfare();
        this.featureName = channelData.getFeatureName();
        this.latitude = channelData.getLatitude();
        this.longitude = channelData.getLongitude();
    }

    public JSONObject getAddressJSONObject() {
        JSONObject params = new JSONObject();
        try {
            params.put("address", this.address);
            params.put("countryCode", this.countryCode);
            params.put("countryName", this.countryName);
            params.put("adminArea", this.adminArea);
            params.put("locality", this.locality);
            params.put("thoroughfare", this.thoroughfare);
            params.put("featureName", this.featureName);
            params.put("thoroughfare", this.thoroughfare);
            params.put("latitude", this.latitude);
            params.put("longitude", this.longitude);

        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }

        return params;
    }

    public void setAddressJSONObject(JSONObject params) {
        try {
            params.put("address", this.address);
            params.put("countryCode", this.countryCode);
            params.put("countryName", this.countryName);
            params.put("adminArea", this.adminArea);
            params.put("locality", this.locality);
            params.put("thoroughfare", this.thoroughfare);
            params.put("featureName", this.featureName);
            params.put("thoroughfare", this.thoroughfare);
            params.put("latitude", this.latitude);
            params.put("longitude", this.longitude);

        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
    }

    public boolean isOwner(String userId) {

        if(userId.equals(this.ownerId)) {
            return true;
        } else if(this.owner != null && userId.equals(this.owner.getId())) {
            return true;
        }

        return false;
    }

    public String getActionId(String type, String userId) {
        ArrayList<SubLinkData> subLinkDatas = this.getSubLinks(type);
        if(subLinkDatas != null && subLinkDatas.size() > 0) {
            Iterator<SubLinkData> iterator = subLinkDatas.iterator();

            while(iterator.hasNext()) {
                SubLinkData data = iterator.next();
                if(data.getOwner().equals(userId)) {
                    return data.getId();
                }
            }
        }

        return "";
    }

    public String getActionName(String type, String userId) {
        ArrayList<SubLinkData> subLinkDatas = this.getSubLinks(type);
        if(subLinkDatas != null && subLinkDatas.size() > 0) {
            Iterator<SubLinkData> iterator = subLinkDatas.iterator();

            while(iterator.hasNext()) {
                SubLinkData data = iterator.next();
                if(data.getOwner().equals(userId)) {
                    return data.getName();
                }
            }
        }

        return "";
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public ChannelData getOwner() {
        return owner;
    }

    public void setOwner(ChannelData owner) {
        this.owner = owner;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ChannelData getParent() {
        return parent;
    }

    public void setParent(ChannelData parent) {
        this.parent = parent;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        if(TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)) {
            name = email.substring(0, email.indexOf("@"));
        }
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public JSONObject getDesc() {
        return desc;
    }

    public void setDesc(JSONObject desc) {
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public String getPhoto() {

        if(this.photos != null)
            return this.photos[0];
        else
            return null;
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

        if(subLinks != null && subLinks.size() > 0) {
            ArrayList<SubLinkData> newArray = new ArrayList<SubLinkData>();
            for (int idx=0; idx< subLinks.size(); idx++) {
                SubLinkData data = subLinks.get(idx);

                if(data.getType().equals(type)) {
                    newArray.add(data);
                }
            }
            return newArray;
        }
        return null;
    }

    public ArrayList<SubLinkData> getSubLinks(String type, String name) {

        if(subLinks != null && subLinks.size() > 0) {
            ArrayList<SubLinkData> newArray = new ArrayList<SubLinkData>();
            for (int idx=0; idx< subLinks.size(); idx++) {
                SubLinkData data = subLinks.get(idx);

                if(TextUtils.equals(data.getType(), type) && TextUtils.equals(data.getName(), name)) {
                    newArray.add(data);
                }
            }
            return newArray;
        }
        return null;
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

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
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
