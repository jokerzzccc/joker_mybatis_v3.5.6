package com.joker.mybatis.session.defaults;

import com.joker.mybatis.binding.MapperRegistry;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.SqlSession;
import com.joker.mybatis.session.SqlSessionFactory;

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

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }

}
