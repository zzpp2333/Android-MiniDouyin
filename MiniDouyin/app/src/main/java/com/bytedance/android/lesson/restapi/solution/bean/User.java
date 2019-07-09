package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("name")
    public String name;
    @SerializedName("status")
    public int status;
    @SerializedName("msg")
    public String msg;

    public String getName(){
        return this.name;
    }
    public int getstatus(){
        return this.status;
    }
    public String getmsg(){
        return this.msg;
    }

    @Override
    public String toString(){
        return name+" "+status+" "+msg;
    }
}
