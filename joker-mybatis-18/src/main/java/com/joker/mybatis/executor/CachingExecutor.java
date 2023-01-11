package com.joker.mybatis.executor;

import com.alibaba.fastjson.JSON;
import com.joker.mybatis.cache.Cache;
import com.joker.mybatis.cache.CacheKey;
import com.joker.mybatis.cache.TransactionalCacheManager;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.ResultHandler;
import com.joker.mybatis.session.RowBounds;
import com.joker.mybatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * 二级缓存执行器:支持二级缓存的 Executor 的实现类。
 * 缓存执行器是一个装饰器模式，将SimpleExecutor做一层包装，提供缓存的能力。
 * 因为这样的包装后就可以将SimpleExecutor中的一级缓存以及相应的能力进行使用，
 * 在二级缓存CachingExecutor执行器中完成缓存在会话周期内的流转操作。
 * </p>
 * Caching Executor实现类中主要注意的点是会话中数据查询时的缓存使用，在query方法中执行的delegate.<E>query操作。
 * 其实这个delegate就是SimpleExecutor实例化的对象，当缓存数据随着会话周期处理完后，
 * 则存放到MappedStatement所提供的Cache缓存队列中，也就是本章节所实现的FiflCache先进先出缓存实现类。
 * 另外关于缓存的流转会调用TransactionalCacheManager事务缓存管理器进行操作，
 * 从会话作用域范围，通过会话的结束，刷新提交到二级缓存或者清空处理。
 *
 * @author jokerzzccc
 * @date 2023/1/10
 */
public class CachingExecutor implements Executor {

    private Logger logger = LoggerFactory.getLogger(CachingExecutor.class);

    /**
     * 被委托的 Executor 对象
     */
    private final Executor delegate;
    /**
     * TransactionalCacheManager 对象
     */
    private final TransactionalCacheManager tcm = new TransactionalCacheManager();

    public CachingExecutor(Executor delegate) {
        this.delegate = delegate;
        // 设置 delegate 被当前执行器所包装
        delegate.setExecutorWrapper(this);
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        return delegate.update(ms, parameter);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        Cache cache = ms.getCache();
        if (cache != null) {
            flushCacheIfRequired(ms);
            if (ms.isUseCache() && resultHandler == null) {
                @SuppressWarnings("unchecked")
                List<E> list = (List<E>) tcm.getObject(cache, key);
                if (list == null) {
                    list = delegate.<E>query(ms, parameter, rowBounds, resultHandler, key, boundSql);
                    // cache：缓存队列实现类，FIFO
                    // key：哈希值 [mappedStatementId + offset + limit + SQL + queryParams + environment]
                    // list：查询的数据
                    tcm.putObject(cache, key, list);
                }
                // 打印调试日志，记录二级缓存获取数据
                if (logger.isDebugEnabled() && cache.getSize() > 0) {
                    logger.debug("二级缓存：{}", JSON.toJSONString(list));
                }
                return list;
            }
        }
        return delegate.<E>query(ms, parameter, rowBounds, resultHandler, key, boundSql);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        // 1. 获取绑定SQL
        BoundSql boundSql = ms.getBoundSql(parameter);
        // 2. 创建缓存Key
        CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
        return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
    }

    @Override
    public Transaction getTransaction() {
        return delegate.getTransaction();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        delegate.commit(required);
        tcm.commit();
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        try {
            delegate.rollback(required);
        } finally {
            if (required) {
                tcm.rollback();
            }
        }
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            if (forceRollback) {
                tcm.rollback();
            } else {
                tcm.commit();
            }
        } finally {
            delegate.close(forceRollback);
        }
    }

    @Override
    public void clearLocalCache() {
        delegate.clearLocalCache();
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        throw new UnsupportedOperationException("This method should not be called");
    }

    /**
     * 如果需要清空缓存，则进行清空。
     */
    private void flushCacheIfRequired(MappedStatement ms) {
        Cache cache = ms.getCache();
        if (cache != null && ms.isFlushCacheRequired()) {
            tcm.clear(cache);
        }
    }

}
