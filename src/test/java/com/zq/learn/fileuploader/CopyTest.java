package com.zq.learn.fileuploader;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
public class CopyTest {

    private static final String fileName = "F:\\upload\\hello.xlsx";

    public static void main(String[] args) {
        File file = new File(fileName);
        for(int i = 10;i < 1000;i++) {
            File destFile = new File("F:\\upload\\hello"+(i + 1)+".xlsx");
            try {
                FileUtils.copyFile(file,destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("done...");
    }
}
