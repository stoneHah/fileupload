package com.zq.learn.fileuploader.support.fileparser;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
@Deprecated
public class CsvParser implements Parser{
    public void read(InputStream in, ItemProcessor<String[]> itemProcessor) {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new InputStreamReader(in,"utf-8"), ',', '"', 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] nextLine;
        try {
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine != null) {
                    itemProcessor.process(nextLine);
                }
            }

            itemProcessor.complete();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(in);
        }
    }
}
