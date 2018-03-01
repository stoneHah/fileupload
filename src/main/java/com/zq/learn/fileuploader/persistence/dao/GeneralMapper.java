package com.zq.learn.fileuploader.persistence.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import org.apache.ibatis.annotations.Param;

public interface GeneralMapper {
	public List<Map<String, Object>> getByTable(@Param("tableName") String tableName,
												Page page);
}
