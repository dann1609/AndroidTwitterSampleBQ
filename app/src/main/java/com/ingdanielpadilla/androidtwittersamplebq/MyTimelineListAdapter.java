package com.ingdanielpadilla.androidtwittersamplebq;

/**
 * Created by daniel.padilla on 17/1/2017.
 */

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

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.models.Identifiable;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineResult;

/**
 * TimelineListAdapter is a ListAdapter providing timeline items for ListViews.
 * Concrete subclasses must define a type parameter and implement getView.
 */

public abstract class MyTimelineListAdapter<T extends Identifiable> extends BaseAdapter {
    protected final Context context;
    protected final MyTimelineDelegate<T> delegate;

    /**
     * Constructs a TimelineListAdapter for the given Timeline.
     * @param context the context for row views.
     * @param timeline a Timeline providing access to timeline data items.
     * @throws java.lang.IllegalArgumentException if context or timeline is null
     */
    public MyTimelineListAdapter(Context context, Timeline<T> timeline) {
        this(context, new MyTimelineDelegate<>(timeline));
    }

    MyTimelineListAdapter(Context context, MyTimelineDelegate<T> delegate) {
        this(context, delegate,null);
    }

    MyTimelineListAdapter(Context context, MyTimelineDelegate<T> delegate,Callback<TimelineResult<T>> cb) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        this.context = context;
        this.delegate = delegate;
        delegate.refresh(myCb);
    }



    /**
     * Clears the items and loads the latest Timeline items.
     */
    public void refresh(Callback<TimelineResult<T>> cb) {
        delegate.refresh(cb);
    }

    @Override
    public int getCount() {
        return delegate.getCount();
    }

    @Override
    public T getItem(int position) {
        return delegate.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return delegate.getItemId(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        delegate.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        delegate.unregisterDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        delegate.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        delegate.notifyDataSetInvalidated();
    }

    Callback<TimelineResult<T>> myCb=new Callback<TimelineResult<T>>() {
        @Override
        public void success(Result<TimelineResult<T>> result) {
            save();
        }

        @Override
        public void failure(TwitterException exception) {
            notifyFailure(exception);
        }
    };

    public void notifyFailure(TwitterException exception){
        int er=0;
        try {
            er=((TwitterApiException) exception).getStatusCode();
        }catch (Exception e){};
        String msg="";
        if(er==404){
            msg="This user do not exist! \nLoading last user searched";
        }
        else{
            msg=exception.getMessage();
        }
        Toast.makeText(context,msg , Toast.LENGTH_SHORT).show();
    }

    public void save(){}
}

