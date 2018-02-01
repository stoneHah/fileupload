package com.zq.learn.fileuploader.common.enums;

import org.apache.commons.lang.ArrayUtils;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/1
 **/
public enum  FileExtension {
    CSV("csv"),EXCEL("xls","xlsx");

    private String[] extensions;

    FileExtension(String... extensions) {
        this.extensions = extensions;
    }

    public static FileExtension getBy(String extension) {
        FileExtension[] values = values();
        for (FileExtension value : values) {
            String[] extensions = value.extensions;
            for (String s : extensions) {
                if (s.equalsIgnoreCase(extension)) {
                    return value;
                }
            }
        }
        return null;
    }
}
