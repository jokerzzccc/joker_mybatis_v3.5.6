package com.joker.mybatis.mapping;

import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.type.JdbcType;

/**
 * <p>
 * 参数映射
 * #{property,javaType=int,jdbcType=NUMERIC}
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/23
 */
public class ParameterMapping {

    private Configuration configuration;

    // property
    private String property;

    // javaType = int
    private Class<?> javaType = Object.class;

    // jdbcType=NUMERIC
    private JdbcType jdbcType;

    private ParameterMapping() {
    }

    public static class Builder {

        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
        }

        public Builder javaType(Class<?> javaType) {
            return this;
        }

        public ParameterMapping build() {
            return parameterMapping;
        }

    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

}
