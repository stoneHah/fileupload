package com.zq.learn.fileuploader.support.batch.reader;

import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/30
 **/
public class ParsedItemReader extends FlatFileItemReader<ParsedItem> {

    private static final String[] columns = StringUtils.commaDelimitedListToStringArray(
            "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM");

    public ParsedItemReader(Resource resource) {
        setResource(resource);
        setLinesToSkip(1);
        setEncoding("utf-8");

        setLineMapper(new DefaultLineMapper<ParsedItem>() {{
            setLineTokenizer(new DelimitedLineTokenizer());
            setFieldSetMapper(fieldSet -> {
                ParsedItem parseItem = new ParsedItem();
                String[] values = fieldSet.getValues();
                for (int i = 0; i < values.length; i++) {
                    parseItem.put(columns[i], values[i]);
                }
                return parseItem;
            });
        }});
    }
}
