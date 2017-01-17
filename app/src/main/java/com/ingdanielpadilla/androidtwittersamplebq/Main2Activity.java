package com.ingdanielpadilla.androidtwittersamplebq;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    Gson gson;
    String json;
    UserTimeline userTimeline;
    MyTweetTimelineListAdapter adapter;
    SharedPreferences  mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creating a shared preference
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.content_main2);
        ListView myTweetListView = (ListView) findViewById(R.id.tweet_list);


            userTimeline = new UserTimeline.Builder()
                    .screenName("realDonaldTrump")
                    .build();


        adapter = new MyTweetTimelineListAdapter(this,userTimeline);
        adapter.load(mPrefs);


        myTweetListView.setAdapter(adapter);
        Log.d("TwitterKit", adapter.toString());





        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                    @Override
                    public void success(Result<TimelineResult<Tweet>> result) {
                        swipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        swipeLayout.setRefreshing(false);
                        Toast.makeText(Main2Activity.this, "No se pudo conectar con el servidor de Twitter", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("TwitterKit", String.valueOf(adapter.getCount()));
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Main2Activity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("TwitterKite", String.valueOf(adapter.getCount()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TwitterKite", String.valueOf(adapter.getCount()));
        adapter.save(mPrefs);
    }
}
