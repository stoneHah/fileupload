package com.zq.learn.fileuploader.support.batch;

import com.zq.learn.fileuploader.support.batch.exception.ParseException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * 压缩文件流解析器
 *
 * @author qun.zheng
 * @create 2018/1/27
 **/
@Component
public class CompressionFileStreamParser implements StreamParser<T> {
    @Override
    public ParseResult<T> parse(InputStream inputStream) throws ParseException {
        return null;
    }

    /**
     * 暂只支持zip的压缩文件
     * @param source
     * @return
     */
    @Override
    public boolean match(Object source) {
        String fileName = String.valueOf(source);
        String extension = StringUtils.getFilenameExtension(fileName);
        return "zip".equalsIgnoreCase(extension);
    }
}
