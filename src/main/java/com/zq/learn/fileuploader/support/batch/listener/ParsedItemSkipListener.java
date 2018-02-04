package com.zq.learn.fileuploader.support.batch.listener;

import org.springframework.batch.core.listener.SkipListenerSupport;
import org.springframework.stereotype.Component;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/4
 **/
@Component
public class ParsedItemSkipListener extends SkipListenerSupport {
    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        System.out.println("items skiped");
    }
}
