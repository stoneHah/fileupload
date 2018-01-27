package com.zq.learn.fileuploader.support.fileparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
@Component
public class BatchExcelParser {
    private static final Logger logger = LoggerFactory.getLogger(BatchExcelParser.class);

    private ThreadPoolExecutor pool;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void postConstruct(){
        logger.info("初始化批量excel解析池");
        pool = new ThreadPoolExecutor(
                2, 5, 120, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(3000));
    }

    public void parse(ExcelData excelData){
        pool.execute(new ExcelParseTask(excelData));
    }

    @PreDestroy
    public void onDestroy(){
        logger.info("销毁批量excel解析池");
        pool.shutdown();
    }

    public static class ExcelData{
        private InputStream stream;
        private String fileName;
        private String group;

        public ExcelData(String group,String fileName,InputStream stream) {
            this.stream = stream;
            this.group = group;
            this.fileName = fileName;
        }

        public InputStream getStream() {
            return stream;
        }
    }

    private class ExcelParseTask implements Runnable{

        private ExcelData excelData;

        public ExcelParseTask(ExcelData excelData) {
            this.excelData = excelData;
        }

        @Override
        public void run() {
            long mill = System.currentTimeMillis();

            final int chunkSize = 500;
            final List<String[]>[] chunk = new List[]{new ArrayList<>(chunkSize)};
//            ExcelParser.read(excelData.getStream(), item -> System.out.println(excelData.fileName + "_no" + increment.getAndAdd(1) + JSON.toJSONString(item)));
            ExcelParser.read(excelData.getStream(), item -> {
                chunk[0].add(item);
                if (chunk[0].size() > chunkSize) {
                    saveToDb(chunk[0]);
                    chunk[0] = new ArrayList<>(chunkSize);
                }
            });

            System.out.println("耗时:" + (System.currentTimeMillis() - mill) + "ms");
        }

        private void saveToDb(List<String[]> itemList) {
            if (CollectionUtils.isEmpty(itemList)) {
                return;
            }
            StringBuilder sb = new StringBuilder()
                    .append("insert into employee(name,age,dept,salary) values");
            for (int i = 0;i < itemList.size();i++) {
                String[] item = itemList.get(i);
                sb.append("(").append("'"+item[0]+"',"+item[1]+",'"+item[2]+"',"+item[3]).append(")");

                if (i < itemList.size() - 1) {
                    sb.append(",");
                }
            }
            jdbcTemplate.batchUpdate(sb.toString());
        }
    }
}
