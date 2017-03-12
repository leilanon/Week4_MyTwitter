package lin.leila.apps.week4_mytwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import lin.leila.apps.week4_mytwitter.R;
import lin.leila.apps.week4_mytwitter.TwitterApplication;
import lin.leila.apps.week4_mytwitter.TwitterClient;
import lin.leila.apps.week4_mytwitter.models.User;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;
    User user;

    TextView tvUsername;
    ImageView ivProfileImg;
    EditText etBody;
    FloatingActionButton fabCompose;
    TextView tvRemain;
//
//    SharedPreferences spref;
//    SharedPreferences.Editor editor;
//    public static final String KEY = "lin.leila.week3_twitterclient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = TwitterApplication.getRestClient();
        findView();

        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                user = User.fromJSON(response);
                toolbar.setTitle("@" + user.getScreenName());
                init(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("DEBUG", "Request Failed.");
                Snackbar.make(getCurrentFocus(), "Fail request", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void findView() {
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImg);
        etBody = (EditText) findViewById(R.id.etBody);
        fabCompose = (FloatingActionButton) findViewById(R.id.fabCompose);
        tvRemain = (TextView) findViewById(R.id.tvRemain);
    }

    public void init(User user) {

        tvUsername.setText("@" + user.getScreenName());
        Glide.with(this).load(user.getProfileImgUrl()).into(ivProfileImg);

        etBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (etBody.getText().toString().length() > 140) {
                    Snackbar.make(getCurrentFocus(), "Status is over 140 characters.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        etBody.addTextChangedListener(etWatcher);

        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBody.getText().toString().length() > 140) {
                    Snackbar.make(v, "Status is over 140 characters.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    client.composeNewTweet(etBody.getText().toString(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            getTweet(response);
                            ComposeActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", "POST_FAIL: "+errorResponse.toString());
                            setResult(RESULT_CANCELED);
                        }
                    });
                }
            }
        });
    }


    public void getTweet(JSONObject response) {
        Intent intent = new Intent();
        String tweetId = "";

        try {
            tweetId = response.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (tweetId.length() > 0) {
            intent.putExtra("tweet_id", tweetId);
            intent.putExtra("resCode", 200);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
            Log.d("DEBUG", "SINGLE_RESULT: FAIL");
        }
    }


    private final TextWatcher etWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tvRemain.setText(String.valueOf(140 - s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };
}
