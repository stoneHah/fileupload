package com.zq.learn.fileuploader.persistence.model;

import java.io.Serializable;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author qun.zheng
 * @since 2018-03-01
 */
@TableName("file_table_info")
public class FileTableInfo extends Model<FileTableInfo> {

    private static final long serialVersionUID = 1L;

	private Integer id;
    /**
     * 表名
     */
	@TableField("table_name")
	private String tableName;
    /**
     * 表描述信息
     */
	@TableField("table_desc")
	private String tableDesc;
    /**
     * 表标题
     */
	@TableField("table_title")
	private String tableTitle;

	/**
	 * 版本（乐观锁保留字段）
	 */
	@Version
	private Integer version;

	@TableField("update_time")
	private Date updateTime;

	public FileTableInfo() {
	}

	public FileTableInfo(String tableName) {
		this.tableName = tableName;
	}

	public FileTableInfo(String tableName, String tableDesc) {
		this.tableName = tableName;
		this.tableDesc = tableDesc;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "FileTableInfo{" +
			"id=" + id +
			", tableName=" + tableName +
			", tableDesc=" + tableDesc +
			", tableTitle=" + tableTitle +
			", updateTime=" + updateTime +
			"}";
	}
}
