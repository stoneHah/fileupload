package com.zq.learn.fileuploader.support.batch;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author qun.zheng
 * @create 2018/2/1
 **/
public class JobFactory {

    public List<Job> jobs = Collections.emptyList();

    public JobFactory(List<Job> jobs) {
        this.jobs = jobs;
    }

    public Job getJob(String jobName){
        if (CollectionUtils.isEmpty(jobs) || !StringUtils.hasText(jobName)) {
            return null;
        }

        for (Job job : jobs) {
            if (job.getName().equals(jobName)) {
                return job;
            }
        }
        return null;
    }
}
