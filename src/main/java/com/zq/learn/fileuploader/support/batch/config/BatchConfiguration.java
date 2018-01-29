//package com.zq.learn.fileuploader.support.batch.config;
//
//import com.zq.learn.fileuploader.model.Employee;
//import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
//import org.springframework.batch.item.database.JdbcBatchItemWriter;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.file.mapping.DefaultLineMapper;
//import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
//import org.springframework.batch.item.support.ListItemWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import javax.sql.DataSource;
//import java.util.List;
//
//@Configuration
//@EnableBatchProcessing
//public class BatchConfiguration {
//
//    private static final String[] columns = StringUtils.commaDelimitedListToStringArray(
//            "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM"
//    );
//
//    @Autowired
//    public JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    public StepBuilderFactory stepBuilderFactory;
//
//    @Autowired
//    public DataSource dataSource;
//
//    @Bean
//    @StepScope
//    public ItemReader<ParsedItem> csvItemReader() {
//        FlatFileItemReader<ParsedItem> csvFileReader = new FlatFileItemReader<>();
//        csvFileReader.setResource(new ClassPathResource("data/employee.csv"));
//        csvFileReader.setLinesToSkip(1);
//
//        csvFileReader.setLineMapper(new DefaultLineMapper<ParsedItem>() {{
//            setLineTokenizer(new DelimitedLineTokenizer());
//            setFieldSetMapper(fieldSet -> {
//                ParsedItem parseItem = new ParsedItem();
//                String[] values = fieldSet.getValues();
//                for (int i = 0; i < values.length; i++) {
//                    parseItem.put(columns[i], values[i]);
//                }
//                return parseItem;
//            });
//        }});
//
//        return csvFileReader;
//    }
//
//    @Bean
//    public ListItemWriter<ParsedItem> ramWriter(){
//        return new ListItemWriter<>();
//    }
//
//    //@Bean
//    public JdbcBatchItemWriter<ParsedItem> writer() {
//        CustomerJdbcBatchItemWriter<ParsedItem> writer = new CustomerJdbcBatchItemWriter<ParsedItem>() {
//            @Override
//            protected void beforeWrite(List items) {
//                if (!CollectionUtils.isEmpty(items)) {
//
//                }
//            }
//        };
//        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ParsedItem>());
//        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
//        writer.setDataSource(dataSource);
//        return writer;
//    }
//
//    @Bean
//    public Job importUserJob(JobCompletionNotificationListener listener) {
//        return jobBuilderFactory.get("importCsvJob")
//                .incrementer(new RunIdIncrementer())
//                .listener(listener)
//                .flow(step1())
//                .end()
//                .build();
//    }
//
//    @Bean
//    public Step step1() {
//        return stepBuilderFactory.get("csvToRam")
//                .<ParsedItem, ParsedItem> chunk(3)
//                .reader(csvItemReader())
////                .processor(processor())
//                .writer(ramWriter())
//                .build();
//    }
//
//    @Configuration
//    public class EmployeeToDbConfiguration{
//        @Bean
//        public ItemReader<Employee> csvEmployeeItemReader() {
//            FlatFileItemReader<Employee> csvFileReader = new FlatFileItemReader<>();
//            csvFileReader.setResource(new ClassPathResource("data/employee.csv"));
//            csvFileReader.setLinesToSkip(1);
//
//            csvFileReader.setLineMapper(new DefaultLineMapper<Employee>() {{
//                setLineTokenizer(new DelimitedLineTokenizer(){{
//                    setNames(new String[]{"name","age"});
//                }});
//                setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>(){{
//                    setTargetType(Employee.class);
//                }});
//            }});
//
//            return csvFileReader;
//        }
//
//        @Bean
//        public JdbcBatchItemWriter<Employee> employeDbWriter() {
//            JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
//            writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
//            writer.setSql("INSERT INTO employee (name, age) VALUES (:name, :age)");
//            writer.setDataSource(dataSource);
//            return writer;
//        }
//
//        @Bean
//        public Job importEmployeeFromCsvToDbJob() {
//            return jobBuilderFactory.get("importEmployeeFromCsvToDbJob")
//                    .incrementer(new RunIdIncrementer())
//                    .flow(csvToDbStep())
//                    .end()
//                    .build();
//        }
//
//        @Bean
//        public Step csvToDbStep() {
//            return stepBuilderFactory.get("csvToDb")
//                    .<Employee,Employee>chunk(5000)
//                    .reader(csvEmployeeItemReader())
//                    .writer(employeDbWriter())
//                    .build();
//        }
//    }
//
//
//}
