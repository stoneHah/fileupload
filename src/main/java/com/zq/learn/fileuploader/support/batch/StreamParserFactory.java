package com.zq.learn.fileuploader.support.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/27
 **/
@Component
public class StreamParserFactory {

    @Autowired
    private List<StreamParser> parserList;

    public StreamParser getStreamParser(Object source){
        for (StreamParser streamParser : parserList) {
            if (streamParser.match(source)) {
                return streamParser;
            }
        }

        return null;
    }
}
