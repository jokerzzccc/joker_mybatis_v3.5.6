package com.joker.mybatis.builder;

import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.ParameterMapping;
import com.joker.mybatis.mapping.SqlSource;
import com.joker.mybatis.session.Configuration;

import java.util.List;

/**
 * <p>
 * 静态的 SqlSource 实现类
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/23
 */
public class StaticSqlSource implements SqlSource {

    private String sql;
    private List<ParameterMapping> parameterMappings;
    private Configuration configuration;

    public StaticSqlSource(Configuration configuration, String sql) {
        this(configuration, sql, null);
    }

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration, sql,parameterMappings, parameterObject);
    }

}
