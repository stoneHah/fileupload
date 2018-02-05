package com.zq.learn.fileuploader.support.batch;

import com.zq.learn.fileuploader.common.enums.FileExtension;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/1
 **/
public class JobNameFactory {
    public static final String JOB_CSV_TO_DB = "CsvToDbJob";
    public static final String JOB_EXCEL_XLSX_TO_DB = "ExcelXlsxToDbJob";
    public static final String JOB_EXCEL_XLS_TO_DB = "ExcelXlsToDbJob";

    public static final String STEP_CSV_TO_DB = "CsvToDbStep";
    public static final String STEP_EXCEL_XLSX_TO_DB = "ExcelXlsxToDbStep";
    public static final String STEP_EXCEL_XLS_TO_DB = "ExcelXlsToDbStep";

    public static String getJobName(FileExtension extension){
        String jobName = null;
        switch (extension) {
            case Csv:
                jobName = JOB_CSV_TO_DB;
                break;
            case Excel_XLS:
                jobName = JOB_EXCEL_XLS_TO_DB;
                break;
            case Excel_XLSX:
                jobName = JOB_EXCEL_XLSX_TO_DB;
                break;
        }

        return jobName == null ? "" : jobName;
    }
}
