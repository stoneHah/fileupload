package com.zq.learn.fileuploader.persistence.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
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
 * @since 2018-02-27
 */
@TableName("file_filter_info")
public class FileFilterInfo extends Model<FileFilterInfo> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
	@TableField("file_key")
	private String fileKey;
	@TableField("filter_record")
	private String filterRecord;


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

	public String getFilterRecord() {
		return filterRecord;
	}

	public void setFilterRecord(String filterRecord) {
		this.filterRecord = filterRecord;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "FileFilterInfo{" +
			"id=" + id +
			", fileKey=" + fileKey +
			", filterRecord=" + filterRecord +
			"}";
	}
}
