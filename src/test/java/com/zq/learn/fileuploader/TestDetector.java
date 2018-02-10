package com.zq.learn.fileuploader;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.FileNotFoundException;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/9
 **/
public class TestDetector {
    public static void main(String[] args) throws Exception {
        byte[] buf = new byte[4096];
//        java.io.FileInputStream fis = new java.io.FileInputStream("D:\\Documents\\北沙 - 副本1.csv");
        java.io.FileInputStream fis = new java.io.FileInputStream("C:\\Users\\Administrator\\Desktop\\a2016(2).csv");

        // (1)
        UniversalDetector detector = new UniversalDetector(null);

        // (2)
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        // (3)
        detector.dataEnd();

        // (4)
        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }

        // (5)
        detector.reset();
    }
}
