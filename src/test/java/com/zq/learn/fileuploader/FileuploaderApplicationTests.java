package com.zq.learn.fileuploader;

import com.zq.learn.fileuploader.support.batch.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileuploaderApplicationTests {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("importCsvToDbJob")
	private Job importCsvToDbJob;

	@Autowired
	@Qualifier("importExcelToDbJob")
	private Job importExcelToDbJob;

	@Autowired
	private JobRepository jobRepository;

	private ScheduledThreadPoolExecutor scheduler;

	@Before
	public void setup() {
		scheduler = new ScheduledThreadPoolExecutor(8);
	}

	@Test
	public void contextLoads() {

	}

	@Test
	public void runJob() throws Exception {
		jobLauncher.run(importCsvToDbJob, newExecution("D:\\Documents\\employee.csv","employee"));
	}

	@Test
	public void runImportExcelToDbJob() throws Exception {
		CountDownLatch doneSignal = new CountDownLatch(1);
		JobExecution jobExecution = jobLauncher.run(importExcelToDbJob, newExecution("D:\\Documents\\employee.xlsx", "employee"));

		scheduler.scheduleAtFixedRate(new Runnable() {
			private StepExecution stepExecution;
			@Override
			public void run() {
				Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
				if (stepExecutions.isEmpty()) {
					System.out.println("job还未启动");
					return;
				}

				if (jobExecution.getEndTime() != null) {
					doneSignal.countDown();
					return;
				}

				if (stepExecution == null) {
					stepExecution = stepExecutions.iterator().next();
				}
				ExecutionContext executionContext = stepExecution.getExecutionContext();
				if(executionContext.containsKey(Keys.READ_MAX)){
					int maxItems = executionContext.getInt(Keys.READ_MAX);
					System.out.println("当前读取的数量:" + stepExecution.getReadCount() + "最大可读取的数量:" + maxItems);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);

		doneSignal.await();
	}

	/*@Test
	public void runImportEmployeeFromCsvToDbJob() throws Exception {
		jobLauncher.run(importEmployeeFromCsvToDbJob, newExecution());
	}*/

	private JobParameters newExecution(String fileName,String tableName) {
		Map<String, JobParameter> parameters = new HashMap<>();

		File file = new File(fileName);
		JobParameter parameter = new JobParameter("file:///" + file.getAbsolutePath());
		JobParameter tableParam = new JobParameter(tableName);
		JobParameter dateParam = new JobParameter(new Date());

		parameters.put("resource", parameter);
		parameters.put("tableName", tableParam);
		parameters.put("date", dateParam);

		return new JobParameters(parameters);
	}

}
