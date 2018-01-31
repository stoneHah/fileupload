package com.zq.learn.fileuploader.service;

import java.io.InputStream;
import java.util.Map;

/**
 * 文件导入业务类
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
public interface IFileImportService {

    /**
     *
     * @param groupKey
     * @param tableName
     * @param fileName
     * @param inputStream
     * @return 文件Key
     */
    String importFile(String groupKey,String tableName, String fileName, InputStream inputStream);

    /**
     * 获取文件处理结果信息
     * @param groupKey
     * @return
     */
    Map<String,FileProcessResult> getFilesProcessResult(String groupKey);

    /**
     * 文件处理 进程信息
     */
    public static class FileProcessResult{

        public static enum Status{
            PREPARE,STARTED,COMPLETED, FAILED,
        }

        private Status status = Status.PREPARE;

        private Integer readCount;
        private Integer totalCount;
        private Integer skipCount;

        public Integer getReadCount() {
            return readCount;
        }

        public void setReadCount(Integer readCount) {
            this.readCount = readCount;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getSkipCount() {
            return skipCount;
        }

        public void setSkipCount(Integer skipCount) {
            this.skipCount = skipCount;
        }
    }

}
