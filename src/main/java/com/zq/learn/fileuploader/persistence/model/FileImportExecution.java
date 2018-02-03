package com.zq.learn.fileuploader.persistence.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author qun.zheng
 * @since 2018-02-03
 */
@TableName("file_import_execution")
public class FileImportExecution extends Model<FileImportExecution> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
	@TableField("file_key")
	private String fileKey;
    /**
     * 任务开始时间
     */
	@TableField("job_start_time")
	private Date jobStartTime;
    /**
     * 任务结束时间
     */
	@TableField("job_end_time")
	private Date jobEndTime;
	@TableField("read_count")
	private Integer readCount;
	@TableField("write_count")
	private Integer writeCount;
	@TableField("failed_message")
	private String failedMessage;
    /**
     * 任务执行状态
     */
	private Integer status;
	@TableField("update_time")
	private Date updateTime;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFileKey() {
		return fileKey;
	}

	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}

	public Date getJobStartTime() {
		return jobStartTime;
	}

	public void setJobStartTime(Date jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	public Date getJobEndTime() {
		return jobEndTime;
	}

	public void setJobEndTime(Date jobEndTime) {
		this.jobEndTime = jobEndTime;
	}

	public Integer getReadCount() {
		return readCount;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}

	public Integer getWriteCount() {
		return writeCount;
	}

	public void setWriteCount(Integer writeCount) {
		this.writeCount = writeCount;
	}

	public String getFailedMessage() {
		return failedMessage;
	}

	public void setFailedMessage(String failedMessage) {
		this.failedMessage = failedMessage;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "FileImportExecution{" +
			"id=" + id +
			", fileKey=" + fileKey +
			", jobStartTime=" + jobStartTime +
			", jobEndTime=" + jobEndTime +
			", readCount=" + readCount +
			", writeCount=" + writeCount +
			", failedMessage=" + failedMessage +
			", status=" + status +
			", updateTime=" + updateTime +
			"}";
	}
}
