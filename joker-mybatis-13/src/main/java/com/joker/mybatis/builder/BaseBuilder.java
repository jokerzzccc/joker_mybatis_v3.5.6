package com.joker.mybatis.builder;

import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.type.TypeAliasRegistry;
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

}
