package com.zq.learn.fileuploader.cache;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 基于内存的缓存管理
 * @author ibm
 *
 */
@Component
public class InMemoryExpireableCacheManager implements ExpireableCacheManager{
	
	private static final ConcurrentHashMap<String, ExpireWrapper> cacheMap = new ConcurrentHashMap<>();

	@Override
	public void addData(String key, Object value) {
		clearExpireData();
		
		cacheMap.put(key, new UnExpireWrapper(value));
	}
	
	@Override
	public void addData(String key, Object value, int expireTime) {
		clearExpireData();
		
		cacheMap.put(key, new ExpireWrapper(value,expireTime));
	}

	@Override
	public Object getData(String key) {
		clearExpireData();
		
		ExpireWrapper wrapper = cacheMap.get(key);
		if(wrapper != null){
			if(wrapper.isExpired()){
				return null;
			}
			return wrapper.getObj();
		}
		
		return null;
	}
	
	@Override
	public void evict(String key) {
		if(cacheMap.containsKey(key)){
			cacheMap.remove(key);
		}
	}

	@Override
	public boolean isExpired(String key) {
		clearExpireData();
		
		ExpireWrapper wrapper = cacheMap.get(key);
		if(wrapper == null){
			return true;
		}
		return wrapper.isExpired();
	}

	@Override
	public boolean willExpire(String key) {
		clearExpireData();
		
		ExpireWrapper wrapper = cacheMap.get(key);
		if(wrapper == null){
			return true;
		}
		return wrapper.willExpire();
	}
	
	private void clearExpireData(){
		Enumeration<String> keys = cacheMap.keys();
		
		if(keys.hasMoreElements()){
			String key = keys.nextElement();
			ExpireWrapper wrapper = cacheMap.get(key);
			if(wrapper != null && wrapper.isExpired()){
				cacheMap.remove(key);
			}
		}
	}
	
	private static class ExpireWrapper{
		
		/**
		 * 
		 */
		private static final double threshold = 0.95;
		
		/**
		 * 包装的对象
		 */
		private Object obj;
		
		/**
		 * 最后一次更新时长
		 */
		private DateTime createTime;
		
		/**
		 * 超时时长(秒数)
		 */
		private int expireSec;
		
		public ExpireWrapper(Object obj, int expireSec) {
			super();
			this.obj = obj;
			this.expireSec = expireSec;
			this.createTime = new DateTime();
		}

		public Object getObj() {
			return obj;
		}

		/**
		 * 判断对象是否已经过期
		 * @return
		 */
		public boolean isExpired(){
			return expired(expireSec);
		}
		
		/**
		 * 判断对象是否将要过期
		 * @return
		 */
		public boolean willExpire(){
			return expired((int)(expireSec * threshold));
		}
		
		private boolean expired(int sec) {
			if(createTime.plusSeconds(sec).isBeforeNow()){
				return true;
			}
			return false;
		}
	}
	
	public static class UnExpireWrapper extends ExpireWrapper{

		public UnExpireWrapper(Object obj) {
			super(obj, -1);
		}
		
		@Override
		public boolean isExpired() {
			return false;
		}
		
		@Override
		public boolean willExpire() {
			return false;
		}
		
	}

}
