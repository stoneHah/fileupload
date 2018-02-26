package com.zq.learn.fileuploader.controller.dto;

import com.baomidou.mybatisplus.plugins.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * restful 接口返回List的实体类
 * @param <T>
 */
public class ListResponse<T> {

    private List<T> rows;
    private int total;

    public ListResponse(Object o){
        if (null != o){
            if (o instanceof Page){
                Page page = (Page)o;
                rows = page.getRecords();
                total = page.getTotal();
            }else {
                rows = new ArrayList<>();
                total = 0;
            }
        }else {
            rows = new ArrayList<>();
            total = 0;
        }
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> data) {
        this.rows = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
