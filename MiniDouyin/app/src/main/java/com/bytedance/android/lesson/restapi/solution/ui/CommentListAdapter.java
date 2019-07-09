package com.bytedance.android.lesson.restapi.solution.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.android.lesson.restapi.solution.CommentOperator;
import com.bytedance.android.lesson.restapi.solution.R;
import com.bytedance.android.lesson.restapi.solution.bean.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private final CommentOperator operator;
    private final List<Comment> comments = new ArrayList<>();

    public CommentListAdapter(CommentOperator operator) {
        this.operator = operator;
    }

    public void refresh(List<Comment> newComments) {
        comments.clear();
        if (newComments != null) {
            comments.addAll(newComments);
            //Log.i("datalist",newComments.get(0).toString());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new CommentViewHolder(itemView, operator);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        commentViewHolder.bind(comments.get(i));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
