package com.zq.learn.fileuploader.common.enums;

import org.apache.commons.lang.ArrayUtils;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/1
 **/
public enum  FileExtension {
    Csv("csv"),Excel_XLS("xls"),Excel_XLSX("xlsx");

    private String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public static FileExtension getBy(String extension) {
        FileExtension[] values = values();
        for (FileExtension value : values) {
            if(value.extension.equalsIgnoreCase(extension)){
                return value;
            }
        }
        return null;
    }
}
