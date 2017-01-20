package com.ingdanielpadilla.androidtwittersamplebq;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.UserTimeline;

import io.fabric.sdk.android.Fabric;

public class ScrollingActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "TKrDYmwkijsXGYkSCA4ytNBZm";
    private static final String TWITTER_SECRET = "rVN9jhBZJGlzZ0q9W5jSIhAlkPe9mPjBJPF3tcmngeoX86nUw8";

    UserTimeline userTimeline;
    MyTweetTimelineListAdapter adapter;
    SharedPreferences mPrefs;
    ListView myTweetListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final android.widget.SearchView searchView = (android.widget.SearchView) findViewById(R.id.search);
        setSupportActionBar(toolbar);

        //SearchView listener is initiated.
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //User time line is created.
                createUserTimeline(query);
                //Set focus again in list view.
                myTweetListView.requestFocus();
                //Set Softkeyboard hidden
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(myTweetListView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        //Creating a shared preference
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.content_scrolling);
        myTweetListView = (ListView) findViewById(R.id.tweet_list);

        //Creating by default Donald Trump tweet list.
        createUserTimeline("realDonaldTrump");

        //For any SDK lower than Lollipop Collapsing toolbar won't work.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myTweetListView.setNestedScrollingEnabled(true);
            myTweetListView.startNestedScroll(View.OVER_SCROLL_ALWAYS);
        }

        //Setting Swipe layout listener.
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
                        adapter.notifyFailure(exception);
                    }
                });
            }
        });



        //Request at beginning focus in List view
        myTweetListView.requestFocus();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(ScrollingActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //Saving tweets list.
        adapter.save();
    }

    public void createUserTimeline(String user){

        //Creating user timeline.
        userTimeline = new UserTimeline.Builder()
                .screenName(user)
                .build();

        //Creating adapter with UserTimeLine created.
        adapter = new MyTweetTimelineListAdapter(this,userTimeline);
        //Loading stored tweets.
        adapter.load();

        //Setting adapter in list View
        myTweetListView.setAdapter(adapter);
    }


}
