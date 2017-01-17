package com.ingdanielpadilla.androidtwittersamplebq;

import android.content.Context;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

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
    public MyTweetTimelineListAdapter(Context context, Timeline<Tweet> timeline) {
        super(context, timeline);
    }

    @Override
    public int getCount() {
        int x=super.getCount();
        if (x==0){
            return 2;
        }
        else{
            return x;
        }
    }

    @Override
    public Tweet getItem(int position) {
       if(position<super.getCount()){return super.getItem(position);}
        else{return null;}
    }

    @Override
    public void refresh(Callback<TimelineResult<Tweet>> cb) {
        super.refresh(cb);
    }
}
