package com.bytedance.android.lesson.restapi.solution;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bytedance.android.lesson.restapi.solution.bean.CommentResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.CommentService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewNoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    //private static int count=1;
    private String url;
    private boolean tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);
        url=getIntent().getStringExtra("url");

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NewNoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = postComments(String.valueOf(content),url);
                if (succeed) {
                    Toast.makeText(NewNoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NewNoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean postComments(String content, String url) {
        Retrofit retrofit = RetrofitManager.get("http://114.115.172.245:8008/");
        CommentService Cservice = retrofit.create(CommentService.class);
        tag=true;

        //Date date=new Date(System.currentTimeMillis());
        //String str=String.valueOf(date);

        final Call<CommentResponse> postCall = Cservice.addComment("kongfeng", content, String.valueOf(System.currentTimeMillis()), url);
        //count=count+1;

        postCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if (response.isSuccessful()) {
                    Log.i("success", commentResponse.getMsg());
                    Toast.makeText(NewNoteActivity.this, R.string.success_try_refresh, Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(NewNoteActivity.this, "Failed!", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                tag=false;
                t.printStackTrace();
                Toast.makeText(NewNoteActivity.this, "Failed!", Toast.LENGTH_LONG);
            }
        });
        return tag;
    }
}

