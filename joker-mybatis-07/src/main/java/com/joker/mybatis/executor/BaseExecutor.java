package com.joker.mybatis.executor;

import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.ResultHandler;
import com.joker.mybatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * 执行器抽象基类:
 * 在抽象基类中封装了执行器的全部接口，这样具体的子类继承抽象类后，就不用在处理这些共性的方法。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/22
 */
public abstract class BaseExecutor implements Executor{

    private Logger logger = LoggerFactory.getLogger(BaseExecutor.class);

    protected Configuration configuration;
    protected Transaction transaction;
    protected Executor wrapper;

    private boolean closed;

    protected BaseExecutor(Configuration configuration, Transaction transaction){
        this.configuration = configuration;
        this.transaction = transaction;
        this.wrapper = this;
    }

    /**
     * 在 query 查询方法中，封装一些必要的流程处理，如果检测关闭等，
     * 在 Mybatis 源码中还有一些缓存的操作，这里暂时剔除掉，以核心流程为主。
     * @param ms
     * @param parameter
     * @param resultHandler
     * @param boundSql
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return doQuery(ms, parameter, resultHandler, boundSql);
    }

    protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return transaction;
    }

    @Override
    public void commit(boolean required) throws SQLException {
        if (closed) {
            throw new RuntimeException("Cannot commit, transaction is already closed");
        }
        if (required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed) {
            if (required) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(forceRollback);
            } finally {
                transaction.close();
            }
        } catch (SQLException e) {
            logger.warn("Unexpected exception on closing transaction.  Cause: " + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }

}
