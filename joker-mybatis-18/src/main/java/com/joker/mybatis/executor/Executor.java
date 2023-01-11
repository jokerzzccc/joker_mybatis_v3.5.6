package com.joker.mybatis.executor;

import com.joker.mybatis.cache.CacheKey;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.ResultHandler;
import com.joker.mybatis.session.RowBounds;
import com.joker.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * 执行器入口: 确定出事务和操作和 SQL 执行的统一标准接口。
 * </p>
 * 在执行器中定义的接口包括事务相关的处理方法和执行SQL查询的操作，随着后续功能的迭代还会继续补充其他的方法。
 *
 * @author jokerzzccc
 * @date 2022/10/22
 */
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    /**
     * 更新 or 插入 or 删除，由传入的 MappedStatement 的 SQL 所决定
     *
     * @param ms
     * @param parameter
     * @return
     * @throws SQLException
     */
    int update(MappedStatement ms, Object parameter) throws SQLException;

    /**
     * 查询，含缓存，带 ResultHandler + CacheKey + BoundSql
     */
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException;

    /**
     * 查询，带 ResultHandler
     */
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);

    /**
     * 清理本地 Session 缓存
     */
    void clearLocalCache();

    /**
     * 创建 CacheKey：
     * CacheKey 的创建，需要依赖于；mappedStatementld+offset+limit+
     * SQL+queryParams+environment信息构建出一个哈希值
     */
    CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

    /**
     * 设置包装的 Executor 对象
     *
     * @param executor
     */
    void setExecutorWrapper(Executor executor);

}
