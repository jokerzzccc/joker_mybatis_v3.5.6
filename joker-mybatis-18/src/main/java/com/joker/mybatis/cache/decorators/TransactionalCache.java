package com.joker.mybatis.cache.decorators;

import com.joker.mybatis.cache.Cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The 2nd level cache transactional buffer.
 * <p>
 * This class holds all cache entries that are to be added to the 2nd level cache during a Session.
 * Entries are sent to the cache when commit is called or discarded if the Session is rolled back.
 * Blocking cache support has been added. Therefore any get() that returns a cache miss
 * will be followed by a put() so any lock associated with the key can be released.
 * <p>
 * TransactionalCache事务缓存提供了对一级缓存的数据存放和使用的操作，
 * 当一级缓存作用域范围的会话因为commit、.close结束，则会调用到 flushPendingEntries 方法，
 * 通过循环处理调用delegate.putObject(entry.getKey(),entry..getValue(O片把数据刷新到二级缓存队列中。
 * 另外rollback回滚方法则是一种清空缓存操作。
 * <p>
 * 实现 Cache 接口，支持事务的 Cache 实现类，主要用于二级缓存中。
 * TransactionalCache所保存的是会话期间内的缓存数据，当会话结束后则把缓存刷新到二级缓存中。如果是回滚操作则清空缓存。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/10
 */
public class TransactionalCache implements Cache {

    /**
     * 委托的 Cache 对象。
     * <p>
     * 实际上，就是二级缓存 Cache 对象。
     */
    private final Cache delegate;
    /**
     * commit 时要不要清缓存:
     * 提交时，清空 {@link #delegate}。
     * 初始时，该值为 false;
     * 清理后{@link #clear()} 时，该值为 true ，表示持续处于清空状态
     */
    private boolean clearOnCommit;
    /**
     * commit 时要添加的元素:
     * 待提交的 KV 映射
     */
    private final Map<Object, Object> entriesToAddOnCommit;
    /**
     * 查找不到的 KEY 集合
     */
    private final Set<Object> entriesMissedInCache;

    public TransactionalCache(Cache delegate) {
        // delegate = FifoCache
        this.delegate = delegate;
        // 默认 commit 时不清缓存
        this.clearOnCommit = false;
        this.entriesToAddOnCommit = new HashMap<>();
        this.entriesMissedInCache = new HashSet<>();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public Object getObject(Object key) {
        // key：CacheKey 拼装后的哈希码
        Object object = delegate.getObject(key);
        if (object == null) {
            entriesMissedInCache.add(key);
        }
        return clearOnCommit ? null : object;
    }

    @Override
    public void putObject(Object key, Object object) {
        entriesToAddOnCommit.put(key, object);
    }

    @Override
    public Object removeObject(Object key) {
        return null;
    }

    @Override
    public void clear() {
        clearOnCommit = true;
        entriesToAddOnCommit.clear();
    }

    /**
     * 提交事务
     */
    public void commit() {
        if (clearOnCommit) {
            delegate.clear();
        }
        flushPendingEntries();
        // 重置
        reset();
    }

    /**
     * 回滚事务
     */
    public void rollback() {
        // <1> 从 delegate 移除出 entriesMissedInCache
        unlockMissedEntries();
        // <2> 重置
        reset();
    }

    /**
     * 重构缓存到初始状态：
     * 因为，一个 Executor 可以提交多次事务，而 TransactionalCache 需要被重用，那么就需要重置回初始状态。
     */
    private void reset() {
        // 重置 clearOnCommit 为 false
        clearOnCommit = false;
        // 清空 entriesToAddOnCommit、entriesMissedInCache
        entriesToAddOnCommit.clear();
        entriesMissedInCache.clear();
    }

    /**
     * 将 entriesToAddOnCommit、entriesMissedInCache 刷入 delegate 中.
     * 刷新数据到 MappedStatement#Cache 中，也就是把数据填充到 Mapper XML 级别下。
     */
    private void flushPendingEntries() {
        // 将 entriesToAddOnCommit 刷入 delegate 中
        for (Map.Entry<Object, Object> entry : entriesToAddOnCommit.entrySet()) {
            delegate.putObject(entry.getKey(), entry.getValue());
        }
        // 将 entriesMissedInCache 刷入 delegate 中
        for (Object entry : entriesMissedInCache) {
            if (!entriesToAddOnCommit.containsKey(entry)) {
                delegate.putObject(entry, null);
            }
        }
    }

    /**
     * 将 entriesMissedInCache 同步到 delegate 中。
     */
    private void unlockMissedEntries() {
        for (Object entry : entriesMissedInCache) {
            delegate.putObject(entry, null);
        }
    }

}
