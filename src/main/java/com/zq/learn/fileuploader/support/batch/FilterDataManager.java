package com.zq.learn.fileuploader.support.batch;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.sun.javafx.scene.control.skin.VirtualFlow.ArrayLinkedList;
import com.zq.learn.fileuploader.cache.ExpireableCacheManager;
import com.zq.learn.fileuploader.persistence.dao.FileFilterInfoMapper;
import com.zq.learn.fileuploader.persistence.model.FileFilterInfo;
import com.zq.learn.fileuploader.support.batch.model.KeyValue;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import org.apache.ibatis.session.RowBounds;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.FileFilter;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 过滤数据管理器
 *
 * @author qun.zheng
 * @create 2018/2/27
 **/
@Component
public class FilterDataManager implements InitializingBean {

    private int thresold = 500;

    private JdbcBatchItemWriter<FileFilterInfo> jdbcBatchItemWriter;
    private ConcurrentHashMap<String, List<FileFilterInfo>> map = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, AtomicInteger> fileFilterCountMap = new ConcurrentHashMap<>();

    @Autowired
    private FileFilterInfoMapper fileFilterInfoMapper;

    @Autowired
    private DataSource dataSource;

    public FilterDataManager() {
    }

    public void addFilterData(String fileKey, ParsedItem parsedItem){
        if (parsedItem == null) {
            return;
        }

        //增加过滤记录数
        incrementFilterCount(fileKey);
        List<FileFilterInfo> list = getFilterDataList(fileKey);

        FileFilterInfo fileFilterInfo = new FileFilterInfo();
        fileFilterInfo.setFileKey(fileKey);
        fileFilterInfo.setFilterRecord(JSON.toJSONString(parsedItem));
        list.add(fileFilterInfo);

        if (list.size() >= thresold) {
            flush(list);
        }
    }

    private void incrementFilterCount(String fileKey) {
        AtomicInteger atomicInteger = fileFilterCountMap.get(fileKey);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger(0);
            fileFilterCountMap.put(fileKey,atomicInteger);
        }

        atomicInteger.incrementAndGet();
    }

    public void flush(String fileKey) {
        List<FileFilterInfo> list = getFilterDataList(fileKey);
        flush(list);
    }

    private void flush(List<FileFilterInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        try {
            jdbcBatchItemWriter.write(list);
            list.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<FileFilterInfo> getFilterDataList(String fileKey) {
        List<FileFilterInfo> list = null;
        if (!map.containsKey(fileKey)) {
            synchronized (this) {
                if(!map.containsKey(fileKey)){
                    list = Collections.synchronizedList(new ArrayList<>());
                    map.put(fileKey, list);
                }else{
                    list = map.get(fileKey);
                }
            }
        }else{
            list = map.get(fileKey);
        }

        return list;
    }

    public int countFilterData(String fileKey){
        AtomicInteger atomicInteger = fileFilterCountMap.get(fileKey);
        if (atomicInteger == null) {
            return 0;
        }

        return atomicInteger.intValue();
        /*int total = 0;

        FileFilterInfo entity = new FileFilterInfo();
        entity.setFileKey(fileKey);
        com.baomidou.mybatisplus.mapper.Wrapper<FileFilterInfo> wrapper = new EntityWrapper<>(entity);
        Integer count = fileFilterInfoMapper.selectCount(wrapper);
        if (count != null) {
            total += count;
        }

        List<FileFilterInfo> list = getFilterDataList(fileKey);
        if(!CollectionUtils.isEmpty(list)){
            total += list.size();
        }
        return total;*/
    }

    public List<String> moreFilterData(String fileKey) {
        int limit = 200;
        FileFilterInfo entity = new FileFilterInfo();
        entity.setFileKey(fileKey);
        com.baomidou.mybatisplus.mapper.Wrapper<FileFilterInfo> wrapper = new EntityWrapper<>(entity);

        List<FileFilterInfo> list = fileFilterInfoMapper.selectPage(new RowBounds(0, limit), wrapper);
        /*if (list.size() < limit) {
            List<FileFilterInfo> filterDataList = getFilterDataList(fileKey);
            if (!CollectionUtils.isEmpty(filterDataList)) {
                list.addAll(filterDataList);
            }
        }*/

        List<String> result = new ArrayList<>();
        for (FileFilterInfo fileFilterInfo : list) {
            result.add(fileFilterInfo.getFilterRecord());
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setSql("insert into file_filter_info(file_key,filter_record) values (?,?)");
        jdbcBatchItemWriter.setItemPreparedStatementSetter((ItemPreparedStatementSetter<FileFilterInfo>) (item, ps) -> {
            ps.setString(1, item.getFileKey());
            ps.setString(2, item.getFilterRecord());
        });

        jdbcBatchItemWriter.afterPropertiesSet();
    }

}
