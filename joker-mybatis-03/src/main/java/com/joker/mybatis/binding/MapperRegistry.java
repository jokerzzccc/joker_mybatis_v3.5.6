package com.joker.mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Mapper 注册机：MapperRegistry 提供包路径的扫描和映射器代理类注册机服务，完成接口对象的代理类注册处理
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/17
 */
public class MapperRegistry {

    private Configuration config;

    public MapperRegistry(Configuration config) {
        this.config = config;
    }

    /**
     * 将已添加的映射器代理加入到 HashMap
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    /**
     * 获取 Mapper(映射器代理类),包装了手动实例化的过程
     *
     * @param type
     * @param sqlSession
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (null == mapperProxyFactory) {
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    /**
     * add Mapper
     *
     * @param type Mapper 类型
     * @param <T>
     */
    public <T> void addMapper(Class<T> type) {
        // Mapper 必须是接口才会注册
        if (type.isInterface()) {
            // 如果重复添加了，报错
            if (hasMapper(type)) {
                throw new RuntimeException("Type " + type + " is already known to the MapperRegistry.");

            }
            // 注册映射器代理工厂
            knownMappers.put(type, new MapperProxyFactory<>(type));
        }
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    /**
     * Adds the mappers.
     * <p>
     * 提供了 ClassScanner.scanPackage 扫描包路径，
     * 调用 addMapper 方法，给接口类创建 MapperProxyFactory 映射器代理类，
     * 并写入到 knownMappers 的 HashMap 缓存中。
     *
     * @param packageName 包路径（包含 Mapper 的包路径)
     */
    public void addMappers(String packageName) {
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }

}
