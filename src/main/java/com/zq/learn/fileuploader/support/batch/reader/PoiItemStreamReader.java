package com.zq.learn.fileuploader.support.batch.reader;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
    }

    @Override
    protected void doClose() throws Exception {
        IOUtils.closeQuietly(workbookStream);
        IOUtils.closeQuietly(workbook);
    }
}
