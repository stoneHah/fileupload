package com.zq.learn.fileuploader.support.batch.reader;

import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Executable;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
public interface RowMapper<T> {

    T mapRow(Row row) throws Exception;
}
