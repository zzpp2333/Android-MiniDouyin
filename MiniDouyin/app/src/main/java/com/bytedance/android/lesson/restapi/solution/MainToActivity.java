package com.bytedance.android.lesson.restapi.solution;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

public class MainToActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainto);
        Button btn=findViewById(R.id.btn);
        final String name=getIntent().getStringExtra("name");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainToActivity.this,Main2Activity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }
}
