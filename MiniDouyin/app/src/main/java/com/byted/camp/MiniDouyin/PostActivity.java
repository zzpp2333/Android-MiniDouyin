package com.byted.camp.MiniDouyin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.byted.camp.MiniDouyin.beans.PostVideoResponse;
import com.byted.camp.MiniDouyin.newtork.IMiniDouyinService;
import com.byted.camp.MiniDouyin.newtork.RetrofitManager;
import com.byted.camp.MiniDouyin.utils.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.byted.camp.MiniDouyin.utils.Utils.*;

public class PostActivity extends AppCompatActivity {

    private Button postBtn;
    private SurfaceView mSurfaceView;
    private Camera mCamera;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;

    private Uri mCover;
    private Uri mVideo;

    private Boolean pictureTaken = false;
    private Boolean videoTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_post);
        mSurfaceView = findViewById(R.id.img);
        //todo 给SurfaceHolder添加Callback

        mCamera = getCamera(CAMERA_TYPE);
        final SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                //mCamera.setPreviewDisplay(surfaceHolder.getSurface());
                //mCamera.startPreview();
                try {
                    startPreview(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                releaseCameraAndPreview();
            }
        });

        findViewById(R.id.btn_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 拍一张照片
                if(null != mCamera){
                    mCamera.takePicture(null,null,mPicture);
                    pictureTaken = true;
                }
            }
        } );

        findViewById(R.id.btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    //todo 停止录制
                    isRecording = false;
                    videoTaken = true;
                    releaseMediaRecorder();
                } else {
                    //todo 录制
                    if (prepareVideoRecorder()) {
                        //mMediaRecorder.start();
                        isRecording = true;
                    }
                }
            }
        });

        findViewById(R.id.btn_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (mCamera.getParameters().isSmoothZoomSupported()) {
                            camera.cancelAutoFocus();
                        }
                    }
                });
            }
        });

        findViewById(R.id.btn_facing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 切换前后摄像头
                Camera.CameraInfo info = new Camera.CameraInfo();
                if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
                    releaseCameraAndPreview();
                    mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
//                mCamera.open();
                }else{
                    releaseCameraAndPreview();
                    mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
//                mCamera.open();
                }
                try {
                    startPreview(mSurfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } );

        postBtn = findViewById(R.id.btn_post);
        postBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //if(null == mCover || null == mVideo){
                if(!videoTaken || !pictureTaken){
                    postBtn.setText(R.string.missinghint);
                    Toast.makeText(PostActivity.this,R.string.missinghint,Toast.LENGTH_LONG);
                    //postBtn.setEnabled(true);
                }else{
                    postVideo();
                }
            }
        });
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等
        Camera cam = Camera.open(position);
        rotationDegree = getCameraDisplayOrientation(position);
        cam.setDisplayOrientation(rotationDegree);

        Camera.Parameters mParams = cam.getParameters();
        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        cam.setParameters(mParams);

        return cam;
    }

    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) throws IOException {
        //todo 开始预览
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();

        size = getOptimalPreviewSize(mCamera.getParameters().getSupportedPreviewSizes(),width,height);

        Camera.Parameters mParameters = mCamera.getParameters();
        mParameters.setPreviewSize(size.width,size.height);
        mCamera.setParameters(mParameters);
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
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

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show
        Retrofit retrofit = RetrofitManager.get("http://test.androidcamp.bytedance.com/");
        IMiniDouyinService postService = retrofit.create(IMiniDouyinService.class);
        final Call<PostVideoResponse> postCall = postService.createVideo("16061121","wxxxzhang",
                getMultipartFromUri("cover_image",mCover),getMultipartFromUri("video",mVideo));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        postCall.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                //PostVideoResponse postResponse = response.body();
                //Log.i("success",postResponse.getUrl());
                if(response.isSuccessful()){
                    Toast.makeText(PostActivity.this,R.string.success_try_refresh,Toast.LENGTH_LONG);
                    videoTaken = pictureTaken = false;
                }else {
                    Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_LONG);
                }
                postBtn.setEnabled(true);
                postBtn.setText(R.string.post);
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

    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        File videoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mVideo = Uri.fromFile(videoFile);

        mMediaRecorder.setOutputFile(videoFile.toString());
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);
        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        }catch (Exception e){
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        //todo 释放MediaRecorder
        try {
            //RuntimeException:stop failed
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            mMediaRecorder.stop();
        } catch (IllegalStateException e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }catch (RuntimeException e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }catch (Exception e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }
        //mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                return;
            }
            Log.i("pictureFile",pictureFile.getPath());
            mCover = Uri.fromFile(pictureFile);
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(bytes);
                fos.close();
            } catch (IOException e) {
                Log.d("mPicture", "Error accessing file: " + e.getMessage());
            }
            try {
                startPreview(mSurfaceView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
