package com.zq.learn.fileuploader.support.fileparser;

import com.zq.learn.fileuploader.model.Employee;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.item.excel.mapping.PassThroughRowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
public class ExcelParser {
    public static void read(InputStream in, ItemProcessor<String[]> itemProcessor) {
        PoiItemReader<String[]> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new InputStreamResource(in));
        reader.setRowMapper(new PassThroughRowMapper());

        reader.open(new ExecutionContext());
        try {
            String[] item = null;
            while ((item = reader.read()) != null) {
                itemProcessor.process(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static interface ItemProcessor<T>{
        void process(T item);
    }
}
