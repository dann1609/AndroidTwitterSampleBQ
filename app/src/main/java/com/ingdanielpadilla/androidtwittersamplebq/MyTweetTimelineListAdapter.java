package com.ingdanielpadilla.androidtwittersamplebq;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by daniel.padilla on 16/1/2017.
 */

public class MyTweetTimelineListAdapter extends TweetTimelineListAdapter {
    /**
     * Constructs a TweetTimelineListAdapter for the given Tweet Timeline.
     *
     * @param context  the context for row views.
     * @param timeline a Timeline&lt;Tweet&gt; providing access to Tweet data items.
     * @throws IllegalArgumentException if timeline is null
     */

    ArrayList<Tweet> mStoredTweets;
    public MyTweetTimelineListAdapter(Context context, Timeline<Tweet> timeline) {
        super(context, timeline);
    }

    @Override
    public int getCount() {
        int x=super.getCount();
        if (x==0){
            return mStoredTweets.size();
        }
        else{
            return x;
        }
    }

    @Override
    public Tweet getItem(int position) {
       if(position<super.getCount()){return super.getItem(position);}
        else{return mStoredTweets.get(position);}
    }

    @Override
    public void refresh(Callback<TimelineResult<Tweet>> cb) {
        super.refresh(cb);
    }

    Callback<TimelineResult<Tweet>> myCb=new Callback<TimelineResult<Tweet>>() {
        @Override
        public void success(Result<TimelineResult<Tweet>> result) {
        }

        @Override
        public void failure(TwitterException exception) {
        }
    };

    public void save(SharedPreferences mPrefs){

        if(super.getCount()>0) {
            ArrayList<Tweet> mAL = new ArrayList<Tweet>();

            for (int i = 0; i < super.getCount(); i++) {
                mAL.add(super.getItem(i));
            }

            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mAL);
            prefsEditor.putString("StoredTweets", json);
            prefsEditor.commit();
        }
    }

    public void load(SharedPreferences mPrefs){
        Gson gson = new Gson();
        String json = mPrefs.getString("StoredTweets", null);
        if(json==null){
            this.mStoredTweets = new ArrayList<Tweet>();
        }else {
            Type type = new TypeToken<ArrayList<Tweet>>() {
            }.getType();
            this.mStoredTweets = gson.fromJson(json, type);
        }
    }

}
