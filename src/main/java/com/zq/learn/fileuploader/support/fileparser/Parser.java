package com.zq.learn.fileuploader.support.fileparser;

import java.io.InputStream;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/27
 **/
@Deprecated
public interface Parser {
    void read(InputStream in, ItemProcessor<String[]> itemProcessor);
}
