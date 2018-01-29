package com.zq.learn.fileuploader.support.fileparser;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 解压器
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
@Deprecated
public class Decompressor {

    /**
     * Unzip it
     * @param in input zip input stream
     */
    public static void unZip(InputStream in,EntryProcessor processor){

        byte[] buffer = new byte[1024];

        try{
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new BufferedInputStream(in, 1024));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while(ze!=null){
                ByteArrayOutputStream out = new ByteArrayOutputStream((int) ze.getSize());
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }


                //数据处理
                processor.process(ze.getName(),out.toByteArray());

                out.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static interface EntryProcessor{
        void process(String name,byte[] bytes);
    }

}
