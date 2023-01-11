package com.joker.mybatis.mapping;

import com.joker.mybatis.cache.Cache;
import com.joker.mybatis.cache.decorators.FifoCache;
import com.joker.mybatis.cache.impl.PerpetualCache;
import com.joker.mybatis.reflection.MetaObject;
import com.joker.mybatis.reflection.SystemMetaObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * 缓存构建器，基于装饰者设计模式、建造者模式，进行 Cache 对象的构造。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/10
 */
public class CacheBuilder {

    /**
     * 编号。
     * <p>
     * 目前看下来，是命名空间
     */
    private final String id;
    /**
     * 负责存储的 Cache 实现类
     */
    private Class<? extends Cache> implementation;
    /**
     * Cache 装饰类集合
     * <p>
     * 例如，负责过期的 Cache 实现类
     */
    private final List<Class<? extends Cache>> decorators;
    /**
     * 缓存容器大小
     */
    private Integer size;
    /**
     * 清空缓存的频率。0 代表不清空
     */
    private Long clearInterval;
    /**
     * 是否序列化
     */
    private boolean readWrite;
    /**
     * Properties 对象
     */
    private Properties properties;
    /**
     * 是否阻塞
     */
    private boolean blocking;

    public CacheBuilder(String id) {
        this.id = id;
        this.decorators = new ArrayList<>();
    }

    public CacheBuilder implementation(Class<? extends Cache> implementation) {
        this.implementation = implementation;
        return this;
    }

    public CacheBuilder addDecorator(Class<? extends Cache> decorator) {
        if (decorator != null) {
            this.decorators.add(decorator);
        }
        return this;
    }

    public CacheBuilder size(Integer size) {
        this.size = size;
        return this;
    }

    public CacheBuilder clearInterval(Long clearInterval) {
        this.clearInterval = clearInterval;
        return this;
    }

    public CacheBuilder readWrite(boolean readWrite) {
        this.readWrite = readWrite;
        return this;
    }

    public CacheBuilder blocking(boolean blocking) {
        this.blocking = blocking;
        return this;
    }

    public CacheBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public Cache build() {
        // 设置默认实现类
        setDefaultImplementations();
        // 创建基础 Cache 对象
        Cache cache = newBaseCacheInstance(implementation, id);
        // 设置属性
        setCacheProperties(cache);
        // 如果是 PerpetualCache 类，则进行包装
        if (PerpetualCache.class.equals(cache.getClass())) {
            for (Class<? extends Cache> decorator : decorators) {
                // 使用装饰者模式包装，包装 Cache 对象
                cache = newCacheDecoratorInstance(decorator, cache);
                // 额外属性设置
                setCacheProperties(cache);
            }
        }
        return cache;
    }

    /**
     * 设置默认实现类
     */
    private void setDefaultImplementations() {
        if (implementation == null) {
            implementation = PerpetualCache.class;
            if (decorators.isEmpty()) {
                decorators.add(FifoCache.class);
            }
        }
    }

    private void setCacheProperties(Cache cache) {
        if (properties != null) {
            // 初始化 Cache 对象的属性
            MetaObject metaCache = SystemMetaObject.forObject(cache);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (metaCache.hasSetter(name)) {
                    Class<?> type = metaCache.getSetterType(name);
                    if (String.class == type) {
                        metaCache.setValue(name, value);
                    } else if (int.class == type
                            || Integer.class == type) {
                        metaCache.setValue(name, Integer.valueOf(value));
                    } else if (long.class == type
                            || Long.class == type) {
                        metaCache.setValue(name, Long.valueOf(value));
                    } else if (short.class == type
                            || Short.class == type) {
                        metaCache.setValue(name, Short.valueOf(value));
                    } else if (byte.class == type
                            || Byte.class == type) {
                        metaCache.setValue(name, Byte.valueOf(value));
                    } else if (float.class == type
                            || Float.class == type) {
                        metaCache.setValue(name, Float.valueOf(value));
                    } else if (boolean.class == type
                            || Boolean.class == type) {
                        metaCache.setValue(name, Boolean.valueOf(value));
                    } else if (double.class == type
                            || Double.class == type) {
                        metaCache.setValue(name, Double.valueOf(value));
                    } else {
                        throw new RuntimeException("Unsupported property type for cache: '" + name + "' of type " + type);
                    }
                }
            }
        }
    }

    /**
     * 创建基础 Cache 对象
     *
     * @param cacheClass Cache 类
     * @param id 编号
     * @return Cache 对象
     */
    private Cache newBaseCacheInstance(Class<? extends Cache> cacheClass, String id) {
        Constructor<? extends Cache> cacheConstructor = getBaseCacheConstructor(cacheClass);
        try {
            return cacheConstructor.newInstance(id);
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate cache implementation (" + cacheClass + "). Cause: " + e, e);
        }
    }

    /**
     * 获得 Cache 类的构造方法
     *
     * @param cacheClass Cache 类
     * @return 构造方法
     */
    private Constructor<? extends Cache> getBaseCacheConstructor(Class<? extends Cache> cacheClass) {
        try {
            return cacheClass.getConstructor(String.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid base cache implementation (" + cacheClass + ").  " +
                    "Base cache implementations must have a constructor that takes a String id as a parameter.  Cause: " + e, e);
        }
    }

    /**
     * 包装指定 Cache 对象
     *
     * @param cacheClass 包装的 Cache 类
     * @param base 被包装的 Cache 对象
     * @return 包装后的 Cache 对象
     */
    private Cache newCacheDecoratorInstance(Class<? extends Cache> cacheClass, Cache base) {
        Constructor<? extends Cache> cacheConstructor = getCacheDecoratorConstructor(cacheClass);
        try {
            return cacheConstructor.newInstance(base);
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate cache decorator (" + cacheClass + "). Cause: " + e, e);
        }
    }

    /**
     * 获得方法参数为 Cache 的构造方法
     *
     * @param cacheClass 指定类
     * @return 构造方法
     */
    private Constructor<? extends Cache> getCacheDecoratorConstructor(Class<? extends Cache> cacheClass) {
        try {
            return cacheClass.getConstructor(Cache.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid cache decorator (" + cacheClass + ").  " +
                    "Cache decorators must have a constructor that takes a Cache instance as a parameter.  Cause: " + e, e);
        }
    }

}
