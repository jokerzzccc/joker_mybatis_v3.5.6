package com.joker.mybatis.session;

import com.joker.mybatis.builder.xml.XMLConfigBuilder;
import com.joker.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * <p>
 * SqlSessionFactoryBuilder 是作为整个 Mybatis 的入口类，通过指定解析XML的IO，引导整个流程的启动。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        final XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    public SqlSessionFactory build(Configuration configuration) {
         return new DefaultSqlSessionFactory(configuration);
    }
}
