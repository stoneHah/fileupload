package com.zq.learn.fileuploader.support.batch.reader;

import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/30
 **/
public class ParsedItemReader extends FlatFileItemReader<ParsedItem> {
    private static final Pattern valuePattern = Pattern.compile("=\"(.*)\"");

    public static final String[] columns = StringUtils.commaDelimitedListToStringArray(
            "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM");

    protected String encode;

    public ParsedItemReader(Resource resource) {
        setResource(resource);
        setLinesToSkip(1);
        encode = detectedEncoding(resource);
        if (StringUtils.hasText(encode)) {
            setEncoding(encode);
        }
//        setEncoding("gbk");

        setLineMapper(new DefaultLineMapper<ParsedItem>() {{
            setLineTokenizer(new DelimitedLineTokenizer());
            setFieldSetMapper(fieldSet -> {
                ParsedItem parseItem = new ParsedItem();
                String[] values = fieldSet.getValues();
                for (int i = 0; i < values.length; i++) {
                    parseItem.put(columns[i], cleanValue(values[i]));
                }
                return parseItem;
            });
        }});
    }

    protected String detectedEncoding(Resource resource) {
        String encode = null;
        byte[] buf = new byte[4096];
        InputStream fis = null;
        try {
            fis = resource.getInputStream();
            // (1)
            UniversalDetector detector = new UniversalDetector(null);

            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();

            // (4)
            encode = detector.getDetectedCharset();

            // (5)
            detector.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encode == null ? "gbk" : encode;
    }

    protected String cleanValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }

        Matcher matcher = valuePattern.matcher(value);
        if(matcher.matches()){
            return matcher.group(1);
        }

        return value;
    }

    public String getEncode() {
        return encode;
    }
}
