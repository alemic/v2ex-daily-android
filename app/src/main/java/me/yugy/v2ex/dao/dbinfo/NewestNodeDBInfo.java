package me.yugy.v2ex.dao.dbinfo;

import me.yugy.v2ex.dao.datahelper.NewestNodeDataHelper;
import me.yugy.v2ex.utils.database.Column;
import me.yugy.v2ex.utils.database.SQLiteTable;

/**
 * Created by yugy on 14-3-14.
 */
public class NewestNodeDBInfo extends BaseTopicsDBInfo{

    public static final SQLiteTable TABLE = new SQLiteTable(NewestNodeDataHelper.TABLE_NAME)
            .addColumn(TOPIC_ID, Column.DataType.INTEGER)
            .addColumn(TITLE, Column.DataType.TEXT)
            .addColumn(URL, Column.DataType.TEXT)
            .addColumn(CONTENT, Column.DataType.TEXT)
            .addColumn(CONTENT_RENDERED, Column.DataType.TEXT)
            .addColumn(REPLIES, Column.DataType.INTEGER)
            .addColumn(MEMBER_ID, Column.DataType.INTEGER)
            .addColumn(MEMBER_USERNAME, Column.DataType.TEXT)
            .addColumn(MEMBER_TAGLINE, Column.DataType.TEXT)
            .addColumn(MEMBER_AVATAR, Column.DataType.TEXT)
            .addColumn(NODE_ID, Column.DataType.INTEGER)
            .addColumn(CREATED, Column.DataType.INTEGER)
            .addColumn(LAST_MODIFIED, Column.DataType.INTEGER)
            .addColumn(LAST_TOUCHED, Column.DataType.INTEGER);
}
