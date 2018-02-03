package com.zq.learn.fileuploader.support.batch.listener;

import com.zq.learn.fileuploader.persistence.dao.FileImportExecutionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 文件导入step
 */
@Component
public class StepCompletionNotificationListener extends StepExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(StepCompletionNotificationListener.class);

	@Autowired
	private FileImportExecutionMapper fileImportExecutionMapper;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return super.afterStep(stepExecution);
	}
}
