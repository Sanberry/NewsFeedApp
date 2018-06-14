package com.example.android.newsfeedapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class NewsFeedActivity extends AppCompatActivity implements LoaderCallbacks<List<NewsFeed>> {
    private static final String LOG_TAG = NewsFeedActivity.class.getName();

    private static final String USGS_REQUEST_URL = "https://content.guardianapis.com/search?api-key=0e9f4aa9-1182-41b9-a7c1-32ded52e7486&show-tags=contributor";

    private static final int NEWS_LOADER_ID = 1;

    private NewsFeedAdapter mAdapter;

    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "NewsFeedActivity onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        ListView newsFeedListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsFeedListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new NewsFeedAdapter(this, new ArrayList<NewsFeed>());
        newsFeedListView.setAdapter(mAdapter);

        newsFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsFeed currentNewsFeed = mAdapter.getItem(position);

                Uri newsFeedUri = Uri.parse(currentNewsFeed.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsFeedUri);

                startActivity(websiteIntent);
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<NewsFeed>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String section = sharedPrefs.getString(getString(R.string.settings_section_key), getString(R.string.settings_section_key_default));
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("section", section);

        return new NewsFeedLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsFeed>> loader, List<NewsFeed> newsFeeds) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_news);
        mAdapter.clear();

        if (newsFeeds != null && !newsFeeds.isEmpty()) {
            mAdapter.addAll(newsFeeds);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsFeed>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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


