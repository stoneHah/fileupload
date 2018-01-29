package com.zq.learn.fileuploader.support.batch.parser;

import com.zq.learn.fileuploader.support.batch.exception.ParseException;
import org.apache.poi.ss.formula.functions.T;

import java.io.InputStream;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/27
 **/
public interface StreamParser<T> {
    ParseResult<T> parse(InputStream inputStream) throws ParseException;

    boolean match(Object source);

    public static class ParseResult<T>{
        private T result;

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }
    }
}
