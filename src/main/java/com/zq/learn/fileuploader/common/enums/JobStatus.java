package com.zq.learn.fileuploader.common.enums;

import org.springframework.batch.core.BatchStatus; /**
 * job 状态
 *
 * @author qun.zheng
 * @create 2018/2/7
 **/
public enum JobStatus {
    Unknow(-1),Starting(0),Complete(1),Failed(2),Exception(3);

    private int code;

    JobStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static JobStatus parse(BatchStatus status) {
        JobStatus jobStatus = Unknow;
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
                jobStatus = JobStatus.Exception;
                break;
            case UNKNOWN:
                break;
        }

        return jobStatus;
    }
}
