package com.zq.learn.fileuploader.service;

import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;
import java.util.Map;

/**
 * 表数据查询信息
 *
 * @author qun.zheng
 * @create 2018/3/1
 **/
public interface ITableDataService {
    /**
     * 获取表的列信息
     * @param tableName
     * @return
     */
    List<String> getColumns(String tableName);

    /**
     * 获取表数据
     * @param tableName
     * @param page
     * @return
     */
    List<Map<String,String>> getDataByTable(String tableName, Page page);
}
