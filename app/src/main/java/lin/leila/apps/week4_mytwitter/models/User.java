package lin.leila.apps.week4_mytwitter.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leila on 2017/3/12.
 */

public class User {
    // list attribute
    private String name;
    private long uid;
    private String screenName;
    private String profileImgUrl;
    private String tagline;
    private int followersCount;
    private int followingsCount;

    public String getTagline() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingsCount() {
        return followingsCount;
    }


    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    // deserialize the user json => User
    public static User fromJSON(JSONObject json) {
        User u = new User();
        // Extract and fill the value
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImgUrl = json.getString("profile_image_url");
            u.tagline = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.followingsCount = json.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return an user
        return u;
    }
}
