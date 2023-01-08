package com.joker.mybatis.transaction.jdbc;

import com.joker.mybatis.session.TransactionIsolationLevel;
import com.joker.mybatis.transaction.Transaction;
import com.joker.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * <p>
 * Creates {@link JdbcTransaction} instances.
 * </p>
 * JdbcTransaction 工厂
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public class JdbcTransactionFactory implements TransactionFactory {

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }

}
