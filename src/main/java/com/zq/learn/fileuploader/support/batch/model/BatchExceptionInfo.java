package com.zq.learn.fileuploader.support.batch.model;

import org.springframework.util.CollectionUtils;

import java.io.*;
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

    public List<String> getExceptionMessageList(){
        List<String> list = new ArrayList<>();

        if (!CollectionUtils.isEmpty(chunkExceptionContextList)) {
            for (ChunkExceptionContext chunkExceptionContext : chunkExceptionContextList) {
                List<Exception> exceptionList = chunkExceptionContext.getExceptionList();
                if (!CollectionUtils.isEmpty(exceptionList)) {
                    for (Exception exception : exceptionList) {
                        list.add(exception.getMessage());
                    }
                }
            }
        }

        return list;
    }

    /**
     * 获取完整的异常信息
     * @return
     */
    public String getStackTraceInfo(){
        if (!CollectionUtils.isEmpty(chunkExceptionContextList)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(new BufferedOutputStream(out));

            for (ChunkExceptionContext chunkExceptionContext : chunkExceptionContextList) {
                Map<String, Object> context = chunkExceptionContext.getContext();
                pw.println("上下文信息:" + context.toString());
                pw.println("=============begin=========================");

                List<Exception> exceptionList = chunkExceptionContext.getExceptionList();
                if (!CollectionUtils.isEmpty(exceptionList)) {
                    for (Exception exception : exceptionList) {
                        exception.printStackTrace(pw);
                    }
                }
                pw.println("=============end=========================");
            }

            return new String(out.toByteArray());
        }

        return "";
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
