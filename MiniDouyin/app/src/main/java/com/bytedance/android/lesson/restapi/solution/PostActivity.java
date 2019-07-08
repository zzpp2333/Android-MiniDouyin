package com.bytedance.android.lesson.restapi.solution;


import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.bytedance.android.lesson.restapi.solution.bean.PostVideoResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.IMiniDouyinService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;
import com.bytedance.android.lesson.restapi.solution.utils.ResourceUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostActivity extends AppCompatActivity {

    private Button postBtn;

    private Uri mCover;
    private Uri mVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mCover = Uri.parse(getIntent().getStringExtra("cover"));
        mVideo = Uri.parse(getIntent().getStringExtra("video"));

        Log.i("coverAT:::",mCover.toString());
        Log.i("coverAT:::",mVideo.toString());

        VideoView preview = findViewById(R.id.preview);
        preview.setVideoURI(mVideo);
        preview.start();

        postBtn = findViewById(R.id.post);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postVideo();
                if (postBtn.getText().equals(R.string.success_try_refresh)) {
                    postBtn.setText(R.string.select_an_image);
                }
            }
        });
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(PostActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        postBtn.setText("POSTING...");
        postBtn.setEnabled(false);

        Log.i("atpost:::",mCover.getEncodedPath());
        Log.i("atpost::",mVideo.getEncodedPath());
        // if success, make a text Toast and show
        Retrofit retrofit = RetrofitManager.get("http://test.androidcamp.bytedance.com/");
        IMiniDouyinService postService = retrofit.create(IMiniDouyinService.class);
        final Call<PostVideoResponse> postCall = postService.createVideo("16061121","wxxxzhang",
                getMultipartFromUri("cover_image",mCover),getMultipartFromUri("video",mVideo));

        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }*/

        postCall.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                //PostVideoResponse postResponse = response.body();
                //Log.i("success",postResponse.getUrl());
                if(response.isSuccessful()){
                    postBtn.setText(R.string.success_try_refresh);
                    Toast.makeText(PostActivity.this,R.string.success_try_refresh,Toast.LENGTH_LONG);
                }else {
                    postBtn.setText(R.string.post);
                    Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_LONG);
                }
                postBtn.setEnabled(true);
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_LONG);
                postBtn.setEnabled(true);
                postBtn.setText(R.string.post);
            }
        });
    }

}
