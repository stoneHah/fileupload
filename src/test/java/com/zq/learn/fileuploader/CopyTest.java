package com.zq.learn.fileuploader;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
public class CopyTest {

    private static final String fileName = "F:\\upload\\csv\\employee.csv";

    public static void main(String[] args) throws Exception {
       /* File file = new File(fileName);
        for(int i = 1;i < 2000;i++) {
            File destFile = new File("F:\\upload\\csv\\employee"+(i + 1)+".csv");
            try {
                FileUtils.copyFile(file,destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("done...");*/

       /* File file = new File("D:\\Documents\\employee.xlsx");
        System.out.println(file.getAbsolutePath());

        UrlResource urlResource = new UrlResource("file:///" + file.getAbsolutePath());
        System.out.println(urlResource.exists());*/

       saveAsXlsx();
    }

    private static void saveAsXlsx() throws Exception {
        Thread.sleep(10 * 1000);
        String excelFileName = "D:\\Documents\\upload\\employee10w_new.xlsx";

        Workbook workbook = WorkbookFactory.create(new File("D:\\Documents\\upload\\employee - 10w.xls"));

        FileOutputStream fileOut = new FileOutputStream(excelFileName);
        workbook.write(fileOut);

        fileOut.flush();
        fileOut.close();

        Thread.sleep(30 * 1000);
    }
}
