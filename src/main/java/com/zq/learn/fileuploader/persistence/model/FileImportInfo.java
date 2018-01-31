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
 * @since 2018-01-31
 */
@TableName("file_import_info")
public class FileImportInfo extends Model<FileImportInfo> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
	@TableField("file_name")
	private String fileName;
	@TableField("file_path")
	private String filePath;

	@TableField("table_name")
	private String tableName;
    /**
     * 对应spring batch的job instance id
     */
	@TableField("job_instance_id")
	private Integer jobInstanceId;
    /**
     * 文件id
     */
	@TableField("file_key")
	private String fileKey;
    /**
     * 每次上传都会有一个key与之对应
     */
	@TableField("upload_key")
	private String uploadKey;
	@TableField("update_time")
	private Date updateTime;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getJobInstanceId() {
		return jobInstanceId;
	}

	public void setJobInstanceId(Integer jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	public String getFileKey() {
		return fileKey;
	}

	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}

	public String getUploadKey() {
		return uploadKey;
	}

	public void setUploadKey(String uploadKey) {
		this.uploadKey = uploadKey;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "FileImportInfo{" +
			"id=" + id +
			", fileName=" + fileName +
			", filePath=" + filePath +
			", jobInstanceId=" + jobInstanceId +
			", fileKey=" + fileKey +
			", uploadKey=" + uploadKey +
			", updateTime=" + updateTime +
			"}";
	}
}
