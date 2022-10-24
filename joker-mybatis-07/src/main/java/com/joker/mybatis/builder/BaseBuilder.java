package com.joker.mybatis.builder;

import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.type.TypeAliasRegistry;

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

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguartion() {
        return configuration;
    }

}
