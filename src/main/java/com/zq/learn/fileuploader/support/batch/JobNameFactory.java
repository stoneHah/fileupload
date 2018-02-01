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
    public static final String JOB_EXCEL_TO_DB = "ExcelToDbJob";

    public static final String STEP_CSV_TO_DB = "CsvToDbStep";
    public static final String STEP_EXCEL_TO_DB = "ExcelToDbStep";

    public static String getJobName(FileExtension extension){
        String jobName = null;
        switch (extension) {
            case CSV:
                jobName = JOB_CSV_TO_DB;
                break;
            case EXCEL:
                jobName = JOB_EXCEL_TO_DB;
                break;
        }

        return jobName == null ? "" : jobName;
    }
}
