package com.byted.camp.MiniDouyin;


import com.byted.camp.MiniDouyin.beans.Note;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public interface NoteOperator {

    void deleteNote(Note note);

    void updateNote(Note note);
}

