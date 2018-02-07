package com.zq.learn.fileuploader.service;

import com.baomidou.mybatisplus.annotations.TableField;
import com.zq.learn.fileuploader.common.enums.JobStatus;
import com.zq.learn.fileuploader.controller.dto.Response;
import com.zq.learn.fileuploader.controller.dto.Response.ResponseBuilder;
import com.zq.learn.fileuploader.exception.FileImportException;
import com.zq.learn.fileuploader.persistence.model.FileImportInfo;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.batch.core.BatchStatus;

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
    GroupFileProcessResult getFilesProcessResult(String groupKey);

    public static class GroupFileProcessResult{
        public static final GroupFileProcessResult EMPTY = new GroupFileProcessResult();

        private BatchStatus status = BatchStatus.STARTING;
        private Long timeConsume = null;
        private Map<String,FileProcessResult> filesProcessResult;

        public BatchStatus getStatus() {
            return status;
        }

        public void setStatus(BatchStatus status) {
            this.status = status;
        }

        public Map<String, FileProcessResult> getFilesProcessResult() {
            return filesProcessResult;
        }

        public void setFilesProcessResult(Map<String, FileProcessResult> filesProcessResult) {
            this.filesProcessResult = filesProcessResult;
        }

        public Long getTimeConsume() {
            return timeConsume;
        }

        public void setTimeConsume(Long timeConsume) {
            this.timeConsume = timeConsume;
        }
    }

    /**
     * 文件处理 进程信息
     */
    public static class FileProcessResult{

        public static final FileProcessResult EMPTY = new FileProcessResult();

        private JobStatus status = JobStatus.Starting;

        private Integer readCount;
        private Integer writeCount;

        private boolean error = false;
        private String errorMsg;
        private Long timeConsume = null;

        public Integer getReadCount() {
            return readCount;
        }

        public void setReadCount(Integer readCount) {
            this.readCount = readCount;
        }

        public Integer getWriteCount() {
            return writeCount;
        }

        public void setWriteCount(Integer writeCount) {
            this.writeCount = writeCount;
        }

        public JobStatus getStatus() {
            return status;
        }

        public void setStatus(JobStatus status) {
            this.status = status;
        }

        public Long getTimeConsume() {
            return timeConsume;
        }

        public void setTimeConsume(Long timeConsume) {
            this.timeConsume = timeConsume;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
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
