package com.zq.learn.fileuploader.support.batch.model;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 批处理异常信息
 *
 * @author qun.zheng
 * @create 2018/2/7
 **/
public class BatchExceptionInfo implements Serializable{

    /**
     * 读取批异常
     */
    public static final String READ_BATCH_EXCEPTION = BatchExceptionInfo.class.getName() + ".READ_BATCH_EXCEPTION";

    /**
     * 写入批异常
     */
    public static final String WRITE_BATCH_EXCEPTION = BatchExceptionInfo.class.getName() + ".WRITE_BATCH_EXCEPTION";

    private List<ChunkExceptionContext> chunkExceptionContextList = new ArrayList<>();

    /**
     *
     * @param chunkExceptionContext
     */
    public void addChunkExceptionContext(ChunkExceptionContext chunkExceptionContext) {
        chunkExceptionContextList.add(chunkExceptionContext);
    }

    public int getExceptionCount(){
        int count = 0;
        if (chunkExceptionContextList.isEmpty()) {
            return count;
        }

        for (ChunkExceptionContext chunkExceptionContext : chunkExceptionContextList) {
            count += chunkExceptionContext.getExceptionCount();
        }
        return count;
    }

    public String getExceptionInfo(){
        StringBuilder builder = new StringBuilder();

        if (!CollectionUtils.isEmpty(chunkExceptionContextList)) {
            for (ChunkExceptionContext chunkExceptionContext : chunkExceptionContextList) {
                List<Exception> exceptionList = chunkExceptionContext.getExceptionList();
                if (!CollectionUtils.isEmpty(exceptionList)) {
                    for (Exception exception : exceptionList) {
                        builder.append(exception.getMessage()).append("\n");
                    }
                }
            }
        }

        return builder.toString();
    }

    public boolean isEmpty(){
        return CollectionUtils.isEmpty(chunkExceptionContextList);
    }

    public static class ChunkExceptionContext {
        private List<Exception> exceptionList = new ArrayList<>();
        private int exceptionCount = 0;
        private Map<String, Object> context = new HashMap<>();

        public void addException(Exception e) {
            exceptionList.add(e);
        }

        public void addExceptionCount(int count){
            this.exceptionCount += count;
        }

        public void addData(String key, Object value){
            context.put(key, value);
        }


        public Map<String, Object> getContext() {
            return context;
        }

        public List<Exception> getExceptionList() {
            return exceptionList;
        }

        public int getExceptionCount() {
            return exceptionCount;
        }
    }
}
