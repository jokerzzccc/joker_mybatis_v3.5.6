package com.joker.mybatis.session.defaults;

import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.Environment;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * The default implementation for {@link SqlSession}.
 * Note that this class is not Thread-Safe.
 * </p>
 * SqlSession 接口的默认实现类
 *
 * @author jokerzzccc
 * @date 2022/10/17
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;
    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return this.selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        List<T> list = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
        return list.get(0);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}
