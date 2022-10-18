package com.joker.mybatis.session;

import com.joker.mybatis.binding.MapperRegistry;
import com.joker.mybatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 配置项：通过配置类包装注册机和SQL语句
 * 串联整个流程的对象保存操作。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public class Configuration {

    /**
     * 映射器注册机
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * 映射的语句，存在Map里
     */
    protected final Map<String, MappedStatement> mappedStatements =  new HashMap<>();

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



}
