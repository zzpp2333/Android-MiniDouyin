package com.bytedance.android.lesson.restapi.solution;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bytedance.android.lesson.restapi.solution.bean.User;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;
import com.bytedance.android.lesson.restapi.solution.newtork.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPwd;
    private EditText editCode;
    private EditText editName;
    private Button send;
    private Button register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.reg_name);
        editName.setFocusable(true);
        editName.requestFocus();
        InputMethodManager inputManager1 = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager1 != null) {
            inputManager1.showSoftInput(editName, 0);
        }

        editPwd = findViewById(R.id.reg_password);
        editPwd.setFocusable(true);
        editPwd.requestFocus();
        InputMethodManager inputManager2= (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager2 != null) {
            inputManager2.showSoftInput(editPwd, 0);
        }

        editEmail = findViewById(R.id.email);
        editEmail.setFocusable(true);
        editEmail.requestFocus();
        InputMethodManager inputManager3 = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager3 != null) {
            inputManager3.showSoftInput(editName, 0);
        }

        editCode = findViewById(R.id.code);
        editCode.setFocusable(true);
        editCode.requestFocus();
        InputMethodManager inputManager4 = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager4 != null) {
            inputManager4.showSoftInput(editPwd, 0);
        }


        send=findViewById(R.id.btn_receive);
        register=findViewById(R.id.btn_Confirm);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence contentName = editName.getText();
                CharSequence contentEmail=editEmail.getText();
                CharSequence contentPwd=editPwd.getText();

                if (TextUtils.isEmpty(contentName)) {
                    Toast.makeText(RegisterActivity.this,
                            "No Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contentName)) {
                    Toast.makeText(RegisterActivity.this,
                            "No Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contentEmail)) {
                    Toast.makeText(RegisterActivity.this,
                            "No email", Toast.LENGTH_SHORT).show();
                    return;
                }


                String registerName=String.valueOf(contentName);
                String registerPwd=String.valueOf(contentPwd);
                String registerEmail=String.valueOf(contentEmail);
                String registerCode;
                CharSequence contentCode=editCode.getText();
                registerCode=String.valueOf(contentCode);

                SendVerifyCode(registerCode,registerName,registerEmail);


                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence contentName = editName.getText();
                CharSequence contentEmail=editEmail.getText();
                CharSequence contentPwd=editPwd.getText();
                CharSequence contentCode=editCode.getText();
                if (TextUtils.isEmpty(contentName)) {
                    Toast.makeText(RegisterActivity.this,
                            "No Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contentName)) {
                    Toast.makeText(RegisterActivity.this,
                            "No Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contentEmail)) {
                    Toast.makeText(RegisterActivity.this,
                            "No email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contentCode)) {
                    Toast.makeText(RegisterActivity.this,
                            "No code", Toast.LENGTH_SHORT).show();
                    return;
                }

                String registerName=String.valueOf(contentName);
                String registerPwd=String.valueOf(contentPwd);
                String registerEmail=String.valueOf(contentEmail);
                String registerCode=String.valueOf(contentCode);

                registerUser(registerName,registerPwd,registerEmail,registerCode);

            }
        });
    }


    private void SendVerifyCode(String registerName,String registerPwd,String registerEmail){
        Retrofit retrofit= RetrofitManager.get("http://114.115.172.245:8008/");
        UserService getService =  retrofit.create(UserService.class);
        final Call<User> UserCall = getService.register(registerName,registerPwd,registerEmail,"00","0");

        UserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User  user = response.body();
                Log.i("aaa",String.valueOf(response.body()));
                if(user.getstatus()==1) {
                    Toast.makeText(RegisterActivity.this,
                            user.getmsg(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RegisterActivity.this,
                            user.getmsg(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Log.i("onFailure","Failed");
            }
        });
    }

    private void registerUser(String registerName,String registerPwd,String registerEmail,String registerCode){
        Retrofit retrofit= RetrofitManager.get("http://114.115.172.245:8008/");
        UserService getService =  retrofit.create(UserService.class);
        final Call<User> UserCall = getService.register(registerCode,registerName,registerEmail,registerPwd,"1");

        UserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User  user = response.body();
                if(user.getstatus()==1) {
                    Toast.makeText(RegisterActivity.this,
                            user.getmsg(), Toast.LENGTH_SHORT).show();
                    Intent it=new Intent(RegisterActivity.this,MainActivity.class);
                    //it.putExtra("name",user.getName());
                    startActivity(it);
                }else {
                    Toast.makeText(RegisterActivity.this,
                            user.getmsg(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Log.i("onFailure","Failed");
            }
        });
    }
}
