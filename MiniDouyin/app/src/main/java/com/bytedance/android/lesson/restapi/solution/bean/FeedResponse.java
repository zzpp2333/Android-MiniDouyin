

package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:17
 */
public class FeedResponse {

    // TODO-C2 (2) Implement your FeedResponse Bean here according to the response json
    @SerializedName("feeds")
    private List<Feed> feedlist;
    @SerializedName("success")
    private boolean Suc;

    public boolean getSuc(){
        return this.Suc;
    }
    public List<Feed> getFeedlist(){
        return this.feedlist;
    }

    @Override public String toString() {
        return "Value{" +
                "Feed=" + feedlist+
                "success=" + Suc +
                '}';
    }
}
