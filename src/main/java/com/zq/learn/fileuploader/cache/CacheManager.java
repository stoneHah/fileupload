package com.zq.learn.fileuploader.cache;

/**
 * 缓存管理
 * @author ibm
 *
 */
public interface CacheManager {
	/**
	 * 添加数据
	 * @param key
	 * @param value
	 */
	void addData(String key, Object value);
	
	/**
	 * 获取数据
	 * @param key
	 * @return
	 */
	Object getData(String key);
	
	/**
	 * 
	 * @param key
	 */
	void evict(String key);
}
