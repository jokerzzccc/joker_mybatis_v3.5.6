package com.joker.mybatis.executor.statement;

import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * <p>
 * 简单语句处理器（statement）
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/22
 */
public class SimpleStatementHandler  extends BaseStatementHandler{

    public SimpleStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameterObject, resultHandler, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        // N/A
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        String sql = boundSql.getSql();
        statement.execute(sql);
        return resultSetHandler.handleResultSets(statement);
    }

}
