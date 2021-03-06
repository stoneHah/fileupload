package com.zq.learn.fileuploader.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zq.learn.fileuploader.common.enums.FileExtension;
import com.zq.learn.fileuploader.common.enums.JobStatus;
import com.zq.learn.fileuploader.controller.dto.FileImportContext;
import com.zq.learn.fileuploader.exception.FileImportException;
import com.zq.learn.fileuploader.persistence.dao.FileImportExecutionMapper;
import com.zq.learn.fileuploader.persistence.dao.FileImportInfoMapper;
import com.zq.learn.fileuploader.persistence.dao.FileTableInfoMapper;
import com.zq.learn.fileuploader.persistence.model.FileImportExecution;
import com.zq.learn.fileuploader.persistence.model.FileImportInfo;
import com.zq.learn.fileuploader.persistence.model.FileTableInfo;
import com.zq.learn.fileuploader.service.IFileImportService;
import com.zq.learn.fileuploader.service.model.FileImportInfoSupport;
import com.zq.learn.fileuploader.support.batch.FilterDataManager;
import com.zq.learn.fileuploader.support.batch.JobFactory;
import com.zq.learn.fileuploader.support.batch.JobNameFactory;
import com.zq.learn.fileuploader.support.batch.Keys;
import com.zq.learn.fileuploader.support.batch.model.BatchExceptionInfo;
import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import com.zq.learn.fileuploader.utils.IdGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
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
    private FileImportExecutionMapper fileImportExecutionMapper;

    @Autowired
    private FilterDataManager filterDataManager;

    @Autowired
    private FileTableInfoMapper fileTableInfoMapper;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobFactory jobFactory;

    @Override
    public String importFile(String groupKey, String tableName, String fileName, InputStream inputStream, FileImportContext fileImportContext) throws FileImportException {
        assertFileSupport(fileName);

        String fileKey = IdGenerator.getId();
        String savedFileName = StringUtils.stripFilenameExtension(fileName) + "_" +
                IdGenerator.getId() + EXTENSION_SEPARATOR + StringUtils.getFilenameExtension(fileName);

        try {
            String fullPath = saveFile(savedFileName, inputStream);
            Long jobInstanceId = runImportJob(fileImportContext,tableName, fileKey, fullPath);

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

    private Long runImportJob(FileImportContext fileImportContext, String tableName, String fileKey, String fullPath) throws FileImportException {
        Map<String, JobParameter> parameters = new HashMap<>();

        JobParameter parameter = new JobParameter("file:///" + fullPath);
        JobParameter tableParam = new JobParameter(tableName);
        JobParameter fileKeyParam = new JobParameter(fileKey);
        JobParameter dateParam = new JobParameter(new Date());

        parameters.put("fileKey", fileKeyParam);
        parameters.put("resource", parameter);
        parameters.put("tableName", tableParam);
        parameters.put("date", dateParam);

        if (fileImportContext != null) {
            if (fileImportContext.isValidateIDCard()) {
                JobParameter idCardParam = new JobParameter(fileImportContext.getIdcardColumns());
                parameters.put(Keys.ID_CARD_COLUMNS,idCardParam);
            }
        }

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

            if (fileProcessResult.getStatus() == JobStatus.Starting) {
                allComplete = false;
            }else{
                if(maxTimeConsume == null || (fileProcessResult.getTimeConsume() != null && maxTimeConsume < fileProcessResult.getTimeConsume())){
                    maxTimeConsume = fileProcessResult.getTimeConsume();
                }
            }
        }

        result.setFilesProcessResult(map);
        if (allComplete) {
            result.setStatus(JobStatus.Complete);
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
        ExecutionContext executionContext = stepExecution.getExecutionContext();

        int errorCount = 0;
        if(executionContext.containsKey(BatchExceptionInfo.WRITE_BATCH_EXCEPTION)){
            BatchExceptionInfo batchExceptionInfo = (BatchExceptionInfo) executionContext.get(BatchExceptionInfo.WRITE_BATCH_EXCEPTION);
            if (!batchExceptionInfo.isEmpty()) {
                errorCount = batchExceptionInfo.getExceptionCount();

                result.setError(true);
                result.setErrorMsgList(batchExceptionInfo.getExceptionMessageList());
            }
        }

        int readCount = stepExecution.getReadCount();
        result.setReadCount(readCount);
        int filterCount = filterDataManager.countFilterData(fileKey);
        result.setFilterCount(filterCount > readCount ? readCount : filterCount);
        result.setWriteCount(stepExecution.getWriteCount() - errorCount);
        result.setStatus(JobStatus.parse(stepExecution.getStatus()));

        Date curTime = stepExecution.getEndTime() != null ? stepExecution.getEndTime() : new Date();
        result.setTimeConsume(curTime.getTime() - stepExecution.getStartTime().getTime());

        if (result.getFilterCount() > 0) {
            result.setFilterRecords(filterDataManager.moreFilterData(fileKey));
        }

        return result;
    }

    @Override
    public List<FileImportInfoSupport> getFileImportInfos(String fileName,String fileDesc, Date startTime, Date endTime, Page page) {
        List<FileImportInfoSupport> result = null;

        if (endTime != null) {
            endTime = new DateTime(endTime).plusDays(1).toDate();
        }
        List<FileImportInfo> fileImportInfos = fileImportInfoMapper.getFileImportInfos(fileName,fileDesc, startTime,endTime,page);
        if (!CollectionUtils.isEmpty(fileImportInfos)) {
            result = new ArrayList<>();

            Set<String> fileKeys = new HashSet<>();
            for (FileImportInfo fileImportInfo : fileImportInfos) {
                result.add(convert(fileImportInfo));
                fileKeys.add(fileImportInfo.getFileKey());
            }

            List<FileImportExecution> executionList = fileImportExecutionMapper.getFileImportExecutions(fileKeys.toArray(new String[fileKeys.size()]));
            fillExecutionData(result,executionList);
        }

        return result == null ? new ArrayList<>() : result;
    }

    private void fillExecutionData(List<FileImportInfoSupport> list, List<FileImportExecution> executionList) {
        if (CollectionUtils.isEmpty(executionList)) {
            return;
        }

        Map<String, FileImportExecution> map = new HashMap<>();
        for (FileImportExecution execution : executionList) {
            map.put(execution.getFileKey(), execution);
        }

        for (FileImportInfoSupport infoSupport : list) {
            String fileKey = infoSupport.getFileKey();
            FileImportExecution execution = map.get(fileKey);
            if (execution != null) {
                infoSupport.setJobStartTime(execution.getJobStartTime());
                infoSupport.setJobEndTime(execution.getJobEndTime());
                infoSupport.setReadCount(execution.getReadCount());
                infoSupport.setWriteCount(execution.getWriteCount());
                infoSupport.setFilterCount(execution.getFilterCount());
                infoSupport.setStatus(JobStatus.getByCode(execution.getStatus()).getDesc());
                infoSupport.setErrorMsg(execution.getFailedMessage());
            }
        }
    }

    private FileImportInfoSupport convert(FileImportInfo fileImportInfo) {
        FileImportInfoSupport support = new FileImportInfoSupport();

        support.setFileName(fileImportInfo.getFileName());
        support.setFileKey(fileImportInfo.getFileKey());
        support.setImportTime(fileImportInfo.getUpdateTime());
        support.setTableName(fileImportInfo.getTableName());

        FileTableInfo tableInfo = fileImportInfo.getTableInfo();
        if (tableInfo != null) {
            support.setTableDesc(tableInfo.getTableDesc());
        }
        return support;
    }

    @Override
    public void saveFileTableInfo(FileTableInfo fileTableInfo) {
        String tableName = fileTableInfo.getTableName();
        if (!StringUtils.hasText(tableName)) {
            return;
        }

        FileTableInfo tableInfo = fileTableInfoMapper.selectOne(new FileTableInfo(tableName));
        if (tableInfo == null) {
            try {
                fileTableInfoMapper.insert(fileTableInfo);
            } catch (Exception e) {
                updateFileTableInfo(fileTableInfo);
            }
        }else{
            updateFileTableInfo(fileTableInfo);
        }
    }

    private void updateFileTableInfo(FileTableInfo fileTableInfo) {
        if (fileTableInfo.getId() == null) {
            FileTableInfo tableInfo = fileTableInfoMapper.selectOne(new FileTableInfo(fileTableInfo.getTableName()));
            if (tableInfo == null) {
                logger.error("文件表信息更新失败，不存在相关记录[tableName={}]",fileTableInfo.getTableName());
                return;
            }

            fileTableInfo.setId(tableInfo.getId());
            fileTableInfo.setVersion(tableInfo.getVersion());
        }

        int count = 0;
        for(;;){

            Integer updateCount = fileTableInfoMapper.updateById(fileTableInfo);
            if (updateCount > 0) {
                break;
            }

            if(count > 3){
                logger.error("文件表信息更新失败[tableName={}]",fileTableInfo.getTableName());
                return;
            }

            count++;
        }
    }

    public static void main(String[] args) {
        System.out.println(File.separator);
    }
}
