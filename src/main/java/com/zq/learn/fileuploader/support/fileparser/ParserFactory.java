package com.zq.learn.fileuploader.support.fileparser;

import org.springframework.util.StringUtils;

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
