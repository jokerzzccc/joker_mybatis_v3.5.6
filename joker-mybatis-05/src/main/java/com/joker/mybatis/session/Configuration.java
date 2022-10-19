package com.joker.mybatis.session;

import com.joker.mybatis.binding.MapperRegistry;
import com.joker.mybatis.datasource.druid.DruidDataSourceFactory;
import com.joker.mybatis.mapping.Environment;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.transaction.jdbc.JdbcTransaction;
import com.joker.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.joker.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 配置项：串联整个流程的对象保存操作。
 * 整个 Mybatis 的操作都是使用 Configuration 配置项进行串联流程，所以所有内容都会在 Configuration 中进行链接。
 * 通过配置类包装 映射器注册机，SQL语句、环境、类型别名注册机。
 *
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public class Configuration {

    /**
     * 环境
     */
    protected Environment environment;

    /**
     * 映射器注册机
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * 映射的语句，存在Map里
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    /**
     * 类型别名注册机
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
    }

    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
