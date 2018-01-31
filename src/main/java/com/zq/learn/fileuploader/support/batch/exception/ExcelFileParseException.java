package com.zq.learn.fileuploader.support.batch.exception;

import org.springframework.batch.item.ParseException;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
public class ExcelFileParseException extends ParseException {
    private final String filename;
    private final String sheet;
    private final int rowNumber;
    private String[] row;

    public ExcelFileParseException(String message, Throwable cause, String filename, String sheet, int rowNumber) {
        super(message, cause);
        this.filename = filename;
        this.sheet = sheet;
        this.rowNumber = rowNumber;
    }

    public ExcelFileParseException(String message, Throwable cause, String filename, String sheet, int rowNumber, String[] row) {
        super(message, cause);
        this.filename = filename;
        this.sheet = sheet;
        this.rowNumber = rowNumber;
        this.row = row;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getSheet() {
        return this.sheet;
    }

    public int getRowNumber() {
        return this.rowNumber;
    }

    public String[] getRow() {
        return this.row;
    }
}