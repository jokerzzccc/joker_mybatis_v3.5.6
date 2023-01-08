package com.joker.mybatis.executor.statement;

import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.executor.parameter.ParameterHandler;
import com.joker.mybatis.executor.resultset.ResultSetHandler;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.ResultHandler;
import com.joker.mybatis.session.RowBounds;
import com.sun.rowset.internal.Row;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p>
 * 语句处理器基类: 将参数信息、结果信息进行封装处理。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/22
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;
    protected final Executor executor;
    protected final MappedStatement mappedStatement;

    protected final Object parameterObject;
    protected final ResultSetHandler resultSetHandler;
    protected final ParameterHandler parameterHandler;

    protected final RowBounds rowBounds;
    protected BoundSql boundSql;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject,
            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.rowBounds = rowBounds;

        // step-11 新增判断，因为 update 不会传入 boundSql 参数，所以这里要做初始化处理
        if (boundSql == null) {
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }

        this.boundSql = boundSql;

        // 参数和结果集
        this.parameterObject = parameterObject;
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, rowBounds, resultHandler, boundSql);
        this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    /**
     * 包括定义实例化抽象方法，这个方法交由各个具体的实现子类进行处理。
     * 包括；SimpleStatementHandler 简单语句处理器和 PreparedStatementHandler 预处理语句处理器。
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    @Override
    public Statement prepare(Connection connection) throws SQLException {
        Statement statement = null;
        try {
            // 实例化 Statement
            statement = instantiateStatement(connection);
            // 参数设置，可以被抽取，提供配置
            statement.setQueryTimeout(350);
            statement.setFetchSize(10000);
            return statement;
        } catch (Exception e) {
            throw new RuntimeException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

}
