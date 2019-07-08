package com.bytedance.android.lesson.restapi.solution;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private Boolean isplay=false;
    private TextView tv;
    private Boolean islove=false;
    private ImageView love;
    private ImageView comment;
    private String im;
    private String data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        setTitle("VideoView");

        videoView = findViewById(R.id.videoView);
        //videoView.setVideoPath(getVideoPath(R.raw.yuminhong));
        data = getIntent().getStringExtra("data");
        im=getIntent().getStringExtra("im");
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

        tv=findViewById(R.id.information);
        tv.setText(im);

        love=findViewById(R.id.love);
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(islove==false) {
                    love.setImageResource(R.drawable.red);
                    islove=true;
                }
                else{
                    love.setImageResource(R.drawable.gray);
                    islove=true;
                }

            }
        });

        comment=findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),CommentNet.class);
                //intent.putExtra("vname",im);
                intent.putExtra("url",data);
                startActivity(intent);
            }
        });
    }

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
}
