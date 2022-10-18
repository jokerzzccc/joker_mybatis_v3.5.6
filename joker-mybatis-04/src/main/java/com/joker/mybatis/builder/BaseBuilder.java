package com.joker.mybatis.builder;

import com.joker.mybatis.session.Configuration;

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

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguartion() {
        return configuration;
    }

}
