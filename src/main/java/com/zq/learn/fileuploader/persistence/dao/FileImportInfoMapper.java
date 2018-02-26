package com.zq.learn.fileuploader.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.zq.learn.fileuploader.persistence.model.FileImportInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author qun.zheng
 * @since 2018-01-31
 */
public interface FileImportInfoMapper extends BaseMapper<FileImportInfo> {

    /**
     * 获取导入文件信息
     * @param fileName
     * @param startTime
     * @param endTime
     * @param page
     * @return
     */
    List<FileImportInfo> getFileImportInfos(@Param("fileName") String fileName, @Param("startTime")Date startTime,
                                            @Param("endTime")Date endTime, Page page);
}