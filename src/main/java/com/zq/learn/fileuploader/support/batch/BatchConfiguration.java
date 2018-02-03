package com.zq.learn.fileuploader.support.batch;

import com.alibaba.druid.util.DaemonThreadFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zq.learn.fileuploader.support.batch.listener.JobCompletionNotificationListener;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import com.zq.learn.fileuploader.support.batch.reader.ParsedItemReader;
import com.zq.learn.fileuploader.support.batch.reader.PoiItemStreamReader;
import com.zq.learn.fileuploader.support.batch.reader.RowMapper;
import com.zq.learn.fileuploader.support.batch.writer.CustomerJdbcBatchItemWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobRepository jobRepository;

    @Autowired
    public DataSource dataSource;

    @Bean
    public JobLauncher jobLauncher() {
        final SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor(new ThreadFactoryBuilder()
                .setNameFormat("job-task-executor-service")
                .setDaemon(true)
                .build());
        jobLauncher.setTaskExecutor(simpleAsyncTaskExecutor);
        return jobLauncher;
    }

    @Bean
    public JobFactory jobFactory(List<Job> jobs){
        return new JobFactory(jobs);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<ParsedItem> csvItemReader(@Value("#{jobParameters['resource']}") Resource resource) {
        return new ParsedItemReader(resource);
    }

    @Bean
    @StepScope
    public PoiItemStreamReader<ParsedItem> excelItemReader(@Value("#{jobParameters['resource']}") Resource resource) {
        PoiItemStreamReader<ParsedItem> reader = new PoiItemStreamReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(resource);
        reader.setRowMapper(row -> {
            ParsedItem item = new ParsedItem();

            int i = 0;
            for (Cell c : row) {
                item.put(ParsedItemReader.columns[i++], c.getStringCellValue());
            }

            return item;
        });

        return reader;
    }

    @Bean
    @StepScope
    public CustomerJdbcBatchItemWriter writer(@Value("#{jobParameters['tableName']}") String tableName) {
        JdbcBatchItemWriter delegate = new JdbcBatchItemWriter();

        CustomerJdbcBatchItemWriter writer = new CustomerJdbcBatchItemWriter(delegate,tableName);
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Job importCsvToDbJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get(JobNameFactory.JOB_CSV_TO_DB)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importCsvToDbStep())
                .end()
                .build();
    }

    @Bean
    public Step importCsvToDbStep() {
        return stepBuilderFactory.get(JobNameFactory.STEP_CSV_TO_DB)
                .<ParsedItem, ParsedItem> chunk(3000)
                .reader(csvItemReader(null))
//                .listener()
//                .processor(processor())
                .writer(writer(null))
                .build();
    }

    @Bean
    public Job importExcelToDbJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get(JobNameFactory.JOB_EXCEL_TO_DB)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importExcelToDbStep())
                .end()
                .build();
    }

    @Bean
    public Step importExcelToDbStep() {
        return stepBuilderFactory.get(JobNameFactory.STEP_EXCEL_TO_DB)
                .<ParsedItem, ParsedItem> chunk(3000)
                .reader(excelItemReader(null))
//                .processor(processor())
                .writer(writer(null))
                .faultTolerant()
                .skip(SQLException.class)
                .noRetry(SQLException.class)
                .build();
    }




}
