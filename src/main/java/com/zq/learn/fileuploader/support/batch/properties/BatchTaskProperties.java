package com.zq.learn.fileuploader.support.batch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * guns项目配置
 *
 * @author stylefeng
 * @Date 2017/5/23 22:31
 */
@Component
@ConfigurationProperties(prefix = BatchTaskProperties.PREFIX)
public class BatchTaskProperties {

    public static final String PREFIX = "batch.thread";
    
    private int coreSize;

    private int maxSize;

	public int getCoreSize() {
		return coreSize;
	}

	public void setCoreSize(int coreSize) {
		this.coreSize = coreSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
}
