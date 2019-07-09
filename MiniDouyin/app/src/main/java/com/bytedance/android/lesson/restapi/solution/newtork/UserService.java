package com.bytedance.android.lesson.restapi.solution.newtork;

import com.bytedance.android.lesson.restapi.solution.bean.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET("/RegisterVideo")
    Call<User> register(@Query("account") String name,@Query("pwd") String pwd,@Query("email") String email,@Query("code") String code,@Query("flag") String flag);

    @GET("/LoginVideo")
    Call<User> login(@Query("account") String name,@Query("pwd") String pwd);


}
