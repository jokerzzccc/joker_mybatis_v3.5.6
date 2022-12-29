package com.joker.mybatis.transaction;

import com.joker.mybatis.session.TransactionIsolationLevel;
import com.joker.mybatis.transaction.jdbc.JdbcTransaction;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * <p>
 * Creates {@link JdbcTransaction} instances.
 * </p>
 * 事务工厂: 以工厂方法模式包装 JDBC 事务实现，为每一个事务实现都提供一个对应的工厂。与简单工厂的接口包装不同。
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public interface TransactionFactory {

    /**
     * Creates a {@link Transaction} out of an existing connection.
     * <p>
     * 根据 Connection 创建 Transaction
     *
     * @param conn Existing database connection
     * @return Transaction
     */
    Transaction newTransaction(Connection conn);

    /**
     * Creates a {@link Transaction} out of a datasource.
     * <p>
     * 根据数据源和事务隔离级别创建 Transaction
     *
     * @param dataSource DataSource to take the connection from
     * @param level Desired isolation level
     * @param autoCommit Desired autocommit
     * @return Transaction
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);

}
