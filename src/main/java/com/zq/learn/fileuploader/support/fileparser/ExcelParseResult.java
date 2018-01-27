package com.zq.learn.fileuploader.support.fileparser;

import org.springframework.util.MultiValueMap;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
public class ExcelParseResult {
    private long successCount;
    private long errorCount;

    private MultiValueMap<String,ErrorRecord> errorRecordsMap;

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public MultiValueMap<String, ErrorRecord> getErrorRecordsMap() {
        return errorRecordsMap;
    }

    public void setErrorRecordsMap(MultiValueMap<String, ErrorRecord> errorRecordsMap) {
        this.errorRecordsMap = errorRecordsMap;
    }

    private class ErrorRecord{
        private int sheetNo;
        private int rowNo;
        private String msg;

        public ErrorRecord(int sheetNo, int rowNo, String msg) {
            this.sheetNo = sheetNo;
            this.rowNo = rowNo;
            this.msg = msg;
        }

        public int getSheetNo() {
            return sheetNo;
        }

        public void setSheetNo(int sheetNo) {
            this.sheetNo = sheetNo;
        }

        public int getRowNo() {
            return rowNo;
        }

        public void setRowNo(int rowNo) {
            this.rowNo = rowNo;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}
