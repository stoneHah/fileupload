package com.zq.learn.fileuploader.support.batch.model;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/7
 **/
public class KeyValue<K,V> {
    private K key;
    private V value;

    public KeyValue() {
    }

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("{")
                .append(key).append("=").append(value)
                .append("}")
                .toString();
    }
}
