package com.zq.learn.fileuploader.support.batch.listener;

import com.alibaba.fastjson.JSON;
import com.zq.learn.fileuploader.common.enums.JobStatus;
import com.zq.learn.fileuploader.persistence.dao.FileImportExecutionMapper;
import com.zq.learn.fileuploader.persistence.model.FileImportExecution;
import com.zq.learn.fileuploader.support.batch.Keys;
import com.zq.learn.fileuploader.support.batch.model.BatchExceptionInfo;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 文件导入step
 */
public class StepCompletionNotificationListener extends StepExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(StepCompletionNotificationListener.class);

	@Autowired
	private FileImportExecutionMapper fileImportExecutionMapper;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		String fileKey = stepExecution.getJobParameters().getString("fileKey");

		FileImportExecution execution = new FileImportExecutionBuilder()
				.fileKey(fileKey)
				.jobStartTime(stepExecution.getStartTime())
				.jobEndTime(stepExecution.getEndTime() == null ? new Date() : stepExecution.getEndTime())
				.readCount(stepExecution.getReadCount())
				.filterCount(stepExecution.getFilterCount())
				.skipCount(stepExecution.getSkipCount())
				.writeCount(getWriteCount(stepExecution))
				.failedMessage(getFailedMessage(stepExecution))
				.filteredMessage(getFilteredMessage(stepExecution))
				.status(JobStatus.parse(stepExecution.getStatus()).getCode())
				.build();

		fileImportExecutionMapper.insert(execution);
		return null;
	}

	private String getFilteredMessage(StepExecution stepExecution) {
		if(stepExecution.getFilterCount() <= 0){
			return null;
		}

		List<ParsedItem> list = (List<ParsedItem>) stepExecution.getExecutionContext().get(Keys.FILTER_RECORDS);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return JSON.toJSONString(list);
	}

	private String getFailedMessage(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution.getExecutionContext();

		if(executionContext.containsKey(BatchExceptionInfo.WRITE_BATCH_EXCEPTION)){
			BatchExceptionInfo batchExceptionInfo = (BatchExceptionInfo) executionContext.get(BatchExceptionInfo.WRITE_BATCH_EXCEPTION);
			if (!batchExceptionInfo.isEmpty()) {
				return batchExceptionInfo.getStackTraceInfo();
			}
		}
		return null;
	}

	private Integer getWriteCount(StepExecution stepExecution) {
		int errorCount = 0;
		ExecutionContext executionContext = stepExecution.getExecutionContext();
		if(executionContext.containsKey(BatchExceptionInfo.WRITE_BATCH_EXCEPTION)){
			BatchExceptionInfo batchExceptionInfo = (BatchExceptionInfo) executionContext.get(BatchExceptionInfo.WRITE_BATCH_EXCEPTION);
			if (!batchExceptionInfo.isEmpty()) {
				errorCount = batchExceptionInfo.getExceptionCount();
			}
		}

		return stepExecution.getWriteCount() - errorCount;
	}

	public static class FileImportExecutionBuilder {
		private String fileKey;
		/**
		 * 任务开始时间
		 */
		private Date jobStartTime;
		/**
		 * 任务结束时间
		 */
		private Date jobEndTime;
		private Integer readCount;
		private Integer writeCount;
		private Integer filterCount;
		private Integer skipCount;
		private String failedMessage;
		private String filteredMessage;
		private Integer status;

		public FileImportExecutionBuilder() {
		}

		public FileImportExecutionBuilder fileKey(String fileKey) {
			this.fileKey = fileKey;
			return this;
		}

		public FileImportExecutionBuilder jobStartTime(Date jobStartTime) {
			this.jobStartTime = jobStartTime;
			return this;
		}

		public FileImportExecutionBuilder jobEndTime(Date jobEndTime) {
			this.jobEndTime = jobEndTime;
			return this;
		}

		public FileImportExecutionBuilder readCount(Integer readCount) {
			this.readCount = readCount;
			return this;
		}

		public FileImportExecutionBuilder writeCount(Integer writeCount) {
			this.writeCount = writeCount;
			return this;
		}

		public FileImportExecutionBuilder filterCount(Integer filterCount) {
			this.filterCount = filterCount;
			return this;
		}

		public FileImportExecutionBuilder skipCount(Integer skipCount) {
			this.skipCount = skipCount;
			return this;
		}

		public FileImportExecutionBuilder failedMessage(String failedMessage) {
			this.failedMessage = failedMessage;
			return this;
		}

		public FileImportExecutionBuilder filteredMessage(String filteredMessage) {
			this.filteredMessage = filteredMessage;
			return this;
		}

		public FileImportExecutionBuilder status(Integer status) {
			this.status = status;
			return this;
		}

		public FileImportExecution build() {
			FileImportExecution fileImportExecution = new FileImportExecution();
			fileImportExecution.setFileKey(this.fileKey);
			fileImportExecution.setJobStartTime(this.jobStartTime);
			fileImportExecution.setJobEndTime(this.jobEndTime);
			fileImportExecution.setReadCount(this.readCount);
			fileImportExecution.setWriteCount(this.writeCount);
			fileImportExecution.setFilterCount(this.filterCount);
			fileImportExecution.setSkipCount(this.skipCount);
			fileImportExecution.setFailedMessage(this.failedMessage);
			fileImportExecution.setFilteredMessage(this.filteredMessage);
			fileImportExecution.setStatus(this.status);

			return fileImportExecution;
		}
	}
}
