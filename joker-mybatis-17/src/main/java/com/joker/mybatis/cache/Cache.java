package com.joker.mybatis.cache;

/**
 * <p>
 * SPI(Service Provider Interface) for cache providers. 缓存接口:
 * 缓存容器接口。注意，它是一个容器，有点类似 HashMap ，可以往其中添加各种缓存。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/9
 */
public interface Cache {

    /**
     * 获取ID，每个缓存都有唯一ID标识
     *
     * @return The identifier of this cache
     */
    String getId();

    /**
     * 存入值
     *
     * @param key Can be any object but usually it is a {@link CacheKey}
     * @param value The result of a select.
     */
    void putObject(Object key, Object value);

    /**
     * 获取值
     *
     * @param key The key
     * @return The object stored in the cache.
     */
    Object getObject(Object key);

    /**
     * 删除值
     * As of 3.3.0 this method is only called during a rollback
     * for any previous value that was missing in the cache.
     * This lets any blocking cache to release the lock that
     * may have previously put on the key.
     * A blocking cache puts a lock when a value is null
     * and releases it when the value is back again.
     * This way other threads will wait for the value to be
     * available instead of hitting the database.
     *
     * @param key The key
     * @return Not used
     */
    Object removeObject(Object key);

    /**
     * 清空缓存
     * Clears this cache instance.
     */
    void clear();

    /**
     * 获得容器中缓存的数量
     * Optional. This method is not called by the core.
     *
     * @return The number of elements stored in the cache (not its capacity).
     */
    int getSize();

}
