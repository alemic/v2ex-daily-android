package me.yugy.v2ex.dao.dbinfo;

import android.provider.BaseColumns;

import me.yugy.v2ex.utils.database.SQLiteTable;

/**
 * Created by yugy on 14-3-7.
 */
public abstract class BaseNodesDBInfo implements BaseColumns {

    public static final String NODE_ID = "node_id";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String TITLE_ALTERNATIVE = "title_alternative";
    public static final String URL = "url";
    public static final String TOPICS = "topics";
    public static final String HEADER = "header";
    public static final String FOOTER = "footer";
    public static final String IS_COLLECTED = "is_collected";

}
