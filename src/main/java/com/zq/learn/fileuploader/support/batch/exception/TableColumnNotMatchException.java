package com.zq.learn.fileuploader.support.batch.exception;

import java.sql.SQLException;

/**
 * 表列不匹配异常
 *
 * @author qun.zheng
 * @create 2018/2/4
 **/
public class TableColumnNotMatchException extends Exception {
    private String table;
    private String[] curColumns;
    private String[] toCreateColumns;

    public TableColumnNotMatchException(String table, String[] curColumns,String[] toCreateColumns) {
        super("表"+table+"列不匹配");

        this.table = table;
        this.curColumns = curColumns;
        this.toCreateColumns = toCreateColumns;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("表[").append(table).append("]")
                .append("当前的列数("+curColumns.length+")与导入数据的列数("+toCreateColumns.length+")不匹配");
        return builder.toString();
    }

}
