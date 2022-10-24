package com.joker.mybatis.executor;

import com.joker.mybatis.executor.statement.StatementHandler;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.ResultHandler;
import com.joker.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * <p>
 * 简单执行器 SimpleExecutor : 继承抽象基类
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/22
 */
public class SimpleExecutor extends BaseExecutor{

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
            Connection connection = transaction.getConnection();
            Statement stmt = handler.prepare(connection);
            handler.parameterize(stmt);
            return handler.query(stmt, resultHandler);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

}
