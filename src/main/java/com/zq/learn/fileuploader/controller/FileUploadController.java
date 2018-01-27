package com.zq.learn.fileuploader.controller;

import com.alibaba.fastjson.JSON;
import com.zq.learn.fileuploader.support.fileparser.BatchExcelParser;
import com.zq.learn.fileuploader.support.fileparser.BatchExcelParser.ExcelData;
import com.zq.learn.fileuploader.support.fileparser.Decompressor;
import com.zq.learn.fileuploader.support.fileparser.ExcelParser;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.mapping.PassThroughRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
@Controller
@RequestMapping("/")
public class FileUploadController {

    @Autowired
    private BatchExcelParser batchExcelParser;

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String upload(HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload();

            // Parse the request
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    String filename = item.getName();
                    parseCompressFile(filename,stream);
                }
            }

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" );
        } catch (FileUploadException e) {
            redirectAttributes.addFlashAttribute("message",
                    " uploaded error'" );
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message",
                    " uploaded error'" );
        }

        return "redirect:/uploadStatus";
    }

    /**
     * 解析压缩文件
     * @param stream
     */
    private void parseCompressFile(String compressFilename, InputStream stream) {
        // Process the input stream
        Decompressor.unZip(stream, (fileName, bytes) -> {
            System.out.println("parse file" + fileName);
            batchExcelParser.parse(new ExcelData(compressFilename,fileName,new ByteArrayInputStream(bytes)));
//            ExcelParser.read(new ByteArrayInputStream(bytes), item -> System.out.println(JSON.toJSONString(item)));
        });
        IOUtils.closeQuietly(stream);
    }

    @RequestMapping(value ="",method = RequestMethod.GET)
    public String uploaderPage() {
        return "uploader";
    }

    @RequestMapping(value ="/uploadStatus",method = RequestMethod.GET)
    public String uploadStatus() {
        return "uploadStatus";
    }
}
