package lin.leila.apps.week4_mytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import lin.leila.apps.week4_mytwitter.EndlessScrollListener;
import lin.leila.apps.week4_mytwitter.R;
import lin.leila.apps.week4_mytwitter.TweetsArrayAdapter;
import lin.leila.apps.week4_mytwitter.models.Tweet;

/**
 * Created by Leila on 2017/3/12.
 */

public abstract class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    public abstract void populateTimeline(int page);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                populateTimeline(page);
                return true;
            }
        });

        return v;
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }
    public void insert(Tweet tweet) { aTweets.insert(tweet, 0);}
    public void notifyDataSetChanged() { aTweets.notifyDataSetChanged(); }
    public long getLastTweetUid() { return tweets.get(tweets.size()-1).getUid(); }
}
