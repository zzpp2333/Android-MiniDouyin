package com.byted.camp.MiniDouyin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private Boolean isplay=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        setTitle("VideoView");



        videoView = findViewById(R.id.videoView);
        //videoView.setVideoPath(getVideoPath(R.raw.yuminhong));
        String data = getIntent().getStringExtra("data");
        videoView.setVideoPath(data);
        videoView.pause();
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isplay==true) {
                    videoView.pause();
                    isplay=false;
                }
                else{
                    videoView.start();
                    isplay=true;
                }
            }
        });
    }

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
}
