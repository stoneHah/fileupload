package com.zq.learn.fileuploader.support.batch.reader;

import au.com.bytecode.opencsv.CSVReader;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 解析csv
 *
 * @author qun.zheng
 * @create 2018/2/25
 **/
public class CSVItemReader extends ParsedItemReader {

    private static final Logger logger = LoggerFactory.getLogger(CSVItemReader.class);

    private Resource resource;
    private CSVReader reader = null;

    public CSVItemReader(Resource resource) {
        super(resource);
        this.resource = resource;
    }

    @Override
    protected ParsedItem doRead() throws Exception {
        String[] values = reader.readNext();
        if (values == null) {
            return null;
        }

        ParsedItem parseItem = new ParsedItem();
        for (int i = 0; i < values.length; i++) {
            parseItem.put(columns[i], cleanValue(values[i]));
        }
        return parseItem;
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(resource, "Input resource must be set");

        if (!resource.exists()) {
            logger.warn("Input resource does not exist " + resource.getDescription());
            throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + resource);
        }

        if (!resource.isReadable()) {
            logger.warn("Input resource is not readable " + resource.getDescription());
            throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): "
                    + resource);
        }

        try {
            reader = new CSVReader(new InputStreamReader(resource.getInputStream(), getEncode()), ',', '"', 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();

        IOUtils.closeQuietly(reader);
    }
}
