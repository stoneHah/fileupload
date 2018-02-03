package com.zq.learn.fileuploader.controller;

import com.zq.learn.fileuploader.controller.dto.FileUploadResult;
import com.zq.learn.fileuploader.controller.dto.Response;
import com.zq.learn.fileuploader.service.IFileImportService;
import com.zq.learn.fileuploader.service.IFileImportService.GroupFileProcessResult;
import com.zq.learn.fileuploader.utils.IdGenerator;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
    private IFileImportService fileImportService;

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String upload(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            Map<String, String> fileNameKeyMap = new HashMap<>();
            Map<String, String> fileNameErrorMap = new HashMap<>();
            String groupKey = IdGenerator.getId();
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload();

            // Parse the request
            FileItemIterator iter = upload.getItemIterator(request);
            String tableName = null;
            while (iter.hasNext()) {
                FileItemStream item = iter.next();

                InputStream stream = item.openStream();
                try {
                    if (!item.isFormField()) {
                        String filename = item.getName();

                        //导入文件
                        String fileKey = null;
                        try {
                            fileKey = fileImportService.importFile(groupKey, tableName, filename, stream);
                            fileNameKeyMap.put(filename, fileKey);
                        } catch (Exception e) {
                            fileNameErrorMap.put(filename, e.getMessage());
                            e.printStackTrace();
                        }
                    }else{
                        String fieldName = item.getFieldName();
                        if ("table".equals(fieldName)) {
                            tableName = Streams.asString(stream);
                        }
                    }
                }  finally {
                    IOUtils.closeQuietly(stream);
                }
            }

            FileUploadResult result = new FileUploadResult();
            result.setNormalFiles(fileNameKeyMap);
            result.setExceptionFiles(fileNameErrorMap);
            result.setGroup(groupKey);

            redirectAttributes.addFlashAttribute("res",
                    Response.ok(result));
        } catch (FileUploadException e) {
            redirectAttributes.addFlashAttribute("res",
                    Response.error(Response.CODE_ERROR,"文件上传失败"));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("res",
                    Response.error(Response.CODE_ERROR,"文件上传失败"));
        }

        return "redirect:/uploadStatus";
    }

    @RequestMapping(value ="",method = RequestMethod.GET)
    public String uploaderPage() {
        return "uploader";
    }

    @RequestMapping(value ="/uploadStatus",method = RequestMethod.GET)
    public String uploadStatus() {
        return "uploadStatus";
    }

    @RequestMapping(value = "/uploadProgress/{groupKey}",method = RequestMethod.GET)
    @ResponseBody
    public GroupFileProcessResult getUploadProgress(@PathVariable("groupKey") String groupKey){
        return fileImportService.getFilesProcessResult(groupKey);
    }

}
