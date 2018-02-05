package com.zq.learn.fileuploader.support.batch.model;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sun.swing.plaf.synth.DefaultSynthStyle.StateInfo;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedItem {
    private static final Pattern valueCleanPattern = Pattern.compile("(.*)\\s+['’]$");

    private LinkedHashMap<String, String> map;

    public ParsedItem() {
        this.map = new LinkedHashMap<>();
    }

    public ParsedItem put(String key, String value) {
        map.put(key, cleanValue(value));
        return this;
    }

    private static String cleanValue(String value) {
        if (value == null) {
            return "";
        }
        value = StringUtils.trimWhitespace(value);
        Matcher matcher = valueCleanPattern.matcher(value);
        if(matcher.matches()){
            value = StringUtils.trimWhitespace(matcher.group(1));
        }

        return value;
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
