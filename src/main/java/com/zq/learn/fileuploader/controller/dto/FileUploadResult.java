package com.zq.learn.fileuploader.controller.dto;

import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/3
 **/
public class FileUploadResult {
    private Map<String, String> normalFiles;
    private Map<String, String> exceptionFiles;

    private String group;

    public Map<String, String> getNormalFiles() {
        return normalFiles;
    }

    public void setNormalFiles(Map<String, String> normalFiles) {
        this.normalFiles = normalFiles;
    }

    public Map<String, String> getExceptionFiles() {
        return exceptionFiles;
    }

    public void setExceptionFiles(Map<String, String> exceptionFiles) {
        this.exceptionFiles = exceptionFiles;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
