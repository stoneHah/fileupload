package com.zq.learn.fileuploader.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.zq.learn.fileuploader.config.properties.DruidProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置
 *
 * @author stylefeng
 * @Date 2017/5/20 21:58
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Autowired
    DruidProperties druidProperties;

    /**
     * guns的数据源
     */
    @Bean
    public DruidDataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        druidProperties.config(dataSource);
        return dataSource;
    }

}
