package com.predrague.moviereviews;

import android.app.Application;

import okhttp3.Cache;

public class MovieReviews extends Application {
    public static Cache cache;
    public static long cacheSize = 5 * 1024 * 1024;

    @Override
    public void onCreate() {
        super.onCreate();
        cache = new Cache(getApplicationContext().getCacheDir(), cacheSize);
    }
}