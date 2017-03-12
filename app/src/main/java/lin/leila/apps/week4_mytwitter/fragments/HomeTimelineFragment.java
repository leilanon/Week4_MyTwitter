package lin.leila.apps.week4_mytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import lin.leila.apps.week4_mytwitter.TwitterApplication;
import lin.leila.apps.week4_mytwitter.TwitterClient;
import lin.leila.apps.week4_mytwitter.models.Tweet;

/**
 * Created by Leila on 2017/3/12.
 */

public class HomeTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    long maxId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, parent, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
        populateTimeline(0);
    }

    @Override
    public void populateTimeline(int page) {
        if (page == 0) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    addAll(Tweet.fromJSONArray(json));
                    maxId = getLastTweetUid();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } else if (page > 0){
            client.getMoreHomeTimeline(maxId, new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    if (Tweet.fromJSONArray(json).size() > 1) {
                        addAll(Tweet.fromJSONArray(json));
                        maxId = getLastTweetUid();
                    }
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    try {
                        Snackbar.make(getView(),
                                errorResponse.getJSONArray("errors")
                                        .getJSONObject(0)
                                        .get("message").toString(),
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void insertNewTweet(String tweetId) {
        client.getSingleTweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                insert(Tweet.fromJSON(json));
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                populateTimeline(0);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
