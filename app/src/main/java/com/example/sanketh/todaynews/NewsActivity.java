package com.example.sanketh.todaynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = NewsActivity.class.getSimpleName();
    private static final String GUARDIAN_API = "https://content.guardianapis.com/search?show-fields=headline&show-tags=contributor&api-key=5b2d472a-d57e-40db-a9fa-8fb6caa4f805";
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST: News Activity onCreate() called ....");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.loading_indicator);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        ListView newsListView = findViewById(R.id.list_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                News currentNews = mAdapter.getItem(position);

                assert currentNews != null;
                Uri newsUrl = Uri.parse(currentNews.getWebUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUrl);

                startActivity(websiteIntent);
            }
        });


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If there is a network connection, fetch data
        if (isConnected) {
            Log.i(LOG_TAG, "TEST: calling initLoader() ....");
            getSupportLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(getString(R.string.no_internet));
        }

    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called ....");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String searchTerms = sharedPrefs.getString(
                getString(R.string.settings_search_terms_key),
                getString(R.string.settings_search_terms_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_API);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("q", searchTerms);
        uriBuilder.appendQueryParameter("order-by", orderBy);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> newsList) {

        Log.i(LOG_TAG, "TEST: onLoadFinished() called ....");

        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_news);

        mAdapter.clear();


        if (newsList != null && !newsList.isEmpty()) {
            mAdapter.addAll(newsList);
        }

    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        Log.i(LOG_TAG, "TEST: onLoaderReset() called ....");
        mAdapter.clear();

    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
