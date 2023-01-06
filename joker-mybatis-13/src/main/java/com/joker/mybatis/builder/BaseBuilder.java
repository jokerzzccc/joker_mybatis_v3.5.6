package com.joker.mybatis.builder;

import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.type.TypeAliasRegistry;
import com.joker.mybatis.type.TypeHandler;
import com.joker.mybatis.type.TypeHandlerRegistry;

/**
 * <p>
 * Builder(构建器)的基类，建造者模式
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;
    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    protected Class<?> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }

    /**
     * 根据别名解析 Class 类型别名注册/事务管理器别名
     *
     * @param alias
     * @return
     */
    protected Class<?> resolveClass(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return resolveAlias(alias);
        } catch (Exception e) {
            throw new RuntimeException("Error resolving class. Cause: " + e, e);
        }
    }

    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
        if (typeHandlerType == null) {
            return null;
        }
        return typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
    }

}
