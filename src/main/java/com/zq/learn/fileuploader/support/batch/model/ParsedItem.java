package com.zq.learn.fileuploader.support.batch.model;

import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

public class ParsedItem {
    private LinkedHashMap<String, String> map;

    public ParsedItem() {
        this.map = new LinkedHashMap<>();
    }

    public ParsedItem put(String key, String value) {
        map.put(key, value);
        return this;
    }

    public boolean isEmpty(){
        return CollectionUtils.isEmpty(map);
    }

    public void forEach(BiConsumer<String ,String> consumer){
        map.forEach(consumer);
    }

    public Iterator<Entry<String,String>> iterator(){
        return map.entrySet().iterator();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public static void main(String[] args) {
        ParsedItem item = new ParsedItem().put("a", "zhangsan")
                .put("b", "lisi")
                .put("bc", "lisi")
                .put("ab", "wangwu");
        item.forEach((key,value) -> System.out.println(key + "=" + value));
//        System.out.println(item.map.toString());
    }
}
