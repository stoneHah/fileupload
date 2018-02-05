package com.zq.learn.fileuploader.service.impl;

import com.zq.learn.fileuploader.common.enums.FileExtension;
import com.zq.learn.fileuploader.exception.FileImportException;
import com.zq.learn.fileuploader.persistence.dao.FileImportInfoMapper;
import com.zq.learn.fileuploader.persistence.model.FileImportInfo;
import com.zq.learn.fileuploader.service.IFileImportService;
import com.zq.learn.fileuploader.support.batch.JobFactory;
import com.zq.learn.fileuploader.support.batch.JobNameFactory;
import com.zq.learn.fileuploader.support.batch.Keys;
import com.zq.learn.fileuploader.utils.IdGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/31
 **/
@Service
public class FileImportServiceImpl implements IFileImportService {
    private static final Logger logger = LoggerFactory.getLogger(FileImportServiceImpl.class);

    private static final String PARENT_DIR = "youedata" + File.separator + "fileupload";
    private static final String TEMP_PATH = System.getProperty("java.io.tmpdir") + PARENT_DIR;
    private static final char EXTENSION_SEPARATOR = '.';
    private static final FileExtension[] supportExtension = new FileExtension[]{
            FileExtension.Csv, FileExtension.Excel_XLS,FileExtension.Excel_XLSX};

    private static final ConcurrentHashMap<String, JobExecution> jobExecutionMap = new ConcurrentHashMap<>();
    private static final MultiValueMap<String, String> groupFileKeyMap = new LinkedMultiValueMap<>();

    @Autowired
    private FileImportInfoMapper fileImportInfoMapper;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobFactory jobFactory;

    @Override
    public String importFile(String groupKey, String tableName, String fileName, InputStream inputStream) throws FileImportException {
        assertFileSupport(fileName);

        String fileKey = IdGenerator.getId();
        String savedFileName = StringUtils.stripFilenameExtension(fileName) + "_" +
                IdGenerator.getId() + EXTENSION_SEPARATOR + StringUtils.getFilenameExtension(fileName);

        try {
            String fullPath = saveFile(savedFileName, inputStream);
            Long jobInstanceId = runImportJob(tableName, fileKey, fullPath);

            saveImportInfo(new FileImportInfoBuilder()
                    .fileKey(fileKey)
                    .fileName(fileName)
                    .filePath(fullPath)
                    .jobInstanceId(jobInstanceId)
                    .tableName(tableName)
                    .uploadKey(groupKey)
                    .build());

            groupFileKeyMap.add(groupKey, fileKey);
        } catch (IOException e) {
            throw new FileImportException(e.getMessage(), e);
        }
        return fileKey;
    }

    /**
     * 保存导入信息
     *
     * @param fileImportInfo
     */
    private void saveImportInfo(FileImportInfo fileImportInfo) {
        fileImportInfoMapper.insert(fileImportInfo);
    }

    private void assertFileSupport(String fileName) throws FileImportException {
        String extension = StringUtils.getFilenameExtension(fileName);
        FileExtension fileExtension = FileExtension.getBy(extension);
        if (!ArrayUtils.contains(supportExtension, fileExtension)) {
            throw new FileImportException(String.format("不支持的文件类型(%s),暂时仅支持 %s", extension, Arrays.toString(supportExtension)));
        }
    }

    /**
     * 文件流持久化
     *
     * @param fileName
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String saveFile(String fileName, InputStream inputStream) throws IOException {
        String destPath = TEMP_PATH + File.separator + fileName;
        FileUtils.copyInputStreamToFile(inputStream, new File(destPath));

        return destPath;
    }

    private Long runImportJob(String tableName, String fileKey, String fullPath) throws FileImportException {
        Map<String, JobParameter> parameters = new HashMap<>();

        JobParameter parameter = new JobParameter("file:///" + fullPath);
        JobParameter tableParam = new JobParameter(tableName);
        JobParameter dateParam = new JobParameter(new Date());
        JobParameter fileKeyParam = new JobParameter(fileKey);

        parameters.put("fileKey", fileKeyParam);
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
    public GroupFileProcessResult getFilesProcessResult(String groupKey) {
        List<String> fileKeys = groupFileKeyMap.get(groupKey);
        if (CollectionUtils.isEmpty(fileKeys)) {
            return GroupFileProcessResult.EMPTY;
        }

        GroupFileProcessResult result = new GroupFileProcessResult();
        boolean allComplete = true;
        Map<String, FileProcessResult> map = new HashMap<>();
        Long maxTimeConsume = null;
        for (String fileKey : fileKeys) {
            FileProcessResult fileProcessResult = getFileProcessResult(fileKey);
            map.put(fileKey, fileProcessResult);

            if (fileProcessResult.getStatus() != BatchStatus.COMPLETED) {
                allComplete = false;
            }else{
                if(maxTimeConsume == null || maxTimeConsume < fileProcessResult.getTimeConsume()){
                    maxTimeConsume = fileProcessResult.getTimeConsume();
                }
            }
        }

        result.setFilesProcessResult(map);
        if (allComplete) {
            result.setStatus(BatchStatus.COMPLETED);
            result.setTimeConsume(maxTimeConsume);
        }

        return result;
    }

    /**
     * @param fileKey
     * @return
     */
    private FileProcessResult getFileProcessResult(String fileKey) {
        JobExecution jobExecution = jobExecutionMap.get(fileKey);
        if (jobExecution == null) {
            return FileProcessResult.EMPTY;
        }

        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        if (stepExecutions.isEmpty()) {
            logger.info("job还未启动");
            return FileProcessResult.EMPTY;
        }

        FileProcessResult result = new FileProcessResult();

        StepExecution stepExecution = stepExecutions.iterator().next();
        result.setReadCount(stepExecution.getReadCount());
        result.setWriteCount(stepExecution.getWriteCount());
        result.setStatus(stepExecution.getStatus());

        if (stepExecution.getEndTime() != null) {
            result.setTimeConsume(stepExecution.getEndTime().getTime() - stepExecution.getStartTime().getTime());
            result.setStatus(BatchStatus.COMPLETED);
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(File.separator);
    }
}
