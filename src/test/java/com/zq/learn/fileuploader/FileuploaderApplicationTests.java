package com.zq.learn.fileuploader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileuploaderApplicationTests {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	public JobRepositoryFactoryBean jobInstanceDao;

	@Autowired
	@Qualifier("importCsvToDbJob")
	private Job importCsvToDbJob;

	@Test
	public void contextLoads() {

	}

	@Test
	public void testJobInstanceDao(){
		Assert.assertNotNull(jobInstanceDao);
	}

	@Test
	public void runJob() throws Exception {
		jobLauncher.run(importCsvToDbJob, newExecution());
	}

	/*@Test
	public void runImportEmployeeFromCsvToDbJob() throws Exception {
		jobLauncher.run(importEmployeeFromCsvToDbJob, newExecution());
	}*/

	private JobParameters newExecution() {
		Map<String, JobParameter> parameters = new HashMap<>();

		File file = new File("D:\\Documents\\employee.csv");
		JobParameter parameter = new JobParameter("file:///" + file.getAbsolutePath());
		JobParameter tableParam = new JobParameter("employee");

		parameters.put("resource", parameter);
		parameters.put("tableName", tableParam);

		return new JobParameters(parameters);
	}

}
