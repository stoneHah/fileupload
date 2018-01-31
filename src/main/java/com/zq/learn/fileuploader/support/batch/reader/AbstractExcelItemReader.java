package com.zq.learn.fileuploader.support.batch.reader;

import com.zq.learn.fileuploader.support.batch.exception.ExcelFileParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Iterator;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/30
 **/
public abstract class AbstractExcelItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T>, InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private Resource resource;
    private int linesToSkip = 0;
    private int currentSheet = 0;
    private int currentRow = 0;
    private RowMapper<T> rowMapper;
    private RowCallbackHandler skippedRowsCallback;
    private boolean noInput = false;
    private boolean strict = true;

//    private Iterator<Sheet> sheetIterator;
    private Iterator<Row> rowIterator;

    public AbstractExcelItemReader() {
        this.setName(ClassUtils.getShortName(this.getClass()));
    }

    protected T doRead() throws Exception {
        if (!this.noInput && this.rowIterator != null) {
            if (this.rowIterator.hasNext()) {
                Row row = this.rowIterator.next();
                try {
                    return this.rowMapper.mapRow(row);
                } catch (Exception e) {
                    throw new ExcelFileParseException("Exception parsing Excel file.", e, this.resource.getDescription(), getSheet(currentSheet).getSheetName(), currentRow);
                }
            } else {
                ++this.currentSheet;
                if (this.currentSheet >= this.getNumberOfSheets()) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("No more sheets in '" + this.resource.getDescription() + "'.");
                    }

                    return null;
                } else {
                    this.openSheet();
                    return this.doRead();
                }
            }
        } else {
            return null;
        }
    }

    protected void doOpen() throws Exception {
        Assert.notNull(this.resource, "Input resource must be set");
        this.noInput = true;
        if (!this.resource.exists()) {
            if (this.strict) {
                throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + this.resource);
            } else {
                this.logger.warn("Input resource does not exist '" + this.resource.getDescription() + "'.");
            }
        } else if (!this.resource.isReadable()) {
            if (this.strict) {
                throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): " + this.resource);
            } else {
                this.logger.warn("Input resource is not readable '" + this.resource.getDescription() + "'.");
            }
        } else {
            this.openExcelFile(this.resource);
            this.openSheet();
            this.noInput = false;
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Opened workbook [" + this.resource.getFilename() + "] with " + this.getNumberOfSheets() + " sheets.");
            }

        }
    }

    private void openSheet() {
        Sheet sheet = this.getSheet(this.currentSheet);
        this.rowIterator = sheet.rowIterator();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Opening sheet " + sheet.getSheetName() + ".");
        }

        for (int i = 0; i < this.linesToSkip; ++i) {
            if (this.rowIterator.hasNext() && this.skippedRowsCallback != null) {
                this.skippedRowsCallback.handleRow(this.rowIterator.next());
            }
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Openend sheet " + sheet.getSheetName() + ", with " + sheet.getPhysicalNumberOfRows() + " rows.");
        }

    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.rowMapper, "RowMapper must be set");
    }

    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }

    public int getLinesToSkip() {
        return linesToSkip;
    }

    protected abstract Sheet getSheet(int var1);

    protected abstract int getNumberOfSheets();

    protected abstract void openExcelFile(Resource resource) throws Exception;

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public void setSkippedRowsCallback(RowCallbackHandler skippedRowsCallback) {
        this.skippedRowsCallback = skippedRowsCallback;
    }
}
