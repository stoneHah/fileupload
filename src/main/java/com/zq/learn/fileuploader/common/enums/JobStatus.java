package com.zq.learn.fileuploader.common.enums;

import org.springframework.batch.core.BatchStatus; /**
 * job 状态
 *
 * @author qun.zheng
 * @create 2018/2/7
 **/
public enum JobStatus {
    Unknow(-1,"未知"),Starting(0,"开启"),Complete(1,"完成"),Failed(2,"失败"),Exception(3,"异常");

    private int code;
    private String desc;

    JobStatus(int code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
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

    public static JobStatus getByCode(Integer code) {
        if (code != null) {
            JobStatus[] values = values();
            for (JobStatus value : values) {
                if (value.getCode() == code) {
                    return value;
                }
            }
        }

        return JobStatus.Unknow;
    }
}
