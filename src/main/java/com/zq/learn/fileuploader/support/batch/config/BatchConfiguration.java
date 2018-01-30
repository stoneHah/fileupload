package com.zq.learn.fileuploader.support.batch.config;

import com.zq.learn.fileuploader.support.batch.listener.JobCompletionNotificationListener;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import com.zq.learn.fileuploader.support.batch.reader.ParsedItemReader;
import com.zq.learn.fileuploader.support.batch.writer.CustomerJdbcBatchItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;



    @Bean
    @StepScope
    public FlatFileItemReader<ParsedItem> csvItemReader(@Value("#{jobParameters['resource']}") Resource resource) {
        return new ParsedItemReader(resource);
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
        return jobBuilderFactory.get("importCsvToDbJob5")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importCsvToDbStep())
                .end()
                .build();
    }

    @Bean
    public Step importCsvToDbStep() {
        return stepBuilderFactory.get("importCsvToDbStep")
                .<ParsedItem, ParsedItem> chunk(3000)
                .reader(csvItemReader(null))
//                .processor(processor())
                .writer(writer(null))
                .build();
    }

}
