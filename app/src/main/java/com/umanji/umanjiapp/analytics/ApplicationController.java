package com.umanji.umanjiapp.analytics;


import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class ApplicationController extends Application {
    private static final String PROPERTY_ID = "UA-75995705-1";

    public synchronized Tracker getTracker(){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker t = analytics.newTracker(PROPERTY_ID);
        return t;
    }
}