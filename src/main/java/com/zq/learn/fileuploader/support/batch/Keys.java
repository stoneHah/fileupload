package com.zq.learn.fileuploader.support.batch;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
public class Keys {
    private static final String KEY_PREFIX = "youedata.fileupload.keys.";

    /**
     * 最大可读取的数量
     */
    public static final String READ_MAX = KEY_PREFIX + "read.max";

    /**
     * 写入失败数量
     */
    public static final String WRITE_ERROR_COUNT = KEY_PREFIX + "write.fail.count";
}
