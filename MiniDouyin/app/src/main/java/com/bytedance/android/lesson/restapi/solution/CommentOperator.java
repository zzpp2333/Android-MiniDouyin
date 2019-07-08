package com.bytedance.android.lesson.restapi.solution;


import com.bytedance.android.lesson.restapi.solution.bean.Comment;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public interface CommentOperator {

    void deleteNote(Comment comment);

    void updateNote(Comment comment);
}

