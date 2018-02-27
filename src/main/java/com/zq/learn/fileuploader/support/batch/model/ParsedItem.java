package com.zq.learn.fileuploader.support.batch.model;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sun.swing.plaf.synth.DefaultSynthStyle.StateInfo;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedItem {
    private static final Pattern valueCleanPattern = Pattern.compile("(.*)\\s+['’]$");

    private List<KeyValue<String, String>> keyValueList;

    public ParsedItem() {
        this.keyValueList = new ArrayList<>();
    }

    public ParsedItem put(String key, String value) {
        keyValueList.add(new KeyValue<>(key, cleanValue(value)));
        return this;
    }

    private static String cleanValue(String value) {
        if (value == null) {
            return "";
        }
        value = StringUtils.trimWhitespace(value);
        Matcher matcher = valueCleanPattern.matcher(value);
        if (matcher.matches()) {
            value = StringUtils.trimWhitespace(matcher.group(1));
        }

        return value;
    }

    @Transient
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(keyValueList);
    }

    public void forEach(Consumer<KeyValue<String, String>> consumer) {
        keyValueList.forEach(consumer);
    }

    public Iterator<KeyValue<String, String>> iterator() {
        return keyValueList.iterator();
    }

    public KeyValue<String,String> get(int index) {
        if (isEmpty() || (keyValueList.size() - 1 < index)) {
            return null;
        }

        return keyValueList.get(index);
    }

    public List<KeyValue<String, String>> getKeyValueList() {
        return keyValueList;
    }

    @Override
    public String toString() {
        return keyValueList.toString();
    }

    public static void main(String[] args) {
        /*ParsedItem item = new ParsedItem().put("a", "zhangsan")
                .put("b", "lisi")
                .put("bc", "lisi")
                .put("ab", "wangwu");
        item.forEach((key,value) -> System.out.println(key + "=" + value));*/
//        System.out.println(item.map.toString());

       /* Matcher matcher = valuePattern.matcher("null");
        if(matcher.matches()){
            System.out.println(matcher.group(1));
        }*/

//        System.out.println(valueCleanPattern.matcher("132434197705056026      ’").matches());
        System.out.println(cleanValue("132434197705056026      ’"));
    }


}
