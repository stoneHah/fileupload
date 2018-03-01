package com.zq.learn.fileuploader.controller;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.plugins.Page;
import com.zq.learn.fileuploader.controller.dto.ListResponse;
import com.zq.learn.fileuploader.service.ITableDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
@RestController
@RequestMapping("/table")
public class TableDataController {
    @Autowired
    private ITableDataService service;


    @GetMapping(value = "/columns/{tableName}")
    public List<String> getColumns(@PathVariable String tableName) {
        return service.getColumns(tableName);
    }

    @GetMapping(value = "/data/{tableName}")
    public ListResponse getTableData(@PathVariable String tableName,
                                     @RequestParam(value = "offset",required = false,defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit",required = false,defaultValue = "10") Integer pageSize){
        int pageNum = offset % pageSize == 0 ? offset / pageSize : (offset / pageSize + 1);
        Page page = new Page(pageNum + 1, pageSize);

        List<Map<String, String>> records = service.getDataByTable(tableName, page);
        page.setRecords(records);

        return new ListResponse(page);
    }


    public static void main(String[] args) {
    }

}
