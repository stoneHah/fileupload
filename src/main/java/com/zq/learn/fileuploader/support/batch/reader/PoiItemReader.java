package com.zq.learn.fileuploader.support.batch.reader;

import com.monitorjbl.xlsx.StreamingReader;
import com.zq.learn.fileuploader.support.batch.Keys;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Iterator;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
public class PoiItemReader<T> extends AbstractExcelItemStreamReader<T> {
    private Workbook workbook = null;
    private InputStream workbookStream = null;

    private StepExecution stepExecution;

    @BeforeStep
    void beforeStep(StepExecution stepExecution){
        this.stepExecution = stepExecution;
    }

    @Override
    protected Sheet getSheet(int sheetIndex) {
        return workbook.getSheetAt(sheetIndex);
    }

    @Override
    protected int getNumberOfSheets() {
        return workbook.getNumberOfSheets();
    }

    @Override
    protected void openExcelFile(Resource resource) throws Exception {
       /* this.workbookStream = resource.getInputStream();
        if (!this.workbookStream.markSupported() && !(this.workbookStream instanceof PushbackInputStream)) {
            throw new IllegalStateException("InputStream MUST either support mark/reset, or be wrapped as a PushbackInputStream");
        } else {
            this.workbook = WorkbookFactory.create(this.workbookStream);
        }*/

        workbookStream = resource.getInputStream();
        workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(workbookStream);

    }

    @Override
    protected void doClose() throws Exception {
        logger.info("close workbook...");

        IOUtils.closeQuietly(workbookStream);
        IOUtils.closeQuietly(workbook);
    }
}
