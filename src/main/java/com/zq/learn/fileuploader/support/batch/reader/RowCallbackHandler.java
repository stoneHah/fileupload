package com.zq.learn.fileuploader.support.batch.reader;

import org.apache.poi.ss.usermodel.Row;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
public interface RowCallbackHandler {

    void handleRow(Row row) throws RuntimeException;
}
