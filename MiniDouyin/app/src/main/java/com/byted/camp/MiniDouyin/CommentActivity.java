package com.byted.camp.MiniDouyin;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.MiniDouyin.beans.Note;
import com.byted.camp.MiniDouyin.db.TodoContract;
import com.byted.camp.MiniDouyin.db.TodoDbHelper;
import com.byted.camp.MiniDouyin.ui.NoteListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;

public class CommentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        final String name=getIntent().getStringExtra("name");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CommentActivity.this, NoteActivity.class);
                intent.putExtra("name",name);
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                CommentActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                //CommentActivity.this.updateNode(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });
        recyclerView.setAdapter(notesAdapter);
        Log.i("msg","运行adapter");
        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans

        Log.i("msg","运行load");
        TodoDbHelper mDbHelper=new TodoDbHelper(getApplicationContext());

        SQLiteDatabase db= mDbHelper.getReadableDatabase();

        if(db==null){
            Log.i("msg","运行null");
            return Collections.emptyList();
        }

        List<Note> notelist=new LinkedList<>();
        Cursor cursor=null;
        try{
            cursor=db.query(TodoContract.TodoEntry.TABLE_NAME,
                    new String[]{TodoContract.TodoEntry.COLUMN_NAME_CONTENT,TodoContract.TodoEntry.COLUMN_NAME_DATE,TodoContract.TodoEntry.COLUMN_NAME_USER, TodoContract.TodoEntry.COLUMN_NAME_ID},
                    null,null,null,null,TodoContract.TodoEntry.COLUMN_NAME_DATE+" DESC");
            //cursor.moveToFirst();
            while (cursor.moveToNext()){
                Log.i("msg","move");
                String content=cursor.getString(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_NAME_CONTENT));
                long dateMs=cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_DATE));
                String name=cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_USER));
                long id=cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_ID));

                Log.i("msg",content);
                Note note=new Note(id);
                note.setContent(content);
                note.setDate(new Date(dateMs));
                note.setUserName(name);
                notelist.add(note);

            }

        }finally{
            if(cursor!=null){
                cursor.close();
            }
        }
        return notelist;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        TodoDbHelper mDbHelper=new TodoDbHelper(getApplicationContext());

        SQLiteDatabase db= mDbHelper.getReadableDatabase();
        String selection= TodoContract.TodoEntry.COLUMN_NAME_CONTENT+" like ?";
        String[] selectionArgs={note.getContent()};
        int deleteRows=db.delete(TodoContract.TodoEntry.TABLE_NAME,selection,selectionArgs);
    }
/*
    private void updateNode(Note note) {
        // 更新数据
        TodoDbHelper mDbHelper=new TodoDbHelper(getApplicationContext());

        SQLiteDatabase db= mDbHelper.getWritableDatabase();
        String title=String.valueOf(note.getState().intValue);
        ContentValues values=new ContentValues();
        values.put(TodoContract.TodoEntry.COLUMN_NAME_STATE,title);

        String selection= TodoContract.TodoEntry.COLUMN_NAME_ID+" LIKE ?";
        String[] selectionArgs={String.valueOf(note.id)};

        int count=db.update(
                TodoContract.TodoEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

    }
*/

}
