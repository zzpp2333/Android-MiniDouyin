package com.bytedance.android.lesson.restapi.solution.bean;


import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:18
 */
public class Comment {

    // TODO-C2 (1) Implement your Feed Bean here according to the response json


    /**
     * @author Xavier.S
     * @date 2019.01.20 14:18
     */
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("url")
    public String url;
    @SerializedName("time")
    public String time;
    @SerializedName("content")
    public String content;

    public String geturl(){
        return url;
    }
    public void url(String s){
        url=s;

    }
    public String getDate(){
        return this.time;
    }
    public String getContent(){
        return this.content;
    }

    public int getid(){
        return id;
    }
    public String getUser(){
        return name;
    }


    @Override public String toString() {
        return id+" "+name+" "+time+" "+content+" "+url;
    }


}
