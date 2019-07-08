package com.byted.camp.MiniDouyin.beans;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:17
 */
public class FeedResponse {

    // TODO-C2 (2) Implement your FeedResponse Bean here according to the response json
    @SerializedName("feeds") private List<Feed> feed;
    @SerializedName("success") private boolean success;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Feed> getFeed(){return feed;}

    public void setFeed(List<Feed> feed) {this.feed = feed;}

    @Override public String toString() {
        return "Value{" +
                "Feed=" + feed +
                "success=" + success +
                '}';
    }
}
