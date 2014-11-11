package me.yugy.v2ex.dao.dbinfo;

import me.yugy.v2ex.dao.datahelper.AllNodesDataHelper;
import me.yugy.v2ex.utils.database.SQLiteTable;

import static me.yugy.v2ex.utils.database.Column.*;

/**
 * Created by yugy on 14-3-7.
 */
public class AllNodesDBInfo extends BaseNodesDBInfo {

    public static final SQLiteTable TABLE = new SQLiteTable(AllNodesDataHelper.TABLE_NAME)
            .addColumn(NODE_ID, DataType.INTEGER)
            .addColumn(NAME, DataType.TEXT)
            .addColumn(TITLE, DataType.TEXT)
            .addColumn(TITLE_ALTERNATIVE, DataType.TEXT)
            .addColumn(URL, DataType.TEXT)
            .addColumn(TOPICS, DataType.INTEGER)
            .addColumn(HEADER, DataType.TEXT)
            .addColumn(FOOTER, DataType.TEXT)
            .addColumn(IS_COLLECTED, DataType.INTEGER);

}
