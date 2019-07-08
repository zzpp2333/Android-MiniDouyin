package com.byted.camp.MiniDouyin.db;

import android.provider.BaseColumns;


/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    private TodoContract() {
    }

    public static class TodoEntry implements BaseColumns{

        public static final String TABLE_NAME="datalist";
        public static final String COLUMN_NAME_ID="ID";
        public static final String COLUMN_NAME_CONTENT="content";
        public static final String  COLUMN_NAME_DATE="date";
        public static final String  COLUMN_NAME_USER="name";
    };

    public static final String SQL_CREATE_ENTRIES=
            "CREATE TABLE "+ TodoEntry.TABLE_NAME+" ("+
                    TodoEntry.COLUMN_NAME_ID+" LONG PRIMARY KEY,"+
                    TodoEntry.COLUMN_NAME_CONTENT+" TEXT,"+
                    TodoEntry.COLUMN_NAME_DATE+" LONG,"+
                    TodoEntry.COLUMN_NAME_USER+" TEXT)";

}
