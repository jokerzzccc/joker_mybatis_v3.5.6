package com.joker.mybatis.session.defaults;

import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.mapping.Environment;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.SqlSession;
import com.joker.mybatis.session.SqlSessionFactory;
import com.joker.mybatis.session.TransactionIsolationLevel;
import com.joker.mybatis.transaction.Transaction;
import com.joker.mybatis.transaction.TransactionFactory;

import java.sql.SQLException;

/**
 * <p>
 * 默认的简单工厂实现，处理开启 SqlSession 时，对 DefaultSqlSession 的创建以及传递 mapperRegistry，
 * 这样就可以在使用 SqlSession 时获取每个代理类的映射器对象了。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/17
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 在 openSession 中开启事务传递给执行器的创建，
     * 关于执行器的创建具体可以参考 configuration.newExecutor 代码，这部分没有太多复杂的逻辑。读者可以参考源码进行学习。
     * 在执行器创建完毕后，则是把参数传递给 DefaultSqlSession，这样就把整个过程串联起来了。
     *
     * @return
     */
    @Override
    public SqlSession openSession() {
        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, false);
            // 创建执行器
            final Executor executor = configuration.newExecutor(tx);
            // 创建 DefaultSqlSession
            return new DefaultSqlSession(configuration, executor);

        } catch (Exception e) {
            try {
                assert tx != null;
                tx.close();
            } catch (SQLException ignore) {
            }
            throw new RuntimeException("Error opening session.  Cause: " + e);
        }

    }

}
