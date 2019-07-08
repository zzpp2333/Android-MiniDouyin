package com.byted.camp.MiniDouyin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.byted.camp.MiniDouyin.beans.*;
import com.byted.camp.MiniDouyin.newtork.IMiniDouyinService;
import com.byted.camp.MiniDouyin.newtork.RetrofitManager;
import com.byted.camp.MiniDouyin.utils.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_POST = 1002;

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;

    private RecyclerView recyclerView;
    private Button refreshBtn;
    private Button mBtn;

    private Uri mSelectedImage;
    private Uri mSelectedVideo;
    private List<Feed> mFeeds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.actionCapture);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else{
                    startActivityForResult(
                            new Intent(MainActivity.this, CameraActivity.class),REQUEST_CODE_POST);
                }
            }
        });

        initRecyclerView();
        initBtn();
    }
    private void initBtn(){
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else{
                    String s = mBtn.getText().toString();
                    if (getString(R.string.select_an_image).equals(s)) {
                        chooseImage();
                    } else if (getString(R.string.select_a_video).equals(s)) {
                        chooseVideo();
                    } else if (getString(R.string.post_it).equals(s)) {
                        if (mSelectedVideo != null && mSelectedImage != null) {
                            mBtn.setEnabled(false);
                            mBtn.setText(R.string.posting);

                            Log.i("MAINcover",mSelectedImage.toString());
                            Log.i("MAINvideo",mSelectedVideo.toString());
                            Intent postIntent = new Intent(MainActivity.this,PostActivity.class);
                            postIntent.putExtra("cover",mSelectedImage.toString());
                            postIntent.putExtra("video",mSelectedVideo.toString());
                            startActivity(postIntent);

                            mBtn.setEnabled(true);
                            mBtn.setText(R.string.success_try_refresh);
                        } else {
                            throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                        }
                    } else if ((getString(R.string.success_try_refresh).equals(s))) {
                        mBtn.setText(R.string.select_an_image);
                    }

                }

            }
        });

        refreshBtn = findViewById(R.id.btn_refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchFeed(view);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.videorv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
                ImageView iv = (ImageView) viewHolder.itemView;

                iv.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // 点击事件
                        Toast.makeText(getApplicationContext(), "视频"+i,Toast.LENGTH_LONG).show();
                        //Intent it = new Intent(getApplicationContext(), PlayVideo.class);
                        Intent it = new Intent(getApplicationContext(), VideoActivity.class);
                        it.putExtra("data", mFeeds.get(i).getVideoUrl());
                        it.putExtra("im",mFeeds.get(i).toString());
                        startActivity(it);
                    }
                });
                // TODO-C2 (10) Uncomment these 2 lines, assign image url of Feed to this url variable
                String url = mFeeds.get(i).getImageUrl();
                Glide.with(iv.getContext()).load(url).into(iv);
            }

            @Override public int getItemCount() {
                return mFeeds.size();
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void chooseImage() {
        // TODO-C2 (4) Start Activity to select an image
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);
    }


    public void chooseVideo() {
        // TODO-C2 (5) Start Activity to select a video
        Intent intent=new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("TAG", "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d("TAG", "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d("TAG", "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
        }
    }

    public void fetchFeed(View view) {

        // TODO-C2 (9) Send Request to fetch feed
        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received
        Retrofit retrofit= RetrofitManager.get("http://test.androidcamp.bytedance.com/");
        IMiniDouyinService getService =  retrofit.create(IMiniDouyinService.class);
        final Call<FeedResponse> feedCall = getService.fetchResource();

        feedCall.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                FeedResponse feedList = response.body();
                if(feedList.getSuccess()){
                    mFeeds = feedList.getFeed();
                    //recyclerView.getAdapter().notifyDataSetChanged();
                }
                resetRefreshRv();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                resetRefreshRv();
                t.printStackTrace();
                Log.i("onFailure","Failed");
            }
        });
    }

    private void resetRefreshRv() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
