package com.zq.learn.fileuploader.service;

import com.baomidou.mybatisplus.annotations.TableField;
import com.zq.learn.fileuploader.controller.dto.Response;
import com.zq.learn.fileuploader.controller.dto.Response.ResponseBuilder;
import com.zq.learn.fileuploader.exception.FileImportException;
import com.zq.learn.fileuploader.persistence.model.FileImportInfo;
import org.apache.poi.ss.formula.functions.T;

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
    String importFile(String groupKey,String tableName, String fileName, InputStream inputStream) throws FileImportException;

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

    public static class FileImportInfoBuilder{
        private String fileName;
        private String filePath;

        private String tableName;
        private Long jobInstanceId;
        private String fileKey;
        private String uploadKey;

        public FileImportInfoBuilder() {
        }

        public FileImportInfoBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public FileImportInfoBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public FileImportInfoBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public FileImportInfoBuilder jobInstanceId(Long jobInstanceId) {
            this.jobInstanceId = jobInstanceId;
            return this;
        }

        public FileImportInfoBuilder fileKey(String fileKey) {
            this.fileKey = fileKey;
            return this;
        }

        public FileImportInfoBuilder uploadKey(String uploadKey) {
            this.uploadKey = uploadKey;
            return this;
        }

        public FileImportInfo build() {
            FileImportInfo fileImportInfo = new FileImportInfo();
            fileImportInfo.setFileName(fileName);
            fileImportInfo.setFilePath(filePath);
            fileImportInfo.setTableName(tableName);
            fileImportInfo.setJobInstanceId(jobInstanceId);
            fileImportInfo.setFileKey(fileKey);
            fileImportInfo.setUploadKey(uploadKey);

            return fileImportInfo;
        }
    }

}
