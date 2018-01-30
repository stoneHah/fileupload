package com.zq.learn.fileuploader.support.fileparser;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
@Deprecated
public class ExcelParser implements Parser {
    public void read(InputStream in, ItemProcessor<String[]> itemProcessor) {
        Workbook workbook = null;
        try {
            workbook = StreamingReader.builder()
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(in);
            for (Sheet sheet : workbook){
                System.out.println(sheet.getSheetName());
                for (Row r : sheet) {
                    List<String> items = new ArrayList<>();
                    for (Cell c : r) {
                        items.add(getStringCellValue(c));
                    }

                    itemProcessor.process(items.toArray(new String[items.size()]));
                }
            }

            itemProcessor.complete();
        }finally {
            IOUtils.closeQuietly(workbook);
        }
        /*try {
            Workbook workbook = WorkbookFactory.create(in);
            workbook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);

            int numberOfSheets = workbook.getNumberOfSheets();
            try {
                for (int i = 0; i < numberOfSheets; i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    openSheet(sheet, itemProcessor);
                }

                itemProcessor.complete();
            } finally {
                workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }*/
        /*PoiItemReader<String[]> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new InputStreamResource(in));
        reader.setRowMapper(new PassThroughRowMapper());

        try {
            reader.open(new ExecutionContext());

            String[] item = null;
            while ((item = reader.read()) != null) {
                itemProcessor.process(item);
            }

            itemProcessor.complete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }*/
    }

    private void openSheet(Sheet sheet, ItemProcessor<String[]> itemProcessor) {
        try {
            Row headerRow = sheet.getRow(0);
            int colNum = headerRow.getPhysicalNumberOfCells();
            int rowNum = sheet.getLastRowNum();

            String[] items = null;
            for (int i = 1; i <= rowNum; ++i) {
                items = new String[colNum];

                Row row = sheet.getRow(i);
                for (int j = 0; j < colNum; ++j) {
                    String cellValue = getStringCellValue(row.getCell(j));
                    items[j] = cellValue;
                }

                itemProcessor.process(items);
            }
        } catch (Exception e) {
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
