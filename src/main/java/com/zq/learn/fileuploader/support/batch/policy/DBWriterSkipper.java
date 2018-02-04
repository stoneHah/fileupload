package com.zq.learn.fileuploader.support.batch.policy;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

import java.sql.SQLException;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/4
 **/
public class DBWriterSkipper implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        if (t instanceof SQLException) {
            System.out.println("skipcount:" + skipCount);
            return true;
        }
        return false;
    }
}
