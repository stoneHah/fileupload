package com.zq.learn.fileuploader.controller;

import com.zq.learn.fileuploader.support.fileparser.BatchParser;
import com.zq.learn.fileuploader.support.fileparser.BatchParser.FileData;
import com.zq.learn.fileuploader.support.fileparser.Decompressor;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private BatchParser batchParser;

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

                    //处理文件流
                    dealWithFileStream(filename,stream);
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
     * 处理文件流
     * @param filename
     * @param stream
     */
    private void dealWithFileStream(String filename, InputStream stream) throws FileUploadException {
        if (isExcel(filename) || isCsv(filename)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                IOUtils.copy(stream, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            batchParser.parse(new FileData(filename,filename,new ByteArrayInputStream(out.toByteArray())));
        } else if (isCompressFile(filename)) {
            parseCompressFile(filename,stream);
        }else{
            throw new FileUploadException("不支持的文件类型");
        }
    }

    /**
     * 解析压缩文件
     * @param stream
     */
    private void parseCompressFile(String compressFilename, InputStream stream) {
        // Process the input stream
        Decompressor.unZip(stream, (fileName, bytes) -> {
            System.out.println("parse file" + fileName);
            if (isExcel(fileName) || isCsv(fileName)) {
                batchParser.parse(new FileData(compressFilename,fileName,new ByteArrayInputStream(bytes)));
            }
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

    private boolean isCompressFile(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        return "zip".equalsIgnoreCase(extension);
    }

    private boolean isCsv(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        return "csv".equalsIgnoreCase(extension);
    }

    private boolean isExcel(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        return "xls".equalsIgnoreCase(extension) ||
                "xlsx".equalsIgnoreCase(extension);
    }
}
