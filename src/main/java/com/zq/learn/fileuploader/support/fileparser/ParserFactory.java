package com.zq.learn.fileuploader.support.fileparser;

import com.zq.learn.fileuploader.support.batch.StreamParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/27
 **/
@Deprecated
public class ParserFactory {

    public static Parser getParser(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (extension.equalsIgnoreCase("csv")) {
            return new CsvParser();
        }else if("xls".equalsIgnoreCase(extension) ||
                "xlsx".equalsIgnoreCase(extension)){
            return new ExcelParser();
        }

        return null;
    }
}
