package com.zq.learn.fileuploader.support.batch.listener;

import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/4
 **/
@Component
public class DBWriterListener implements ItemWriteListener<ParsedItem> {

    @Override
    public void beforeWrite(List<? extends ParsedItem> items) {

    }

    @Override
    public void afterWrite(List<? extends ParsedItem> items) {

    }

    @Override
    public void onWriteError(Exception exception, List<? extends ParsedItem> items) {
        System.out.println("on write error");
    }
}
