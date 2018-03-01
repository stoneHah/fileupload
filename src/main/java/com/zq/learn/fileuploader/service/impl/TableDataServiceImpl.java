package com.zq.learn.fileuploader.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.zq.learn.fileuploader.persistence.dao.GeneralMapper;
import com.zq.learn.fileuploader.service.ITableDataService;
import com.zq.learn.fileuploader.support.batch.writer.CustomerJdbcBatchItemWriter;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/3/1
 **/
@Service
public class TableDataServiceImpl implements ITableDataService {

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<String> getColumns(String tableName) {
        List<String> curColumns = new ArrayList<>();
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, tableName, null);
            if (rs.next()) {
                //Table Exist
                ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    if (!ArrayUtils.contains(CustomerJdbcBatchItemWriter.excludeColumns, columnName.toLowerCase())) {
                        curColumns.add(columnName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return curColumns;
    }

    @Override
    public List<Map<String, String>> getDataByTable(String tableName, Page page) {
        List<Map<String, String>> result = new ArrayList<>();
        List<Map<String, Object>> list = generalMapper.getByTable(tableName, page);

        if (!CollectionUtils.isEmpty(list)) {
            for (Map<String, Object> map : list) {
                Map<String, String> t = new HashMap<>();
                map.forEach((k,v) -> {
                    t.put(k, String.valueOf(v));
                });

                result.add(t);
            }
        }
        return result;
    }
}
