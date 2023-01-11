package com.joker.mybatis.cache;

import com.joker.mybatis.cache.decorators.TransactionalCache;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * TransactionalCache 管理器:
 * 事务缓存管理器是对事务缓存的包装操作，用于在缓存执行器创建期间实例化，
 * 包装执行期内的所有事务缓存操作，做批量的提交和回滚时缓存数据刷新的处理。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/10
 */
public class TransactionalCacheManager {

    /**
     * Cache 和 TransactionalCache 的映射
     */
    private Map<Cache, TransactionalCache> transactionalCaches = new HashMap<>();

    public void clear(Cache cache) {
        getTransactionalCache(cache).clear();
    }

    /**
     * 得到某个 TransactionalCache 的值
     */
    public Object getObject(Cache cache, CacheKey key) {
        // 首先，获得 Cache 对应的 TransactionalCache 对象
        // 然后从 TransactionalCache 对象中，获得 key 对应的值
        return getTransactionalCache(cache).getObject(key);
    }

    public void putObject(Cache cache, CacheKey key, Object value) {
        // 首先，获得 Cache 对应的 TransactionalCache 对象
        // 然后，添加 KV 到 TransactionalCache 对象中
        getTransactionalCache(cache).putObject(key, value);
    }

    /**
     * 提交所有 TransactionalCache, 提交时全部提交:
     * TransactionalCache 存储的当前事务的缓存，会同步到其对应的 Cache 对象
     */
    public void commit() {
        for (TransactionalCache txCache : transactionalCaches.values()) {
            txCache.commit();
        }
    }

    /**
     * 回滚所有 TransactionalCache
     * 回滚时全部回滚
     */
    public void rollback() {
        for (TransactionalCache txCache : transactionalCaches.values()) {
            txCache.rollback();
        }
    }

    private TransactionalCache getTransactionalCache(Cache cache) {
        TransactionalCache txCache = transactionalCaches.get(cache);
        if (txCache == null) {
            txCache = new TransactionalCache(cache);
            transactionalCaches.put(cache, txCache);
        }
        return txCache;
    }

}
