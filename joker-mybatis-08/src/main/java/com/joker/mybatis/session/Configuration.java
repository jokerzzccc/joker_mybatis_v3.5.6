package com.joker.mybatis.session;

import com.joker.mybatis.binding.MapperRegistry;
import com.joker.mybatis.datasource.druid.DruidDataSourceFactory;
import com.joker.mybatis.datasource.pooled.PooledDataSourceFactory;
import com.joker.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.executor.SimpleExecutor;
import com.joker.mybatis.executor.resultset.DefaultResultSetHandler;
import com.joker.mybatis.executor.resultset.ResultSetHandler;
import com.joker.mybatis.executor.statement.PreparedStatementHandler;
import com.joker.mybatis.executor.statement.StatementHandler;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.Environment;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.transaction.Transaction;
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
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);

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

    /**
     * 创建结果集处理器
     *
     * @param executor
     * @param mappedStatement
     * @param boundSql
     * @return
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    /**
     * 创建执行器
     *
     * @param transaction
     * @return
     */
    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    /**
     * 创建语句处理器
     *
     * @param executor
     * @param mappedStatement
     * @param parameter
     * @param resultHandler
     * @param boundSql
     * @return
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler, boundSql);
    }

}
