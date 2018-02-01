package com.zq.learn.fileuploader.service.impl;

import com.zq.learn.fileuploader.common.enums.FileExtension;
import com.zq.learn.fileuploader.exception.FileImportException;
import com.zq.learn.fileuploader.persistence.dao.FileImportInfoMapper;
import com.zq.learn.fileuploader.persistence.model.FileImportInfo;
import com.zq.learn.fileuploader.service.IFileImportService;
import com.zq.learn.fileuploader.support.batch.JobFactory;
import com.zq.learn.fileuploader.support.batch.JobNameFactory;
import com.zq.learn.fileuploader.utils.IdGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
@Service
public class FileImportServiceImpl implements IFileImportService {
    private static final String PARENT_DIR  = "youedata" + File.separator + "fileupload";
    private static final String TEMP_PATH  = System.getProperty("java.io.tmpdir") + PARENT_DIR;
    private static final char EXTENSION_SEPARATOR = '.';
    private static final FileExtension[] supportExtension = new FileExtension[]{FileExtension.CSV,FileExtension.EXCEL};

    private static final ConcurrentHashMap<String, JobExecution> jobExecutionMap = new ConcurrentHashMap<>();

    @Autowired
    private FileImportInfoMapper fileImportInfoMapper;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobFactory jobFactory;

    @Override
    public String importFile(String groupKey,String tableName, String fileName, InputStream inputStream) throws FileImportException {
        assertFileSupport(fileName);

        String fileKey = IdGenerator.getId();
        String savedFileName = StringUtils.stripFilenameExtension(fileName) + "_" +
                IdGenerator.getId() + EXTENSION_SEPARATOR + StringUtils.getFilenameExtension(fileName);

        try {
            String fullPath = saveFile(savedFileName, inputStream);
            Long jobInstanceId = runImportJob(tableName,fileKey,fullPath);

            saveImportInfo(new FileImportInfoBuilder()
                    .fileKey(fileKey)
                    .fileName(fileName)
                    .filePath(fullPath)
                    .jobInstanceId(jobInstanceId)
                    .tableName(tableName)
                    .uploadKey(groupKey)
                    .build());
        } catch (IOException e) {
            throw new FileImportException(e.getMessage(),e);
        }
        return fileKey;
    }

    /**
     * 保存导入信息
     * @param fileImportInfo
     */
    private void saveImportInfo(FileImportInfo fileImportInfo) {
        fileImportInfoMapper.insert(fileImportInfo);
    }

    private void assertFileSupport(String fileName) throws FileImportException {
        String extension = StringUtils.getFilenameExtension(fileName);
        FileExtension fileExtension = FileExtension.getBy(extension);
        if(!ArrayUtils.contains(supportExtension, fileExtension)){
            throw new FileImportException(String.format("不支持的文件类型(%s),暂时仅支持 %s", extension, Arrays.toString(supportExtension)));
        }
    }

    /**
     * 文件流持久化
     * @param fileName
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String saveFile(String fileName, InputStream inputStream) throws IOException {
        String destPath = TEMP_PATH + File.separator + fileName;
        FileUtils.copyInputStreamToFile(inputStream,new File(destPath));

        return destPath;
    }

    private Long runImportJob(String tableName, String fileKey, String fullPath) throws FileImportException {
        Map<String, JobParameter> parameters = new HashMap<>();

        JobParameter parameter = new JobParameter("file:///" + fullPath);
        JobParameter tableParam = new JobParameter(tableName);
        JobParameter dateParam = new JobParameter(new Date());

        parameters.put("resource", parameter);
        parameters.put("tableName", tableParam);
        parameters.put("date", dateParam);

        String extension = StringUtils.getFilenameExtension(fullPath);
        Job job = getJob(extension);
        if (job == null) {
            throw new FileImportException(String.format("不支持的文件类型(%s),暂时仅支持 %s", extension, Arrays.toString(supportExtension)));
        }

        try {
            JobExecution jobExecution = jobLauncher.run(job, new JobParameters(parameters));
            jobExecutionMap.put(fileKey, jobExecution);

            return jobExecution.getJobInstance().getInstanceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Job getJob(String extension) {
        FileExtension fileExtension = FileExtension.getBy(extension);
        if (fileExtension == null) {
            return null;
        }

        return jobFactory.getJob(JobNameFactory.getJobName(fileExtension));
    }

    @Override
    public Map<String, FileProcessResult> getFilesProcessResult(String groupKey) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println(File.separator);
    }
}
