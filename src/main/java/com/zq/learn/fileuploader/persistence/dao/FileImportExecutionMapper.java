package com.zq.learn.fileuploader.persistence.dao;

import com.zq.learn.fileuploader.persistence.model.FileImportExecution;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author qun.zheng
 * @since 2018-02-03
 */
public interface FileImportExecutionMapper extends BaseMapper<FileImportExecution> {

    List<FileImportExecution> getFileImportExecutions(@Param("fileKeys") String[] fileKeys);
}