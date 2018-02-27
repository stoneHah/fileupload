package com.zq.learn.fileuploader.cache;

/**
 * 可以存放指定超时时间的 缓存管理
 * @author ibm
 *
 */
public interface ExpireableCacheManager extends CacheManager{
	
//	void addData(String key,Object value,long expireTime,TimeUnit timeUnit);
	/**
	 * 添加数据
	 * @param key
	 * @param value
	 * @param expireTime 超时时长(单位:秒数)
	 */
	void addData(String key, Object value, int expireTime);
	
	/**
	 * 判断key是否已经超时
	 * @param key
	 * @return
	 */
	boolean isExpired(String key);
	
	/**
	 * 判断key是否将要超时
	 * @param key
	 * @return
	 */
	boolean willExpire(String key);
}
