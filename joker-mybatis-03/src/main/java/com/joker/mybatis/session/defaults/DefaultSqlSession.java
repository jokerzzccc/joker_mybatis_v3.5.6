package com.joker.mybatis.session.defaults;

import com.joker.mybatis.binding.MapperRegistry;
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

    /**
     * 映射器注册机
     */
    private MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return (T) ("你被代理了！" + "方法：" + statement + " 入参：" + parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return mapperRegistry.getMapper(type, this);
    }

}
