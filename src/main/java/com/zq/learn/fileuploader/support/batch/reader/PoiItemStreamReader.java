package com.zq.learn.fileuploader.support.batch.reader;

import com.monitorjbl.xlsx.StreamingReader;
import com.zq.learn.fileuploader.support.batch.Keys;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
public class PoiItemStreamReader<T> extends AbstractExcelItemReader<T>{
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
        workbookStream = resource.getInputStream();
        workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(workbookStream);

//        calcMaxItemCount();
    }

    private void calcMaxItemCount() {
        int maxItemCount = 0;
//        int linesToSkip = getLinesToSkip();
        for (Sheet sheet : workbook) {
            int rows = sheet.getLastRowNum();
//            int actualRows = rows - linesToSkip;
            if (rows > 0) {
                maxItemCount += rows;
            }
        }

        stepExecution.getExecutionContext().putInt(Keys.READ_MAX,maxItemCount);
    }

    @Override
    protected void doClose() throws Exception {
        IOUtils.closeQuietly(workbookStream);
        IOUtils.closeQuietly(workbook);
    }
}
