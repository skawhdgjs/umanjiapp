package com.umanji.umanjiapp.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;


public class NotyData implements Serializable {

    public ChannelData from;
    public ChannelData to;
    public ChannelData channel;
    public boolean read;

    public NotyData(JSONObject jsonObject) {
        try {
            JSONObject data = jsonObject;
            if(jsonObject.optJSONObject("data") != null) {
                data = jsonObject.optJSONObject("data");
            }

            this.from       = new ChannelData(data.optJSONObject("from"));
            this.to         = new ChannelData(data.optJSONObject("to"));
            this.channel    = new ChannelData(data.optJSONObject("channel"));
            this.read       = data.optBoolean("read");
        } catch (Exception e) {
            Log.e("Document: Error: ", e.toString());
        }
    }

    public ChannelData getFrom() {
        return from;
    }

    public void setFrom(ChannelData from) {
        this.from = from;
    }

    public ChannelData getTo() {
        return to;
    }

    public void setTo(ChannelData to) {
        this.to = to;
    }

    public ChannelData getChannel() {
        return channel;
    }

    public void setChannel(ChannelData channel) {
        this.channel = channel;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
