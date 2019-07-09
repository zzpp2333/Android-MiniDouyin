package com.bytedance.android.lesson.restapi.solution;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.android.lesson.restapi.solution.bean.Feed;
import com.bytedance.android.lesson.restapi.solution.bean.FeedResponse;
import com.bytedance.android.lesson.restapi.solution.bean.PostVideoResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.IMiniDouyinService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;
import com.bytedance.android.lesson.restapi.solution.utils.ResourceUtils;

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
import android.support.design.widget.FloatingActionButton;

public class Main2Activity extends AppCompatActivity {
    private static final int REQUEST_CODE_POST = 1002;

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;

    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private Button mBtnRefresh;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution2_c2);
        name=getIntent().getStringExtra("name");

        FloatingActionButton fab = findViewById(R.id.actionCapture);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Main2Activity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Main2Activity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Main2Activity.this,
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main2Activity.this,
                            new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},
                            1);
                }else{

                    /*startActivityForResult(
                            new Intent(MainActivity.this, PostActivity.class),
                            REQUEST_CODE_POST);*/
                    Intent it=new Intent(Main2Activity.this, CameraActivity.class);
                    it.putExtra("name",name);

                    startActivityForResult(
                           it,REQUEST_CODE_POST);
                }
            }
        });

        initRecyclerView();
        initBtns();
        fetchFeed();
    }

    private void initBtns() {
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Main2Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                            mBtn.setEnabled(false);
                            mBtn.setText("Posting...");

                            Log.i("MAINcover",mSelectedImage.toString());
                            Log.i("MAINvideo",mSelectedVideo.toString());
                            Intent postIntent = new Intent(Main2Activity.this,PostActivity.class);
                            postIntent.putExtra("cover",mSelectedImage.toString());
                            postIntent.putExtra("video",mSelectedVideo.toString());
                            postIntent.putExtra("name",name);

                            startActivity(postIntent);

                            mBtn.setEnabled(true);
                            mBtn.setText(R.string.success_try_refresh);
                            //postVideo();
                        } else {
                            throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                        }
                    } else if ((getString(R.string.success_try_refresh).equals(s))) {
                        mBtn.setText(R.string.select_an_image);
                    }

                }

            }
        });

        mBtnRefresh = findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchFeed();
            }
        });
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new MyViewHolder(imageView);
                //VideoView videoView = new VideoView(viewGroup.getContext());
                //videoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //return new Solution2C1Activity.MyViewHolder(videoView);
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
                //VideoView videoview = (VideoView) viewHolder.itemView;
                //ImageView iv=new ImageView(getApplicationContext());
                ImageView iv = (ImageView) viewHolder.itemView;

                // TODO-C2 (10) Uncomment these 2 lines, assign image url of Feed to this url variable
                String url = mFeeds.get(i).getImage_url();
                Glide.with(iv.getContext()).load(url).into(iv);
                //iv.setAdjustViewBounds(true);

                //String videourl=mFeeds.get(i).getVideo_url();
                //Glide.with(getApplicationContext())
                //.load(videourl)
                //.into(iv);
                //MediaController mediaController = new MediaController(getApplicationContext());
                //videoview.setMediaController(mediaController);
                //mediaController.setMediaPlayer(videoview);
                //videoview.setVideoPath(videourl);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // 点击事件
                        Toast.makeText(getApplicationContext(), "视频"+i,Toast.LENGTH_LONG).show();
                        //Intent it = new Intent(getApplicationContext(), PlayVideo.class);
                        Intent it = new Intent(getApplicationContext(), VideoActivity.class);
                        it.putExtra("data", mFeeds.get(i).getVideo_url());
                        it.putExtra("im",mFeeds.get(i).toString());
                        it.putExtra("name",name);
                        startActivity(it);
                    }
                });
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

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(Main2Activity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        Log.i("rrrrrr",f.getName());
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    public void fetchFeed() {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);

        // TODO-C2 (9) Send Request to fetch feed
        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received
        Retrofit retrofit= RetrofitManager.get("http://test.androidcamp.bytedance.com/");
        IMiniDouyinService getService =  retrofit.create(IMiniDouyinService.class);
        final Call<FeedResponse> feedCall = getService.getGet();

        feedCall.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                FeedResponse feedList = response.body();
                if(feedList.getSuc()){
                    mFeeds = feedList.getFeedlist();
                    mRv.getAdapter().notifyDataSetChanged();
                }
                resetRefreshBtn();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                resetRefreshBtn();
                t.printStackTrace();
                Log.i("onFailure","Failed");
            }
        });
    }

    private void resetRefreshBtn() {
        mBtnRefresh.setText(R.string.refresh_feed);
        mBtnRefresh.setEnabled(true);
    }
}

