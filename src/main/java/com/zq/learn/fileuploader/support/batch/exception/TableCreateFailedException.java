package com.zq.learn.fileuploader.support.batch.exception;

/**
 * 表创建失败的异常
 *
 * @author qun.zheng
 * @create 2018/2/4
 **/
public class TableCreateFailedException extends Exception {
    private String table;

    public TableCreateFailedException(String table) {
        super("表" + table + "创建失败");
        this.table = table;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("表[").append(table).append("]")
                .append("创建失败");
        return builder.toString();
    }

}
