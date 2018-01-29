package com.zq.learn.fileuploader.support.fileparser;

import org.apache.poi.ss.formula.functions.T;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/27
 **/
@Deprecated
public interface ItemProcessor<T> {
    void process(T item);
}
