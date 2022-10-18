package com.joker.mybatis.session.defaults;

import com.joker.mybatis.binding.MapperRegistry;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.SqlSession;

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

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        return (T) ("你被代理了！" + "\n方法：" + statement + "\n入参：" + parameter + "\n待执行SQL：" + mappedStatement.getSql());
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
