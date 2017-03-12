package lin.leila.apps.week4_mytwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import lin.leila.apps.week4_mytwitter.R;
import lin.leila.apps.week4_mytwitter.TwitterApplication;
import lin.leila.apps.week4_mytwitter.fragments.HomeTimelineFragment;
import lin.leila.apps.week4_mytwitter.fragments.MentionsTimelineFragment;

public class TimelineActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabTweet = (FloatingActionButton) findViewById(R.id.fabTweet);
        fabTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
                i.putExtra("tweet_id", "");
                i.putExtra("screen_name", "");
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.miExit) {
            onExitApp(item);
            return true;
        }
        if (id == R.id.miProfile) {
            onProfileView(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem mi) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("screen_name", "");
        startActivity(i);
    }

    public void onExitApp(MenuItem mi) {
        TwitterApplication.getRestClient().clearAccessToken();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitle[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

        @Override
        public int getCount() {
            return tabTitle.length;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            HomeTimelineFragment f = (HomeTimelineFragment) getSupportFragmentManager().getFragments().get(0);
            f.insertNewTweet(data.getStringExtra("tweet_id"));
            Snackbar.make(f.getView(), "New Tweet", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            Log.d("DEBUG", "resultCode/requestCode: " + resultCode + "/" + requestCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
