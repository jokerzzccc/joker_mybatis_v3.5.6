package com.joker.mybatis.executor;

import com.joker.mybatis.executor.statement.StatementHandler;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.ResultHandler;
import com.joker.mybatis.session.RowBounds;
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
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            // 新建一个 StatementHandler
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
            stmt = prepareStatement(handler);
            return handler.update(stmt);
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            // 新建一个 StatementHandler
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, rowBounds, resultHandler, boundSql);
            stmt = prepareStatement(handler);
            // 返回结果
            return handler.query(stmt, resultHandler);
        } finally {
            closeStatement(stmt);
        }
    }

    /**
     * 初始化 StatementHandler 对象
     *
     * @param handler
     * @return
     * @throws SQLException
     */
    private Statement prepareStatement(StatementHandler handler) throws SQLException {
        Statement stmt;
        // 获得 Connection 对象
        Connection connection = transaction.getConnection();
        // 准备语句:创建 Statement 或 PrepareStatement 对象
        stmt = handler.prepare(connection);
        // 设置 SQL 上的参数，例如 PrepareStatement 对象上的占位符
        handler.parameterize(stmt);
        return stmt;
    }

}
