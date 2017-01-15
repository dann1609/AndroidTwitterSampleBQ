package com.ingdanielpadilla.androidtwittersamplebq;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.UserTimeline;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName("@realDonaldTrump")
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();

        ListView myTweetListView = (ListView) findViewById(R.id.content_main2);

        myTweetListView.setAdapter(adapter);
        Log.d("TwitterKit", adapter.toString());


/*
        // TODO: Use a more specific parent
        final ViewGroup parentView = (ViewGroup) findViewById(R.id.content_main2);
        // TODO: Base this Tweet ID on some data from elsewhere in your app
        long tweetId = 631879971628183552L;
        long tweetId2= 820725491812487170L;
        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                TweetView tweetView = new TweetView(Main2Activity.this, result.data);
                parentView.addView(tweetView);
                Log.d("TwitterKit", result.data.text);
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Load Tweet failure", exception);
            }
        });*/

    }

}
