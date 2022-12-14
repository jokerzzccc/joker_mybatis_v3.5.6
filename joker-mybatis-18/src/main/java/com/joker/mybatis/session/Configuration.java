package com.joker.mybatis.session;

import com.joker.mybatis.binding.MapperRegistry;
import com.joker.mybatis.cache.Cache;
import com.joker.mybatis.cache.decorators.FifoCache;
import com.joker.mybatis.cache.impl.PerpetualCache;
import com.joker.mybatis.datasource.druid.DruidDataSourceFactory;
import com.joker.mybatis.datasource.pooled.PooledDataSourceFactory;
import com.joker.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import com.joker.mybatis.executor.CachingExecutor;
import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.executor.SimpleExecutor;
import com.joker.mybatis.executor.keygen.KeyGenerator;
import com.joker.mybatis.executor.parameter.ParameterHandler;
import com.joker.mybatis.executor.resultset.DefaultResultSetHandler;
import com.joker.mybatis.executor.resultset.ResultSetHandler;
import com.joker.mybatis.executor.statement.PreparedStatementHandler;
import com.joker.mybatis.executor.statement.StatementHandler;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.Environment;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.mapping.ResultMap;
import com.joker.mybatis.plugin.Interceptor;
import com.joker.mybatis.plugin.InterceptorChain;
import com.joker.mybatis.reflection.MetaObject;
import com.joker.mybatis.reflection.factory.DefaultObjectFactory;
import com.joker.mybatis.reflection.factory.ObjectFactory;
import com.joker.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import com.joker.mybatis.reflection.wrapper.ObjectWrapperFactory;
import com.joker.mybatis.scripting.LanguageDriver;
import com.joker.mybatis.scripting.LanguageDriverRegistry;
import com.joker.mybatis.scripting.xmltags.XMLLanguageDriver;
import com.joker.mybatis.transaction.Transaction;
import com.joker.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.joker.mybatis.type.TypeAliasRegistry;
import com.joker.mybatis.type.TypeHandlerRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * ??????????????????????????????????????????????????????
 * ?????? Mybatis ????????????????????? Configuration ????????????????????????????????????????????????????????? Configuration ??????????????????
 * ????????????????????? ?????????????????????SQL??????????????????????????????????????????
 *
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public class Configuration {

    /**
     * ??????
     */
    protected Environment environment;
    protected boolean useGeneratedKeys = false;
    /**
     * ???????????????????????????cacheEnabled = true/false
     */
    protected boolean cacheEnabled = true;
    /**
     * ?????????????????????????????????????????? SESSION
     */
    protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;

    /**
     * ??????????????????
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);
    /**
     * ????????????????????????Map???
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();
    /**
     * ??????,??????Map???
     */
    protected final Map<String, Cache> caches = new HashMap<>();
    /**
     * ?????????????????????Map???
     */
    protected final Map<String, ResultMap> resultMaps = new HashMap<>();
    protected final Map<String, KeyGenerator> keyGenerators = new HashMap<>();

    // ??????????????????
    protected final InterceptorChain interceptorChain = new InterceptorChain();

    /**
     * ?????????????????????
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();
    /**
     * ????????????????????????
     */
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    /**
     * ????????????????????????????????????
     */
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    protected final Set<String> loadedResources = new HashSet<>();
    protected String databaseId;

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);

        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);

        typeAliasRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
        typeAliasRegistry.registerAlias("FIFO", FifoCache.class);

        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
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

    // ????????????????????????
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    // ???????????????
    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory);
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public LanguageDriverRegistry getLanguageRegistry() {
        return languageRegistry;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * ????????????????????????
     *
     * @param executor
     * @param mappedStatement
     * @param boundSql
     * @return
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, resultHandler, rowBounds, boundSql);
    }

    /**
     * ???????????????
     *
     * @param transaction
     * @return
     */
    public Executor newExecutor(Transaction transaction) {
        Executor executor = new SimpleExecutor(this, transaction);
        // ??????????????????????????? CachingExecutor(?????????????????????)???????????????
        if (cacheEnabled) {
            executor = new CachingExecutor(executor);
        }
        return executor;

    }

    /**
     * ?????????????????????
     *
     * @param executor
     * @param mappedStatement
     * @param parameter
     * @param resultHandler
     * @param boundSql
     * @return
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        // ????????????????????????Mybatis ?????????????????? STATEMENT???PREPARED???CALLABLE ?????????????????????????????????????????????
        StatementHandler statementHandler = new PreparedStatementHandler(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
        // ???????????????????????????
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;

    }

    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        // ?????????????????????
        ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
        // ?????????????????????????????????????????????????????????????????????????????? interceptorChain.pluginAll(parameterHandler);
        return parameterHandler;
    }

    public LanguageDriver getDefaultScriptingLanguageInstance() {
        return languageRegistry.getDefaultDriver();
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ResultMap getResultMap(String id) {
        return resultMaps.get(id);
    }

    public void addResultMap(ResultMap resultMap) {
        resultMaps.put(resultMap.getId(), resultMap);
    }

    public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
        keyGenerators.put(id, keyGenerator);
    }

    public KeyGenerator getKeyGenerator(String id) {
        return keyGenerators.get(id);
    }

    public boolean hasKeyGenerator(String id) {
        return keyGenerators.containsKey(id);
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public void addInterceptor(Interceptor interceptorInstance) {
        interceptorChain.addInterceptor(interceptorInstance);
    }

    public LocalCacheScope getLocalCacheScope() {
        return localCacheScope;
    }

    public void setLocalCacheScope(LocalCacheScope localCacheScope) {
        this.localCacheScope = localCacheScope;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public void addCache(Cache cache) {
        caches.put(cache.getId(), cache);
    }

    public Cache getCache(String id) {
        return caches.get(id);
    }

}
