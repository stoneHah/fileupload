package com.zq.learn.fileuploader.service.impl;

import com.zq.learn.fileuploader.persistence.model.FileTableInfo;
import com.zq.learn.fileuploader.service.IFileImportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/3/1
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileImportServiceImplTest {

    @Autowired
    private IFileImportService fileImportService;

    @Test
    public void saveFileTableInfo() throws Exception {
        FileTableInfo tableInfo = new FileTableInfo("employee");
        tableInfo.setTableTitle("a,b,c");
        fileImportService.saveFileTableInfo(tableInfo);
    }

}