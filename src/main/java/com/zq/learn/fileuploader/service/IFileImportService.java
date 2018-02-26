package com.zq.learn.fileuploader.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.zq.learn.fileuploader.common.enums.JobStatus;
import com.zq.learn.fileuploader.controller.dto.FileImportContext;
import com.zq.learn.fileuploader.exception.FileImportException;
import com.zq.learn.fileuploader.persistence.model.FileImportInfo;
import com.zq.learn.fileuploader.service.model.FileImportInfoSupport;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import org.springframework.batch.core.BatchStatus;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
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
     * @param fileImportContext
     * @return 文件Key
     */
    String importFile(String groupKey, String tableName, String fileName, InputStream inputStream, FileImportContext fileImportContext) throws FileImportException;

    /**
     * 获取文件处理结果信息
     * @param groupKey
     * @return
     */
    GroupFileProcessResult getFilesProcessResult(String groupKey);

    /**
     * 获取文件导入信息
     * @param fileName
     * @param startTime
     * @param endTime
     * @param page
     * @return
     */
    List<FileImportInfoSupport> getFileImportInfos(String fileName, Date startTime, Date endTime, Page page);

    public static class GroupFileProcessResult{
        public static final GroupFileProcessResult EMPTY = new GroupFileProcessResult();

        private JobStatus status = JobStatus.Starting;
        private Long timeConsume = null;
        private Map<String,FileProcessResult> filesProcessResult;

        public JobStatus getStatus() {
            return status;
        }

        public void setStatus(JobStatus status) {
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
        /**
         * 过滤记录数
         */
        private Integer filterCount;

        private boolean error = false;
        private List<String> errorMsgList;

        /**
         * 过滤记录
         */
        private List<ParsedItem> filterRecords;

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

        public Integer getFilterCount() {
            return filterCount;
        }

        public void setFilterCount(Integer filterCount) {
            this.filterCount = filterCount;
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

        public List<String> getErrorMsgList() {
            return errorMsgList;
        }

        public void setErrorMsgList(List<String> errorMsgList) {
            this.errorMsgList = errorMsgList;
        }

        public List<ParsedItem> getFilterRecords() {
            return filterRecords;
        }

        public void setFilterRecords(List<ParsedItem> filterRecords) {
            this.filterRecords = filterRecords;
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
