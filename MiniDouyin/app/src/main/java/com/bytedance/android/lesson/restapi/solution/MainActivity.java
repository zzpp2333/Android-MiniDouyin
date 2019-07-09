package com.bytedance.android.lesson.restapi.solution;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bytedance.android.lesson.restapi.solution.bean.Comment;
import com.bytedance.android.lesson.restapi.solution.bean.CommentResponse;
import com.bytedance.android.lesson.restapi.solution.bean.User;
import com.bytedance.android.lesson.restapi.solution.newtork.CommentService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;
import com.bytedance.android.lesson.restapi.solution.newtork.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private EditText editName;
    private EditText editPwd;
    private Button login;
    private Button register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.login_name);
        editName.setFocusable(true);
        editName.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editName, 0);
        }

        editPwd = findViewById(R.id.login_password);
        editPwd.setFocusable(true);
        editPwd.requestFocus();
        InputMethodManager inputManager1 = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager1 != null) {
            inputManager1.showSoftInput(editPwd, 0);
        }

        login=findViewById(R.id.btn_login);
        register=findViewById(R.id.btn_go2register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence contentName = editName.getText();
                if (TextUtils.isEmpty(contentName)) {
                    Toast.makeText(MainActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                CharSequence contentPwd = editPwd.getText();
                if (TextUtils.isEmpty(contentPwd)) {
                    Toast.makeText(MainActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                String loginName=String.valueOf(contentName);
                String loginPwd=String.valueOf(contentPwd);
                loginUser(loginName,loginPwd);
                jump(loginName);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(final String loginName, String loginPwd){
        Retrofit retrofit= RetrofitManager.get("http://114.115.172.245:8008/");
        UserService getService =  retrofit.create(UserService.class);
        final Call<User> UserCall = getService.login(loginName,loginPwd);
        //boolean tag=false;

         UserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User  user = response.body();
                //jump(user.getName());
                if(user.getstatus()==1) {
                    //jump(user.getName());
                }else {
                    Log.i("tagtag","+++++++++++++++");
                    Log.i("tagtag",String.valueOf(user.getstatus()));
                    Log.i("tagtag",user.getName());
                    Log.i("tagtag",loginName);
                    Log.i("tagtag","+++++++++++++++");

                    Toast.makeText(MainActivity.this,
                            user.getmsg(), Toast.LENGTH_SHORT).show();
                    Intent it1=new Intent(MainActivity.this,RegisterActivity.class);
                    startActivity(it1);

                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Log.i("onFailure","Failed");
            }
        });

    }
    private void jump(String name){
        Log.i("caojv","jump");
        Intent it=new Intent(MainActivity.this,MainToActivity.class);
        it.putExtra("name",name);
        startActivity(it);
    }
}
