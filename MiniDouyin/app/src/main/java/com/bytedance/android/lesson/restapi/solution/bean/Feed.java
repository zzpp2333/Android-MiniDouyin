package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:18
 */
public class Feed {

    // TODO-C2 (1) Implement your Feed Bean here according to the response json


    /**
     * @author Xavier.S
     * @date 2019.01.20 14:18
     */
    @SerializedName("student_id")
    public String student_id;
    @SerializedName("user_name")
    public String user_name;
    @SerializedName("image_url")
    public String image_url;
    @SerializedName("video_url")
    public String video_url;
    @SerializedName("createdAt")
    public String createdAt;

    public String getImage_url(){
        return image_url;
    }
    public void setImage_url(String s){
        image_url=s;

    }
    public String getVideo_url(){
        return video_url;
    }
    public String getStudent(){
        return student_id;
    }
    public String getUser(){
        return user_name;
    }


    @Override public String toString() {
        String[] str=createdAt.split("T");
         String time=str[0];
        return "From "+user_name +" "+time;
    }
}
