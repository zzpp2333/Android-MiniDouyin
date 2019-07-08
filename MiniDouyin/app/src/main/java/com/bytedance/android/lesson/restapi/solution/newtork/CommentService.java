package com.bytedance.android.lesson.restapi.solution.newtork;

import com.bytedance.android.lesson.restapi.solution.bean.CommentResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CommentService {
    @GET("/SearchComment")
    Call<CommentResponse> getComment(@Query("url") String url);

    @GET("/AddComment")
    Call<CommentResponse> addComment(@Query("username") String name,@Query("content") String content,@Query("time") String time,@Query("url") String url);
}
