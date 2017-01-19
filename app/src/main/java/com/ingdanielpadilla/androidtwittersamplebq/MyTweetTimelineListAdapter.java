package com.ingdanielpadilla.androidtwittersamplebq;

/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * Created by daniel.padilla on 16/1/2017.
 * Base code taken from from com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
 * Some function was edited another was created according requirements
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.BaseTweetView;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.lang.reflect.Type;
import java.util.ArrayList;



public class MyTweetTimelineListAdapter  extends MyTimelineListAdapter<Tweet> {

    ArrayList<Tweet> mStoredTweets;

    protected Callback<Tweet> actionCallback;
    final protected int styleResId;
    protected TweetUi tweetUi;

    static final String TOTAL_FILTERS_JSON_PROP = "total_filters";
    static final String DEFAULT_FILTERS_JSON_MSG = "{\"total_filters\":0}";
    final Gson gson = new Gson();

    /**
     * Constructs a TweetTimelineListAdapter for the given Tweet Timeline.
     * @param context the context for row views.
     * @param timeline a Timeline&lt;Tweet&gt; providing access to Tweet data items.
     * @throws java.lang.IllegalArgumentException if timeline is null
     */
    public MyTweetTimelineListAdapter(Context context, Timeline<Tweet> timeline) {
        this(context, timeline, com.twitter.sdk.android.tweetui.R.style.tw__TweetLightStyle, null);
    }

    MyTweetTimelineListAdapter(Context context, Timeline<Tweet> timeline, int styleResId,
                             Callback<Tweet> cb) {
        this(context, new MyTimelineDelegate<>(timeline), styleResId, cb, TweetUi.getInstance());
    }

    MyTweetTimelineListAdapter(Context context, MyTimelineDelegate<Tweet> delegate, int styleResId,
                               Callback<Tweet> cb, TweetUi tweetUi){
        this(context,delegate,styleResId,cb,tweetUi,null);
    }

    MyTweetTimelineListAdapter(Context context, MyTimelineDelegate<Tweet> delegate, int styleResId,
                             Callback<Tweet> cb, TweetUi tweetUi,Callback<TimelineResult<Tweet>>rCb) {
        super(context, delegate,rCb);
        this.styleResId = styleResId;
        this.actionCallback = new MyTweetTimelineListAdapter.ReplaceTweetCallback(delegate, cb);
        this.tweetUi = tweetUi;

    }

    /**
     * Returns a CompactTweetView by default. May be overridden to provide another view for the
     * Tweet item. If Tweet actions are enabled, be sure to call setOnActionCallback(actionCallback)
     * on each new subclass of BaseTweetView to ensure proper success and failure handling
     * for Tweet actions (favorite, unfavorite).
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final Tweet tweet = getItem(position);
        if (rowView == null) {
            final BaseTweetView tv = new CompactTweetView(context, tweet, styleResId);
            tv.setOnActionCallback(actionCallback);
            rowView = tv;
        } else {
            ((BaseTweetView) rowView).setTweet(tweet);
        }
        rowView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // it was the 2nd button
            }
        });
        return rowView;
    }



    /*
     * On success, sets the updated Tweet in the TimelineDelegate to replace any old copies
     * of the same Tweet by id.
     */
    static class ReplaceTweetCallback extends Callback<Tweet> {
        MyTimelineDelegate<Tweet> delegate;
        Callback<Tweet> cb;

        ReplaceTweetCallback(MyTimelineDelegate<Tweet> delegate, Callback<Tweet> cb) {
            this.delegate = delegate;
            this.cb = cb;
        }

        @Override
        public void success(Result<Tweet> result) {
            delegate.setItemById(result.data);
            if (cb != null) {
                cb.success(result);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (cb != null) {
                cb.failure(exception);
            }
        }
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
       if(position<super.getCount()){
           return super.getItem(position);
       }else{
           if(isLastPosition(position)) {
               delegate.previous();
           }
           return mStoredTweets.get(position);
       }
    }

    public boolean isLastPosition(int position){
        return position==getCount()-1;
    }

    @Override
    public long getItemId(int position) {
        if(position<super.getCount()){return super.getItemId(position);}
            else{return mStoredTweets.get(position).getId();}
    }

    @Override
    public void refresh(Callback<TimelineResult<Tweet>> cb) {
        super.refresh(cb);
    }

    @Override
    public void save() {
        save(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public void load(){
        load(PreferenceManager.getDefaultSharedPreferences(context));
    }

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
