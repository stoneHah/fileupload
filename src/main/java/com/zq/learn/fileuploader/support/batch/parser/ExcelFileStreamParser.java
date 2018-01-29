package com.zq.learn.fileuploader.support.batch.parser;

import com.zq.learn.fileuploader.support.batch.exception.ParseException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * excel文件流解析
 *
 * @author qun.zheng
 * @create 2018/1/27
 **/
@Component
public class ExcelFileStreamParser implements StreamParser<T>{
    @Override
    public ParseResult<T> parse(InputStream inputStream) throws ParseException {
        return null;
    }

    @Override
    public boolean match(Object source) {
        String fileName = String.valueOf(source);
        String extension = StringUtils.getFilenameExtension(fileName);
        return "xls".equalsIgnoreCase(extension) ||
                "xlsx".equalsIgnoreCase(extension);
    }
}
