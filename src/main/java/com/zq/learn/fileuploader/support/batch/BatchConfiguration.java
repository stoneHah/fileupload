package com.zq.learn.fileuploader.support.batch;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zq.learn.fileuploader.support.batch.listener.DBWriterListener;
import com.zq.learn.fileuploader.support.batch.listener.JobCompletionNotificationListener;
import com.zq.learn.fileuploader.support.batch.listener.StepCompletionNotificationListener;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import com.zq.learn.fileuploader.support.batch.policy.DBWriterSkipper;
import com.zq.learn.fileuploader.support.batch.processor.ParsedItemProcessor;
import com.zq.learn.fileuploader.support.batch.reader.CSVItemReader;
import com.zq.learn.fileuploader.support.batch.reader.ExcelEventItemReader;
import com.zq.learn.fileuploader.support.batch.reader.ExcelEventItemReader.RowMapper;
import com.zq.learn.fileuploader.support.batch.reader.ParsedItemReader;
import com.zq.learn.fileuploader.support.batch.reader.PoiItemStreamReader;
import com.zq.learn.fileuploader.support.batch.writer.CustomerJdbcBatchItemWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.jsr.item.ItemReaderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.sql.SQLException;
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
    public CSVItemReader csvItemReader(@Value("#{jobParameters['resource']}") Resource resource) {
        return new CSVItemReader(resource);
    }

    @Bean
    @StepScope
    public PoiItemStreamReader<ParsedItem> excelItemStreamReader(@Value("#{jobParameters['resource']}") Resource resource) {
        PoiItemStreamReader<ParsedItem> reader = new PoiItemStreamReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(resource);
        reader.setRowMapper(row -> {
            ParsedItem item = new ParsedItem();

            int i = 0;
            for (Cell c : row) {
                item.put(ParsedItemReader.columns[i], c.getStringCellValue());

                i++;
            }

            return item;
        });

        return reader;
    }

    @Bean
    @StepScope
    public ExcelEventItemReader<ParsedItem> excelItemReader(@Value("#{jobParameters['resource']}") Resource resource) {
        ExcelEventItemReader excelEventItemReader = new ExcelEventItemReader();
        excelEventItemReader.setResource(resource);
        excelEventItemReader.setRowMapper(new RowMapper() {
            @Override
            public ParsedItem mapRow(String[] row) throws Exception {
                ParsedItem item = new ParsedItem();

                for (int i = 0;i < row.length;i++) {
                    item.put(ParsedItemReader.columns[i], row[i]);
                }

                return item;
            }
        });

        return excelEventItemReader;
    }

    @Bean
    @StepScope
    public ParsedItemProcessor itemProcessor(FilterDataManager filterDataManager){
        return new ParsedItemProcessor(filterDataManager);
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
    @StepScope
    public StepExecutionListener stepCompletionNotificationListener(){
        return new StepCompletionNotificationListener();
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
                .processor(itemProcessor(null))
                .writer(writer(null))
                .listener(stepCompletionNotificationListener())
                .build();
    }

    @Bean
    public Job importExcelXlsxToDbJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get(JobNameFactory.JOB_EXCEL_XLSX_TO_DB)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importExcelXlsxToDbStep(null))
                .end()
                .build();
    }

    @Bean
    public Step importExcelXlsxToDbStep(DBWriterListener listener) {
        return stepBuilderFactory.get(JobNameFactory.STEP_EXCEL_XLSX_TO_DB)
                .<ParsedItem, ParsedItem>chunk(3000)
                .reader(excelItemStreamReader(null))
                .processor(itemProcessor(null))
                .writer(writer(null))
//                .faultTolerant()
//                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .listener(listener)
                .build();
    }

    @Bean
    public Job importExcelXlsToDbJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get(JobNameFactory.JOB_EXCEL_XLS_TO_DB)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importExcelXlsToDbStep(null))
                .end()
                .build();
    }

    @Bean
    public Step importExcelXlsToDbStep(DBWriterListener listener) {
        return stepBuilderFactory.get(JobNameFactory.STEP_EXCEL_XLS_TO_DB)
                .<ParsedItem, ParsedItem>chunk(3000)
                .reader(excelItemReader(null))
                .processor(itemProcessor(null))
                .writer(writer(null))
                /*.faultTolerant()
                .skip(SQLException.class)
                .skipLimit(Integer.MAX_VALUE)*/
                .listener(listener)
                .build();
    }

    @Bean
    public SkipPolicy dbWriterSkipper(){
        return new DBWriterSkipper();
    }




}
