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

    /**
     * 身份证列
     */
    public static final String ID_CARD_COLUMNS = KEY_PREFIX + "idcard.columns";

    /**
     * 过滤记录数
     */
    public static final String FILTER_RECORDS = KEY_PREFIX + "filter.records";

}
