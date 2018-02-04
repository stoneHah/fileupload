package com.zq.learn.fileuploader.support.batch.exception;

import java.sql.SQLException;

/**
 * 表列不匹配异常
 *
 * @author qun.zheng
 * @create 2018/2/4
 **/
public class TableColumnNotMatchException extends SQLException {
    private String table;
    private String[] columns;

    public TableColumnNotMatchException(Throwable cause, String table, String[] columns) {
        super(cause);
        this.table = table;
        this.columns = columns;
    }
}
