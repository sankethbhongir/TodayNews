package com.example.sanketh.todaynews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of news feeds by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private static final String LOG_TAG = NewsLoader.class.getSimpleName();
    private String url;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading() called ....");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Nullable
    @Override
    public List<News> loadInBackground() {

        Log.i(LOG_TAG, "TEST: loadInBackground() called ....");

        if (url == null)
            return null;

        return Utils.fetchNewsData(url);
    }
}
