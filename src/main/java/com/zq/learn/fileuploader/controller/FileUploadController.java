package com.zq.learn.fileuploader.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.zq.learn.fileuploader.controller.dto.FileImportContext;
import com.zq.learn.fileuploader.controller.dto.FileUploadResult;
import com.zq.learn.fileuploader.controller.dto.ListResponse;
import com.zq.learn.fileuploader.controller.dto.Response;
import com.zq.learn.fileuploader.persistence.model.FileTableInfo;
import com.zq.learn.fileuploader.service.IFileImportService;
import com.zq.learn.fileuploader.service.IFileImportService.GroupFileProcessResult;
import com.zq.learn.fileuploader.service.model.FileImportInfoSupport;
import com.zq.learn.fileuploader.utils.IdGenerator;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
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
    private static final String FORM_TABLE_FIELD = "table";
    private static final String FORM_TABLE_DESC_FIELD = "tableDesc";
    private static final String FORM_VALIDATE_IDCARD_FIELD = "validateIDCard";
    private static final String FORM_IDCARD_COLUMNS_FIELD = "idcardColumns";

    @Autowired
    private IFileImportService fileImportService;

    /**
     * 支持table、validateIDCard、idcardColumns
     * @param request
     * @param redirectAttributes
     * @return
     */
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
            String tableDesc = null;
            FileImportContext fileImportContext = new FileImportContext();
            while (iter.hasNext()) {
                FileItemStream item = iter.next();

                InputStream stream = item.openStream();
                try {
                    if (!item.isFormField()) {
                        String filename = item.getName();

                        //导入文件
                        String fileKey = null;
                        try {
                            fileKey = fileImportService.importFile(groupKey, tableName, filename, stream,fileImportContext);
                            fileNameKeyMap.put(filename, fileKey);
                        } catch (Exception e) {
                            fileNameErrorMap.put(filename, e.getMessage());
                            e.printStackTrace();
                        }
                    }else{
                        String fieldName = item.getFieldName();
                        String fieldValue = Streams.asString(stream);
                        if (FORM_TABLE_FIELD.equals(fieldName)) {
                            tableName = fieldValue;
                        }else if(FORM_TABLE_DESC_FIELD.equals(fieldName)){
                            tableDesc = fieldValue;
                        }else{
                            parseContextParam(fileImportContext,fieldName,fieldValue);
                        }
                    }
                }  finally {
                    IOUtils.closeQuietly(stream);
                }
            }

            fileImportService.saveFileTableInfo(new FileTableInfo(tableName,tableDesc));

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

    private void parseContextParam(FileImportContext fileImportContext, String fieldName, String fieldValue) {
        if (FORM_VALIDATE_IDCARD_FIELD.equals(fieldName)) {
            fileImportContext.setValidateIDCard(Boolean.valueOf(fieldValue));
        } else if (FORM_IDCARD_COLUMNS_FIELD.equals(fieldName)) {
            fileImportContext.setIdcardColumns(fieldValue);
        }
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

    /**
     * 获取文件导入信息
     * @param fileName
     * @param startTime
     * @param endTime
     * @param offset
     * @param pageSize
     * @return
     */
    @PostMapping("/fileImportInfos")
    @ResponseBody
    public ListResponse getFileImportInfos(@RequestParam(value = "fileName",required = false) String fileName,
                                           @RequestParam(value = "tableDesc",required = false) String tableDesc,
                                           @RequestParam(value = "startTime",required = false)@DateTimeFormat(pattern="yyyy-MM-dd") Date startTime,
                                           @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd")Date endTime,
                                           @RequestParam(value = "offset",required = false,defaultValue = "0") Integer offset,
                                           @RequestParam(value = "limit",required = false,defaultValue = "10") Integer pageSize){
        int pageNum = offset % pageSize == 0 ? offset / pageSize : (offset / pageSize + 1);
        Page page = new Page(pageNum + 1, pageSize);
        List<FileImportInfoSupport> fileImportInfos = fileImportService.getFileImportInfos(fileName,tableDesc, startTime, endTime, page);
        page.setRecords(fileImportInfos);

        return new ListResponse(page);
    }

    public static void main(String[] args) {
    }

}
