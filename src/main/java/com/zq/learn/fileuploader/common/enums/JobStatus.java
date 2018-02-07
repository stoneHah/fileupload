package com.zq.learn.fileuploader.common.enums;

import org.springframework.batch.core.BatchStatus; /**
 * job 状态
 *
 * @author qun.zheng
 * @create 2018/2/7
 **/
public enum JobStatus {
    Starting(0),Complete(1),Failed(2),Exception(3);

    private int code;

    JobStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static JobStatus parse(BatchStatus status) {
        JobStatus jobStatus = null;
        switch (status) {
            case COMPLETED:
                jobStatus = JobStatus.Complete;
                break;
            case FAILED:
                jobStatus = JobStatus.Failed;
                break;
            case STARTING:
            case STARTED:
                jobStatus = JobStatus.Starting;
                break;
            case ABANDONED:
            case STOPPED:
            case STOPPING:
            case UNKNOWN:
                jobStatus = JobStatus.Exception;
                break;
        }

        return jobStatus;
    }
}
