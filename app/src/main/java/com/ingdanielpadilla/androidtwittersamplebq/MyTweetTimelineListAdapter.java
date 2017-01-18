package com.ingdanielpadilla.androidtwittersamplebq;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.BaseTweetView;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineFilter;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.tweetui.UserTimeline;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by daniel.padilla on 16/1/2017.
 */

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
                             Callback<Tweet> cb, TweetUi tweetUi) {
        super(context, delegate);
        this.styleResId = styleResId;
        this.actionCallback = new MyTweetTimelineListAdapter.ReplaceTweetCallback(delegate, cb);
        this.tweetUi = tweetUi;

        //scribeTimelineImpression();
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

    /*private void scribeTimelineImpression() {
        final String jsonMessage;
        if (delegate instanceof FilterTimelineDelegate) {
            final FilterTimelineDelegate filterTimelineDelegate = (
                    FilterTimelineDelegate) delegate;
            final TimelineFilter timelineFilter = filterTimelineDelegate.timelineFilter;
            jsonMessage = getJsonMessage(timelineFilter.totalFilters());
        } else {
            jsonMessage = DEFAULT_FILTERS_JSON_MSG;
        }

        final ScribeItem scribeItem = ScribeItem.fromMessage(jsonMessage);
        final List<ScribeItem> items = new ArrayList<>();
        items.add(scribeItem);

        final String timelineType = getTimelineType(delegate.getTimeline());
        tweetUi.scribe(ScribeConstants.getSyndicatedSdkTimelineNamespace(timelineType));
        tweetUi.scribe(ScribeConstants.getTfwClientTimelineNamespace(timelineType), items);
    }*/

    private String getJsonMessage(int totalFilters) {
        final JsonObject message = new JsonObject();
        message.addProperty(TOTAL_FILTERS_JSON_PROP, totalFilters);
        return gson.toJson(message);
    }

    /*static String getTimelineType(Timeline timeline) {
        if (timeline instanceof BaseTimeline) {
            return ((BaseTimeline) timeline).getTimelineType();
        }
        return "other";
    }*/

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
