package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentResponse {

    // TODO-C2 (2) Implement your FeedResponse Bean here according to the response json
    @SerializedName("data")
    private List<Comment> commentlist;
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;

    public int getstatus() {
        return this.status;
    }

    public List<Comment> getcommentlist() {
        return this.commentlist;
    }
    public int getStatus(){
        return this.status;
    }
    public String getMsg(){
        return this.msg;
    }

    @Override
    public String toString() {
        return "Value{" +
                "Comment=" + commentlist +
                "status=" + status +
                "msg="+msg+
                '}';
    }
}
