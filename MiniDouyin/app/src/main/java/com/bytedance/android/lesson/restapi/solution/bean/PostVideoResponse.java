package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import retrofit2.http.POST;

/**
 * @author Xavier.S
 * @date 2019.01.18 17:53
 */
public class PostVideoResponse {

    // TODO-C2 (3) Implement your PostVideoResponse Bean here according to the response json

    //    "result": {},
    //    "url": "https://lf1-hscdn-tos.pstatp
    //    .com/obj/developer-baas/baas/tt7217xbo2wz3cem41/a8efa55c5c22de69_1560563154288.mp4",
    //    "success": true
    // TODO-C2 (3) Implement your PostVideoResponse Bean here according to the response json
    @SerializedName("result")
    private JsonObject result;
    @SerializedName("url")
    private String url;
    @SerializedName("success")
    private boolean Suc;

    public String getUrl(){
        return url;
    }


    @Override public String toString() {
        return "Value{" +
                "url='" + url +
                ", success='" + Suc +
                '}';
    }


}
