package lin.leila.apps.week4_mytwitter.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import lin.leila.apps.week4_mytwitter.R;
import lin.leila.apps.week4_mytwitter.TwitterApplication;
import lin.leila.apps.week4_mytwitter.TwitterClient;
import lin.leila.apps.week4_mytwitter.fragments.UserTimelineFragment;
import lin.leila.apps.week4_mytwitter.models.User;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = TwitterApplication.getRestClient();
        String screenName = getIntent().getStringExtra("screen_name");

        if (screenName.length() > 0) {
            client.getOtherProfile(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    user = User.fromJSON(response);
                    toolbar.setTitle("@" + user.getScreenName());
                    populateProfileHeader(user);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Snackbar.make(getCurrentFocus(), "Fail request", Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            client.getUserProfile(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    user = User.fromJSON(response);
                    toolbar.setTitle("@" + user.getScreenName());
                    populateProfileHeader(user);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Snackbar.make(getCurrentFocus(), "Fail request", Snackbar.LENGTH_LONG).show();
                }
            });
        }

        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
    }

    private void populateProfileHeader(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvFullName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollower = (TextView) findViewById(R.id.tvFollower);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImg = (ImageView) findViewById(R.id.ivProfileImg);

        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollower.setText(user.getFollowersCount() + " Follower(s)");
        tvFollowing.setText(user.getFollowingsCount() + " Following(s)");
        Glide.with(this).load(user.getProfileImgUrl()).into(ivProfileImg);
    }

}
