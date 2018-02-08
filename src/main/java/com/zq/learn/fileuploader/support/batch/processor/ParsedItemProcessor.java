package com.zq.learn.fileuploader.support.batch.processor;

import com.zq.learn.fileuploader.support.batch.Keys;
import com.zq.learn.fileuploader.support.batch.model.KeyValue;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import com.zq.learn.fileuploader.utils.IDCardUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/7
 **/
public class ParsedItemProcessor implements ItemProcessor<ParsedItem,ParsedItem> {

    private StepExecution stepExecution;

    @BeforeStep
    void beforeStep(StepExecution stepExecution){
        this.stepExecution = stepExecution;
    }

    @Override
    public ParsedItem process(ParsedItem item) throws Exception {
        JobParameters jobParameters = stepExecution.getJobParameters();
        String idcardColumns = jobParameters.getString(Keys.ID_CARD_COLUMNS);
        if (StringUtils.hasText(idcardColumns)) {
            if(!isValidByIDCardCheck(item,idcardColumns)){
                addFilterRecordToContext(item);
                return null;
            }
        }

        return item;
    }

    /**
     * 添加过滤记录 到上下文
     * @param parsedItem
     */
    private void addFilterRecordToContext(ParsedItem parsedItem) {
        ExecutionContext executionContext = stepExecution.getExecutionContext();

        List<ParsedItem> list = null;
        if (!executionContext.containsKey(Keys.FILTER_RECORDS)) {
            synchronized (this) {
                if (!executionContext.containsKey(Keys.FILTER_RECORDS)) {
                    list = Collections.synchronizedList(new ArrayList<ParsedItem>());
                    executionContext.put(Keys.FILTER_RECORDS,list);
                }
            }
        }else{
            list = (List<ParsedItem>) executionContext.get(Keys.FILTER_RECORDS);
        }

        list.add(parsedItem);
    }

    private boolean isValidByIDCardCheck(ParsedItem item, String idcardColumns) {
        String[] columnAry = delimitedStringToArray(idcardColumns,",，");
        if (ArrayUtils.isEmpty(columnAry)) {
            return true;
        }

        for (String columnNum : columnAry) {
            if (StringUtils.hasText(columnNum)) {
                try {
                    int num = Integer.valueOf(columnNum.trim());
                    if(num < 1){
                        continue;
                    }
                    KeyValue<String,String> keyValue = item.get(num - 1);
                    if (keyValue == null) {
                        continue;
                    }

                    if (!IDCardUtils.isValidIDCard(keyValue.getValue())) {
                        return false;
                    }

                } catch (NumberFormatException e) {
                }
            }
        }


        return true;
    }

    private String[] delimitedStringToArray(String str, String delimiter) {
        if (str == null) {
            return new String[0];
        }
        if (!StringUtils.hasText(delimiter)) {
            return new String[] {str};
        }

        List<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(str, delimiter);
        while(tokenizer.hasMoreTokens()){
            list.add(tokenizer.nextToken());
        }

        return list.toArray(new String[list.size()]);
    }

    public static void main(String[] args) {
        StringTokenizer tokenizer = new StringTokenizer("1,2，3", "  ");
        while(tokenizer.hasMoreTokens()){
            System.out.println(tokenizer.nextToken());
        }
    }
}
